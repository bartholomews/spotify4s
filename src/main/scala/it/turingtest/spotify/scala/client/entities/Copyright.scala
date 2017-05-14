package it.turingtest.spotify.scala.client.entities

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Reads}

/**
  * @see https://developer.spotify.com/web-api/object-model/#copyright-object
  *
  * @param text The copyright text for this album.
  *
  * @param copyrightType The type of copyright:
  *                      C = the copyright,
  *                      P = the sound recording (performance) copyright.
  */
case class Copyright(text: String, copyrightType: String)

object Copyright {
  implicit val copyrightReads: Reads[Copyright] = (
    (JsPath \ "text").read[String] and
      (JsPath \ "type").read[String]
    )(Copyright.apply _)
}
