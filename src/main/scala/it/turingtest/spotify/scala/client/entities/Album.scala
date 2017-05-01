package it.turingtest.spotify.scala.client.entities

import play.api.libs.json.{JsPath, Reads}
import play.api.libs.functional.syntax._

trait Album

case class FullAlbum
(
  album_type: Option[String],
  artists: List[SimpleArtist],
  available_markets: List[String],
  external_urls: ExternalURL,
  href: Option[String],
  id: Option[String],
  images: List[Image],
  name: String,
  uri: Option[String],
  copyrights: List[Copyright],
  external_ids: ExternalID,
  genres: List[String],
  label: String,
  popularity: Int,
  release_date: String,
  release_date_precision: String,
  tracks: Page[SimpleTrack]) extends Album { val objectType = "album" }

object FullAlbum {
  implicit val fullAlbumReads: Reads[FullAlbum] = AlbumReads.fullAlbum (FullAlbum.apply _)
}

case class SimpleAlbum
(
  album_type: Option[String],
  artists: List[SimpleArtist],
  available_markets: List[String],
  external_urls: ExternalURL,
  href: Option[String], // the link to full information Album object
  id: Option[String],
  images: List[Image],
  name: String,
  uri: Option[String]) extends SpotifyObject { override val objectType = "album" }

object SimpleAlbum {
  implicit val simpleAlbumReads: Reads[SimpleAlbum] = AlbumReads.simpleAlbum (SimpleAlbum.apply _)
}

object AlbumReads {

  val simpleAlbum = {
    (JsPath \ "album_type").readNullable[String] and
      (JsPath \ "artists").read[List[SimpleArtist]] and
      (JsPath \ "available_markets").read[List[String]] and
      (JsPath \ "external_urls").read[ExternalURL] and
      (JsPath \ "href").readNullable[String] and
      (JsPath \ "id").readNullable[String] and
      (JsPath \ "images").read[List[Image]] and
      (JsPath \ "name").read[String] and
      (JsPath \ "uri").readNullable[String]
  }
  val fullAlbum = {
    simpleAlbum and (JsPath \ "copyrights").read[List[Copyright]] and
      (JsPath \ "external_ids").read[ExternalID] and (JsPath \ "genres").read[List[String]] and
      (JsPath \ "label").read[String] and (JsPath \ "popularity").read[Int] and
      (JsPath \ "release_date").read[String] and
      (JsPath \ "release_date_precision").read[String] and
      (JsPath \ "tracks").read[Page[SimpleTrack]]
  }

}
