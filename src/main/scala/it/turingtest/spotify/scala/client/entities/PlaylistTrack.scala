package it.turingtest.spotify.scala.client.entities

import play.api.libs.json.{JsPath, Reads}
import play.api.libs.functional.syntax._

/**
  * @see https://developer.spotify.com/web-api/object-model/#playlist-track-object
  */
case class PlaylistTrack
(
  added_at: Option[String],  // TODO https://developer.spotify.com/web-api/user-guide/#timestamps
  added_by: Option[User],
  is_local: Boolean,
  track: Track
)

object PlaylistTrack {
  implicit val playlistTrackReads: Reads[PlaylistTrack] = (
    (JsPath \ "added_at").readNullable[String] and
      (JsPath \ "added_by").readNullable[User] and
      (JsPath \ "is_local").read[Boolean] and
      (JsPath \ "track").read[Track]
    )(PlaylistTrack.apply _)
}
