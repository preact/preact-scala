package com.preact.api.model

import play.api.libs.json.Json

  case class Person(email: String,
                    first_name: Option[String] = None,
                    last_name: Option[String] = None,
                    unique_id: Option[String] = None)

  object Person {
    implicit val format = Json.format[Person]
  }

  case class Event(name: String,
                   `type`: String,
                   timestamp: Option[Long] = None,
                   experiment_name: Option[String] = None,
                   experiment_id: Option[String] = None,
                   email_name: Option[String] = None,
                   email_id: Option[String] = None,
                   campaign_name: Option[String] = None,
                   campaign_id: Option[String] = None,
                   extras: Map[String, String] = Map())

  object Event {
    implicit val eventFormat = Json.format[Event]
  }

  case class Source(name: String)

  object Source {
    implicit val format = Json.format[Source]
  }

  case class EventRecord(person: Person, event: Event, source: Source)

  object EventRecord {
    implicit val format = Json.format[EventRecord]
  }


  case class EventsBulkV3(data: Seq[EventRecord])

  object EventsBulkV3 {
    implicit val format = Json.format[EventsBulkV3]
  }

  case class PreactResponse(success: Boolean)

  object PreactResponse {
    implicit val format = Json.format[PreactResponse]
  }
