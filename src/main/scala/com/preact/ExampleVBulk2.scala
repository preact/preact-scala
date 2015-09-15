package com.preact

import com.preact.api._
import com.preact.api.model.v2.{EventsBulkV3, EventRecord, Source}
import com.preact.api.model.{v2, Event}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success, Try}

object ExampleVBulk2 extends App {

  private val code: String = "wgoihn1ao4"
  private val secret: String = "1dedwby6or"
  private val endpoint: String = "https://api.preact.io/api/v2/events/bulk"

  val preactClient = new PreactAPIClient(dispatch.Http, code, secret, endpoint)

  val person = v2.Person("myEmail1@whatever.com")
  val source = Source("Silverpop")
  val events = List(v2.Event("Open",EventTypes.open), v2.Event("Sent",EventTypes.sent))



  val eventRecords = for(eventData<-events) yield EventRecord(person,eventData,source)
  val bulkEvents = EventsBulkV3(eventRecords)
  val bulkRes = preactClient push bulkEvents
  bulkRes.onComplete(println)


  Await.result(bulkRes, 5.seconds)
  dispatch.Http.shutdown()


}
