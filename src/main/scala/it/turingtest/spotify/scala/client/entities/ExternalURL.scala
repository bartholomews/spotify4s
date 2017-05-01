package it.turingtest.spotify.scala.client.entities

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Reads}

case class ExternalURL(spotify: Option[String], value: Option[String])

object ExternalURL {
  implicit val externalURLReads: Reads[ExternalURL] = (
    (JsPath \ "spotify").readNullable[String] and
      (JsPath \ "value").readNullable[String]
    )(ExternalURL.apply _)
}
