package it.turingtest.spotify.scala.client.entities

import play.api.libs.json.{JsPath, Reads}
import play.api.libs.functional.syntax._

case class SimpleArtist
(
external_urls: ExternalURL,
href: Option[String], // link to full object Artist
id: Option[String],
name: String,
uri: Option[String]
) { val objectType = "artist" }

object SimpleArtist {
  implicit val simpleArtistReads: Reads[SimpleArtist] = (
    (JsPath \ "external_urls").read[ExternalURL] and
      (JsPath \ "href").readNullable[String] and
      (JsPath \ "id").readNullable[String] and
      (JsPath \ "name").read[String] and
      (JsPath \ "uri").readNullable[String]
    )(SimpleArtist.apply _)
}
