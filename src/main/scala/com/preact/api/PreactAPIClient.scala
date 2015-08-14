package com.preact.api

import java.net.ConnectException
import java.util.Base64

import dispatch.Defaults._
import dispatch._
import play.api.libs.json.{Format, Json}

import scala.concurrent.Future
import scala.util.control.NonFatal
import scala.util.{Failure, Success, Try}


class PreactAPIClient(client: dispatch.Http, projectCode: String, secret: String, apiUrl: String) {

  private def buildSecret = {
    Base64.getEncoder.encodeToString(s"$projectCode:$secret".getBytes)
  }

  def push[IN](in: IN, retries: Int = 3)(implicit fmtIn: Format[IN]): Future[Try[String]] = {
    val post = url(apiUrl).POST
      .setBody(Json.toJson(in).toString())
      .setContentType("application/json", "UTF-8")
      .addHeader("Authorization", s"Basic $buildSecret")
      .addHeader("Accept", "application/json")
    Try {//Try because dispatch might throw exceptions and we need to close the client gracefully
      client(post OK as.String).either
    } match {
      case Failure(NonFatal(t)) => client.shutdown();throw  t

      case Success(result) => result.flatMap {
        case Right(a) => Future(Success(a.asInstanceOf[String]))

        case Left(t: ConnectException) => if (retries > 1) push(in, retries - 1) else Future(Failure(new RuntimeException("Connect Exception Retry failed!")))

        //you can easily handle status codes
        case Left(StatusCode(code)) => code match {
          case all => Future(Failure(StatusCode(all)))
        }

        case Left(NonFatal(e)) => Future(Failure(e))

        //fatal
        case Left(throwable: Throwable) => throw throwable
      }
    }
  }


}


