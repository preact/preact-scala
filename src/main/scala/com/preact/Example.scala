package com.preact

import com.preact.api._
import com.preact.api.model.v1._
import play.api.libs.json.Json

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.util.control.NonFatal
import scala.util.{Failure, Success, Try}


object Example extends App {

  private val code: String = ""
  private val secret: String = ""
  private val endpoint: String = ""

  val preactClient = new PreactAPIClient(dispatch.Http, code, secret, endpoint)

  val person = Person("myEmail@whatever.com")
  val account = Account("01234", "Valid Preact Account")

  val events = List(Event("eventName1"), Event("eventName2"))

  val eventPushTasks: List[Future[Try[String]]] =
    for (event <- events)
      yield preactClient.push(ActionEvent(person, event, account))

  val eventPushTasksHandler = Future.sequence(eventPushTasks)

  val successfulTasks = eventPushTasksHandler.map(_.collect { case Success(x: String) => Json.parse(x).as[PreactResponse] })

  val unsuccessfulTasks = eventPushTasksHandler.map(_.collect { case Failure(NonFatal(t)) => t })


  successfulTasks.onSuccess { case response => response foreach println }

  unsuccessfulTasks.onSuccess { case t => t foreach println }


  //case A is possible because httpClient's executor service is still running
  Future.sequence(Seq(successfulTasks, unsuccessfulTasks)).onComplete {
    case _ => println("We have to explicitly shut down the client")
      dispatch.Http.shutdown()
      println("client stopped, program should exit")
  }

  //case B :sync wait
  //Await.result(successfulTasks, 30 seconds)
  //dispatch.Http.shutdown()


}
