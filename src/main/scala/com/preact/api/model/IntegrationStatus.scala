package com.preact.api.model

import play.api.libs.json.Json

case class IntegrationStatus(connected:Boolean, messages:Seq[String])
object IntegrationStatus {
  implicit val frmt = Json.format[IntegrationStatus]
}