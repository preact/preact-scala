package com.preact.api.model

import play.api.libs.json._

object EventTypes extends Enumeration {
  type EventTypes = Value
  val sent          = Value("sent")
  val hard_bounce   = Value("hard_bounce")
  val soft_bounce   = Value("soft_bounce")
  val open          = Value("open")
  val click         = Value("click")
  val print         = Value("print")
  val forward       = Value("forward")
  val spam_report   = Value("spam_report")
  val status_change = Value("status_change")
  val dropped       = Value("dropped")
  val delivered     = Value("delivered")
  val other         = Value("other")

  implicit val myEnumFormat = new Format[EventTypes] {
    def reads(json: JsValue) = JsSuccess(EventTypes.withName(json.as[String]))

    def writes(myEnum: EventTypes) = JsString(myEnum.toString)
  }
}