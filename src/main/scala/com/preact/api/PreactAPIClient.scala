package com.preact.api

import java.net.ConnectException
import java.util.Base64

import dispatch._
import org.slf4j.LoggerFactory
import play.api.libs.json.{Format, Json}

import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.util.control.NonFatal
import scala.util.{Failure, Success, Try}

/**
 * see FutureAndTryCombinations worksheet to choose what push method to use
 */
class PreactAPIClient(client: dispatch.Http, projectCode: String, secret: String, apiUrl: String) {
  private[this] val executor: SingleThreadedExecutor = new SingleThreadedExecutor
  private[this] implicit val ctx: ExecutionContext = ExecutionContext.fromExecutor(executor)
  private lazy val logger  = LoggerFactory.getLogger(getClass)
  private def buildSecret = {
    Base64.getEncoder.encodeToString(s"$projectCode:$secret".getBytes)
  }

  def closeContext() = {
    executor.shutdown()
  }

  def push[IN](in: IN, retries: Int = 3)(implicit fmtIn: Format[IN]): Future[Try[String]] = {
    Try {
    val post = url(apiUrl).POST
        .setBody(Json.toJson(in).toString())
        .setContentType("application/json", "UTF-8")
        .addHeader("Authorization", s"Basic $buildSecret")
        .addHeader("Accept", "application/json")
      logger.debug("|PP-OutsideCtx|")
      client(post OK as.String)(ctx).either
    } match {
      case Failure(NonFatal(t)) => Promise.successful(Failure(t)).future

      case Success(result) => result.flatMap {
        case Right(a) => Promise.successful(Success(a.asInstanceOf[String])).future

        case Left(t: ConnectException) => if (retries > 1) push(in, retries - 1) else Promise.successful(Failure(new RuntimeException("Connect Exception Retry failed!"))).future

        //you can easily handle status codes
        case Left(StatusCode(code)) => code match {
          case all => Promise.successful(Failure(StatusCode(all))).future
        }

        case Left(NonFatal(e)) => Promise.successful(Failure(e)).future

        //fatal
        case Left(throwable: Throwable) => throw throwable
      }
    }
  }

  def simpleAsyncPush[IN](in: IN, retries: Int = 3)(implicit fmtIn: Format[IN]): Future[String] = {
    Try {
      val post = url(apiUrl).POST
        .setBody(Json.toJson(in).toString())
        .setContentType("application/json", "UTF-8")
        .addHeader("Authorization", s"Basic $buildSecret")
        .addHeader("Accept", "application/json")
      logger.debug("|PP-OutsideCtx|")
      client(post OK as.String)(ctx).either
    } match {
      case Failure(NonFatal(t)) => Promise.failed(t).future

      case Success(result) => result.flatMap {
        case Right(a) => Promise.successful(a.asInstanceOf[String]).future

        case Left(t: ConnectException) => if (retries > 1) simpleAsyncPush(in, retries - 1) else Promise.failed(new RuntimeException("Connect Exception Retry failed!")).future

        //you can easily handle status codes
        case Left(StatusCode(code)) => code match {
          case all => Promise.failed(StatusCode(all)).future
        }

        case Left(NonFatal(e)) => Promise.failed(e).future

        //fatal
        case Left(throwable: Throwable) => throw throwable
      }
    }
  }

    def simplePush[IN](in: IN, retries: Int = 3)(implicit fmtIn: Format[IN]): Try[String] = {
    Try {
      val post = url(apiUrl).POST
        .setBody(Json.toJson(in).toString())
        .setContentType("application/json", "UTF-8")
        .addHeader("Authorization", s"Basic $buildSecret")
        .addHeader("Accept", "application/json")
      logger.debug("|PP-OutsideCtx|")
      client(post OK as.String)(ctx).either.apply()
    } match {
      case Failure(NonFatal(t)) => Failure(t)

      case Success(result) => result match {
        case Right(a) => Success(a.asInstanceOf[String])

        case Left(t: ConnectException) => if (retries > 1) simplePush(in, retries - 1) else Failure(new RuntimeException("Connect Exception Retry failed!"))

        //you can easily handle status codes
        case Left(StatusCode(code)) => code match {
          case all => Failure(StatusCode(all))
        }

        case Left(NonFatal(e)) => Failure(e)

        //fatal
        case Left(throwable: Throwable) => throw throwable
      }
    }
  }


  override def toString = s"{apiUrl:$apiUrl,projectCode:$projectCode}"
}


