package it.turingtest.spotify.scala.client.entities

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Reads}

case class TracksURL(href: String, total: Long)

object TracksURL {
  implicit val tracksURLReads: Reads[TracksURL] = (
    (JsPath \ "href").read[String] and
      (JsPath \ "total").read[Long]
    )(TracksURL.apply _)
}
