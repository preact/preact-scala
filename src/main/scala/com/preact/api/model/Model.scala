package com.preact.api.model

import com.preact.api.EventTypes
import EventTypes.EventTypes
import play.api.libs.json.Json

case class Person(email: String,
                  name: Option[String] = None,
                  created_at: Option[Double] = None,
                  uid: Option[String] = None,
                  properties: Map[String, String] = Map())

object Person {
  implicit val personFormat = Json.format[Person]
}

case class Account(id: String, name: String)

object Account {
  implicit val accountFormat = Json.format[Account]
}

case class Event(name: EventTypes,
                 timestamp: Long,
                //use PreactEventExtras
                 extras: Map[String, String] = Map())

object Event {
  implicit val eventFormat = Json.format[Event]
}

case class ActionEvent(person: Person,
                       event: Event,
                       account: Account)

object ActionEvent {
  implicit val actionEventFormat = Json.format[ActionEvent]
}

case class EventsBulk(data: Seq[ActionEvent])

object EventsBulk {
  implicit val bulkV1Format = Json.format[EventsBulk]
}

case class PreactResponse(success: Boolean)

object PreactResponse {
  implicit val format = Json.format[PreactResponse]
}

/**
 * Since the extras fields are not always the same, we cannot use case classes
 * so here are some predefined extra fields to have a unified key across 3rd party implementation
 * when creating Event(name,timestamp, extras) the extras map could be created like this
 * val extras = mutable.Map() //ur immutable if you want
 * exstras.put(PreactEventExtras.eventName.toString, theValue)
 */
object Extras extends Enumeration {
  type Extras = Value
  val eventName =  Value("event_name")
  val experimentName =  Value("experiment_name")
  val experimentId =  Value("experiment_id")
  val email =  Value("email_name")
  val emailId =  Value("email_id")
  val campaignName =  Value("campaign_name")
  val campaignId =  Value("campaign_id")
  val source =  Value("source")
}