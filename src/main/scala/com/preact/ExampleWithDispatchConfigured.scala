package com.preact

import com.preact.api._
import com.preact.api.model.v1._
import play.api.libs.json.Json

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.control.NonFatal
import scala.util.{Failure, Success, Try}


object ExampleWithDispatchConfigured extends App {

  private val code: String = ""
  private val secret: String = ""
  private val endpoint: String = ""

  val httpClient = dispatch.Http {
    import com.ning.http.client._
    val builder = new AsyncHttpClientConfig.Builder()
    builder.setCompressionEnforced(true).setAllowPoolingConnections(true).setRequestTimeout(5000)
    new AsyncHttpClient(builder.build())
  }
  val preactClient = new PreactAPIClient(httpClient, code, secret, endpoint)

  val person = Person("myEmail@whatever.com")
  val account = Account("01234", "Valid Preact Account")

  val events = List(Event("eventName1", Some(account)), Event("eventName2", Some(account)))


  val eventPushTasks: List[Future[Try[String]]] =
    for (event <- events)
      yield preactClient.push(ActionEvent(person, event, account))

  val eventPushTasksHandler = Future.sequence(eventPushTasks)

  val successfulTasks = eventPushTasksHandler.map(_.collect { case Success(x: String) => Json.parse(x).as[PreactResponse] })

  val unsuccessfulTasks = eventPushTasksHandler.map(_.collect { case Failure(NonFatal(t)) => t })


  successfulTasks.onSuccess { case response => response foreach println}

  unsuccessfulTasks.onSuccess { case t => t foreach println }


  Future.sequence(Seq(successfulTasks, unsuccessfulTasks)).onComplete {
    case _ => println("we have to explicitly shut down the client")
      httpClient.shutdown()
      println("client stopped, program should exit")
  }

}
