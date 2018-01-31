package it.turingtest.spotify.scala.client.entities

import it.turingtest.spotify.scala.client.SearchApi._
import play.api.libs.json.{Json, Reads}

case class Artist(
  external_urls: ExternalURL,
  followers: Followers,
  genres: Seq[String],
  href: Option[String], // link to full object Artist
  id: Option[String],
  images: Seq[Image],
  name: String,
  popularity: Int,
  itemType: ItemType, // type
  uri: Option[String]
)

object Artist {
  implicit val reader: Reads[Artist] = Json.reads[Artist]
}
