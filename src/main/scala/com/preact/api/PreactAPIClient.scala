package com.preact.api

import java.net.ConnectException
import java.util.Base64

import dispatch._
import org.slf4j.LoggerFactory
import play.api.libs.json.{Format, Json}

import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.util.control.NonFatal
import scala.util.{Failure, Success, Try}


class PreactAPIClient(client: dispatch.Http, projectCode: String, secret: String, apiUrl: String) {
  private[this] implicit val ctx: ExecutionContext = ExecutionContext.fromExecutor(new SingleThreadedExecutor)
  private lazy val logger  = LoggerFactory.getLogger(getClass)
  private def buildSecret = {
    Base64.getEncoder.encodeToString(s"$projectCode:$secret".getBytes)
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

  override def toString = s"{apiUrl:$apiUrl,projectCode:$projectCode}"
}


