package it.turingtest.spotify.scala.client.entities

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Reads}

/**
  * @see https://developer.spotify.com/web-api/object-model/#external-url-object
  *
  * [@param] {key}
  * @param value
  */
case class ExternalURL(spotify: Option[String], value: Option[String])

// TODO http://stackoverflow.com/questions/27732552/how-to-parse-json-with-variable-keys-in-scala-play
object ExternalURL {
  implicit val externalURLReads: Reads[ExternalURL] = (

    (JsPath \ "spotify").readNullable[String] and
      (JsPath \ "value").readNullable[String]
    )(ExternalURL.apply _)
}
