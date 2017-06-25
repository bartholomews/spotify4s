package it.turingtest.spotify.scala.client.entities

import play.api.libs.json.{JsPath, Reads}
import play.api.libs.functional.syntax._

/**
  * @see https://developer.spotify.com/web-api/object-model/#track-object-full
  * @param album
  * @param artists
  * @param available_markets
  * @param disc_number
  * @param duration_ms
  * @param explicit
  * @param external_ids
  * @param external_urls
  * @param href
  * @param id
  * @param is_playable
  * @param linked_from
  * @param name
  * @param popularity
  * @param preview_url
  * @param track_number
  * @param objectType
  * @param uri
  */
case class Track
(
  album: SimpleAlbum,
  artists: List[SimpleArtist],
  available_markets: Seq[String],
  disc_number: Int,
  duration_ms: Int,
  explicit: Option[Boolean],
  external_ids: ExternalID,
  external_urls: ExternalURL,
  href: Option[String],
  id: Option[String],
  is_playable: Option[Boolean],
  linked_from: Option[TrackLink],
  name: String,
  popularity: Int,
  preview_url: Option[String],
  track_number: Option[Int],
  objectType: String,
  uri: String
)

object Track {
  implicit val trackReads: Reads[Track] = (
    (JsPath \ "album").read[SimpleAlbum] and
      (JsPath \ "artists").read[List[SimpleArtist]] and
      ((JsPath \ "available_markets").read[Seq[String]] or Reads.pure(Seq.empty[String])) and
      (JsPath \ "disc_number").read[Int] and
      (JsPath \ "duration_ms").read[Int] and
      (JsPath \ "explicit").readNullable[Boolean] and
      (JsPath \ "external_ids").read[ExternalID] and
      (JsPath \ "external_urls").read[ExternalURL] and
      (JsPath \ "href").readNullable[String] and
      (JsPath \ "id").readNullable[String] and
      (JsPath \ "is_playable").readNullable[Boolean] and
      (JsPath \ "linked_from").readNullable[TrackLink] and
      (JsPath \ "name").read[String] and
      (JsPath \ "popularity").read[Int] and
      (JsPath \ "preview_url").readNullable[String] and
      (JsPath \ "track_number").readNullable[Int] and
      (JsPath \ "type").read[String] and
      (JsPath \ "uri").read[String]
    )(Track.apply _)
}
