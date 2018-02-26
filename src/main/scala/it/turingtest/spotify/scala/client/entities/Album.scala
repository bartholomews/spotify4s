package it.turingtest.spotify.scala.client.entities

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Reads}

trait Album

/**
  * @see https://developer.spotify.com/web-api/object-model/#album-object-full
  *
  * @param album_type             The type of the album: one of "album", "single", or "compilation".
  *
  * @param artists                The artists of the album.
  *                               Each artist object includes a link in href
  *                               to more detailed information about the artist.
  *
  * @param available_markets      The markets in which the album is available: ISO 3166-1 alpha-2 country codes.
  *                               Note that an album is considered available in a market
  *                               when at least 1 of its tracks is available in that market.
  *
  * @param external_urls          Known external URLs for this album.
  *
  * @param href                   A link to the Web API endpoint providing full details of the album.
  *
  * @param id                     The Spotify ID for the album.
  *
  * @param images                 The cover art for the album in various sizes, widest first.
  *
  * @param name                   The name of the album. In case of an album takedown, the value may be an empty string.
  *
  * @param uri                    The Spotify URI for the album.
  *
  * @param copyrights             The copyright statements of the album.
  *
  * @param external_ids           Known external IDs for the album.
  *
  * @param genres                 A list of the genres used to classify the album.
  *                               For example: "Prog Rock", "Post-Grunge". (If not yet classified, the array is empty.)
  *
  * @param label                  The label for the album.
  *
  * @param popularity             The popularity of the album.
  *                               The value will be between 0 and 100, with 100 being the most popular.
  *                               The popularity is calculated from the popularity of the album's individual tracks.
  *
  * @param release_date           The date the album was first released, for example "1981-12-15".
  *                               Depending on the precision, it might be shown as "1981" or "1981-12".
  *
  * @param release_date_precision The precision with which release_date value is known: "year", "month", or "day".
  *
  * @param tracks                 The tracks of the album.
  */
case class FullAlbum
(
  album_type: Option[String],
  artists: List[SimpleArtist],
  available_markets: Seq[String],
  external_urls: ExternalURL,
  href: Option[String],
  id: Option[String],
  images: List[Image],
  name: String,
  uri: Option[String],
  objectType: String,
  copyrights: List[Copyright],
  external_ids: ExternalID,
  genres: List[String],
  label: Option[String],
  popularity: Int,
  release_date: String,
  release_date_precision: String,
  tracks: Page[SimpleTrack]) extends Album {
}

object FullAlbum {
  implicit val fullAlbumReads: Reads[FullAlbum] = AlbumReads.fullAlbum(FullAlbum.apply _)
}

/**
  * @see https://developer.spotify.com/web-api/object-model/#album-object-simplified
  *
  * @param album_type The type of the album: one of "album", "single", or "compilation".
  *
  * @param artists The artists of the album.
  *                Each artist object includes a link in href
  *                to more detailed information about the artist.
  *
  * @param available_markets The markets in which the album is available: ISO 3166-1 alpha-2 country codes.
  *                          Note that an album is considered available in a market
  *                          when at least 1 of its tracks is available in that market.
  *
  * @param external_urls Known external URLs for this album.
  *
  * @param href A link to the Web API endpoint providing full details of the album.
  *
  * @param id The Spotify ID for the album.
  *
  * @param images The cover art for the album in various sizes, widest first.
  *
  * @param name The name of the album. In case of an album takedown, the value may be an empty string.
  *
  * @param uri The Spotify URI for the album.
  *
  * @param objectType The object type: "album"
  */
case class SimpleAlbum
(
  album_type: Option[String],
  artists: List[SimpleArtist],
  available_markets: Seq[String],
  external_urls: ExternalURL,
  href: Option[String],
  id: Option[String],
  images: List[Image],
  name: String,
  uri: Option[String],
  objectType: String) extends SpotifyObject {
}

object SimpleAlbum {
  implicit val simpleAlbumReads: Reads[SimpleAlbum] = AlbumReads.simpleAlbum(SimpleAlbum.apply _)
}

object AlbumReads {

  val simpleAlbum = {
    (JsPath \ "album_type").readNullable[String] and
      (JsPath \ "artists").read[List[SimpleArtist]] and
      ((JsPath \ "available_markets").read[Seq[String]] or Reads.pure(Seq.empty[String])) and
      (JsPath \ "external_urls").read[ExternalURL] and
      (JsPath \ "href").readNullable[String] and
      (JsPath \ "id").readNullable[String] and
      (JsPath \ "images").read[List[Image]] and
      (JsPath \ "name").read[String] and
      (JsPath \ "uri").readNullable[String] and
      (JsPath \ "type").read[String]
  }
  val fullAlbum = {
    simpleAlbum and (JsPath \ "copyrights").read[List[Copyright]] and
      (JsPath \ "external_ids").read[ExternalID] and (JsPath \ "genres").read[List[String]] and
      (JsPath \ "label").readNullable[String] and (JsPath \ "popularity").read[Int] and
      (JsPath \ "release_date").read[String] and
      (JsPath \ "release_date_precision").read[String] and
      (JsPath \ "tracks").read[Page[SimpleTrack]]
  }

}
