package it.turingtest.spotify.scala.client.entities

import play.api.libs.json.{JsPath, Reads}
import play.api.libs.functional.syntax._

trait Playlist

/**
  * @see https://developer.spotify.com/web-api/object-model/#playlist-object-full
  */
case class FullPlaylist
(
  collaborative: Boolean,
  external_urls: ExternalURL,
  href: String,
  id: String,
  images: Option[List[Image]],
  name: String,
  owner: User,
  public: Option[Boolean],
  snapshot_id: String,
  objectType: String,
  uri: String,
  description: Option[String],
  followers: Followers,
  tracks: Page[PlaylistTrack]) extends Playlist

object FullPlaylist {
  implicit val fullPlaylistReads: Reads[FullPlaylist] = {(
    PlaylistReads.playlistFields and
      (JsPath \ "description").readNullable[String] and
      (JsPath \ "followers").read[Followers] and
      (JsPath \ "tracks").read[Page[PlaylistTrack]]
    )(FullPlaylist.apply _)
  }
}

/**
  * @see https://developer.spotify.com/web-api/object-model/#playlist-object-simplified
  */
case class SimplePlaylist
(
  collaborative: Boolean,
  external_urls: ExternalURL,
  href: String,
  id: String,
  images: Option[List[Image]],
  name: String,
  owner: User,
  public: Option[Boolean],
  snapshot_id: String,
  objectType: String,
  uri: String,
  tracks: TracksURL) extends Playlist

object SimplePlaylist {
  implicit val simplePlaylistReads: Reads[SimplePlaylist] = {(
    PlaylistReads.playlistFields and (JsPath \ "tracks").read[TracksURL])(SimplePlaylist.apply _)
  }
}

private object PlaylistReads {
  val playlistFields = {
    (JsPath \ "collaborative").read[Boolean] and
      (JsPath \ "external_urls").read[ExternalURL] and
      (JsPath \ "href").read[String] and
      (JsPath \ "id").read[String] and
      (JsPath \ "images").readNullable[List[Image]] and
      (JsPath \ "name").read[String] and
      (JsPath \ "owner").read[User] and
      (JsPath \ "public").readNullable[Boolean] and
      (JsPath \ "snapshot_id").read[String] and
      (JsPath \ "type").read[String] and
      (JsPath \ "uri").read[String]
  }
}
