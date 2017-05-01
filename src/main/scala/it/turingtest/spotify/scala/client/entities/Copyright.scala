package it.turingtest.spotify.scala.client.entities

import play.api.libs.json.{JsPath, Reads}
import play.api.libs.functional.syntax._

case class Copyright(text: String, copyrightType: String)

object Copyright {
  implicit val copyrightReads: Reads[Copyright] = (
    (JsPath \ "text").read[String] and
      (JsPath \ "type").read[String]
    )(Copyright.apply _)
}
