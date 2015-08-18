package com.preact.api

import java.time.LocalDateTime

import play.api.libs.json.{Format, Json}

case class Person(email: String,
                  name: Option[String] = None,
                  created_at: Option[Double] = None,
                  uid: Option[String] = None,
                  twitter_id: Option[String] = None,
                  facebook_id: Option[String] = None,
                  stripe_id: Option[String] = None,
                  properties: Option[Map[String, String]] = None)

object Person {
  implicit val personFormat = Json.format[Person]
}

case class Account(id: String,
                   name: String,
                   license_type: Option[String] = None,
                   license_count: Option[String] = None,
                   license_renewal: Option[LocalDateTime] = None,
                   license_value: Option[Int] = None,
                   license_mrr: Option[Double] = None,
                   license_duration: Option[Int] = None,
                   license_status: Option[String] = None,
                   customer_since: Option[LocalDateTime] = None,
                   trial_start: Option[LocalDateTime] = None,
                   trial_end: Option[LocalDateTime] = None,
                   account_manager_name: Option[String] = None,
                   account_manager_email: Option[String] = None
                    )

object Account {
  implicit val accountFormat = Json.format[Account]
}

case class Event(name: String,
                 account: Option[Account] = None,
                 timestamp: Option[Long] = None,
                 revenue: Option[Int] = None,
                 note: Option[String] = None,
                 campaign: Option[String] = None,
                 target_id: Option[String] = None,
                 extras: Option[Map[String, String]] = None)

object Event {
  implicit val eventFormat = Json.format[Event]
}


case class ActionEvent(person: Person,
                       event: Event,
                       account: Account)

object ActionEvent {
  implicit val actionEventFormat = Json.format[ActionEvent]
}

case class EventsBulkV1(data: Seq[ActionEvent])

object EventsBulkV1 {
  implicit val bulkV1Format: Format[EventsBulkV1] = Json.format[EventsBulkV1]
}

case class EventsBulkV2(person: Person, events: Seq[Event])

object EventsBulkV2 {
  implicit val bulkV2Format = Json.format[EventsBulkV2]
}


case class PreactResponse(success: Boolean)

object PreactResponse {
  implicit val preactResponseFormat = Json.format[PreactResponse]
}
