package com.preact

import com.preact.api._
import com.preact.api.model.{Event, Person, _}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success, Try}

object ExampleV2 extends App {

  private val code: String = "wgoihn1ao4"
  private val secret: String = "1dedwby6or"
  private val endpoint: String = "https://api.preact.io/api/v2/events"

  val preactClient = new PreactAPIClient(dispatch.Http, code, secret, endpoint)

  val person = Person("myEmail@whatever.com")
  val source = Source("Silverpop")

  val eventsData = List(Event("Open",EventTypes.open), Event("Sent",EventTypes.sent))

  val eventPushTasks: List[Future[Try[String]]] =
    for (event <- eventsData)
      yield preactClient.push(EventRecord(person, event, source))

  val res = for(seq <- Future.sequence(eventPushTasks)) yield seq.span(_.isSuccess)


  res.onComplete {
    case Success(t) =>
      println("Success:");t._1.foreach(println)
      println("Failures:");t._2.foreach(println)
    case Failure(t) => println(t)
  }

  Await.result(res, 10.seconds)

  dispatch.Http.shutdown()


}
