package it.turingtest.spotify.scala.client.entities

import play.api.libs.json.{JsPath, Reads}
import play.api.libs.functional.syntax._

case class FeaturedPlaylists
  (
    message: String,
    playlists: Page[SimplePlaylist]
    )
  extends SpotifyObject { override val objectType = "featuredPlaylists" }

object FeaturedPlaylists {
  implicit val featuredPlaylistsReads: Reads[FeaturedPlaylists] = (
    (JsPath \ "message").read[String] and
      (JsPath \ "playlists").read[Page[SimplePlaylist]]
    )(FeaturedPlaylists.apply _)
}


