package it.turingtest.spotify.scala.client.entities

import play.api.libs.json.{JsPath, Reads}
import play.api.libs.functional.syntax._

/**
  * @see https://developer.spotify.com/web-api/object-model/#track-object-simplified
  *
  * @param artists The artists who performed the track.
  *                Each artist object includes a link in href to more detailed information about the artist.
  *
  * @param available_markets A list of the countries in which the track can be played,
  *                          identified by their ISO 3166-1 alpha-2 code (@see https://en.wikipedia.org/wiki/ISO_3166-1_alpha-2)
  *
  * @param disc_number The disc number (usually 1 unless the album consists of more than one disc).
  *
  * @param duration_ms The track length in milliseconds.
  *
  * @param explicit Whether or not the track has explicit lyrics (true = yes it does; false = no it does not OR unknown).
  *
  * @param external_urls External URLs for this track.
  *
  * @param href A link to the Web API endpoint providing full details of the track.
  *
  * @param id The Spotify ID (@see https://developer.spotify.com/web-api/user-guide/#spotify-uris-and-ids) for the track.
  *
  * @param is_playable Part of the response when Track Relinking is applied.
  *                    @see https://developer.spotify.com/web-api/track-relinking-guide/
  *                    If true, the track is playable in the given market. Otherwise false.
  *
  * @param linked_from Part of the response when Track Relinking is applied
  *                    and is only part of the response if the track linking, in fact, exists.
  *                    The requested track has been replaced with a different track.
  *                    The track in the linked_from object contains information about the originally requested track.
  */
case class SimpleTrack
(
artists: Seq[SimpleArtist],
available_markets: Seq[String],  // ISO 3166-1 alpha-2 code
disc_number: Int = 1,
duration_ms: Int,
explicit: Boolean,
external_urls: ExternalURL,
href: String,
id: String,
is_playable: Boolean,
linked_from: Option[TrackLink],
name: String,
preview_url: Option[String],
track_number: Option[Int],
objectType: String,
uri: String
)

object SimpleTrack {

  implicit val simpleTrackReads: Reads[SimpleTrack] = (
    (JsPath \ "artists").read[Seq[SimpleArtist]] and
      (JsPath \ "available_markets").read[Seq[String]].orElse(Reads.pure(Nil)) and
      (JsPath \ "disc_number").read[Int] and
      (JsPath \ "duration_ms").read[Int] and
      (JsPath \ "explicit").read[Boolean] and
      (JsPath \ "external_urls").read[ExternalURL] and
      (JsPath \ "href").read[String] and
      (JsPath \ "id").read[String] and
      (JsPath \ "is_playable").read[Boolean] and
      (JsPath \ "linked_from").readNullable[TrackLink] and
      (JsPath \ "name").read[String] and
      (JsPath \ "preview_url").readNullable[String] and
      (JsPath \ "track_number").readNullable[Int] and
      (JsPath \ "type").read[String] and
      (JsPath \ "uri").read[String]
    )(SimpleTrack.apply _)

}

case class TrackLink
(
external_urls: ExternalURL,
href: String,
id: String,
uri: String
) { val objectType = "track" }

object TrackLink {

  implicit val trackLinkReads: Reads[TrackLink] = (
  (JsPath \ "external_urls").read[ExternalURL] and
    (JsPath \ "href").read[String] and
    (JsPath \ "id").read[String] and
    (JsPath \ "uri").read[String]
  )(TrackLink.apply _)

}
