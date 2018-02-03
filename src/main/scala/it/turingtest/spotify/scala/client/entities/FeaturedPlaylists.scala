package it.turingtest.spotify.scala.client.entities

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Reads}

case class FeaturedPlaylists(message: String, playlists: Page[SimplePlaylist])
  extends SpotifyObject { override val objectType = "featuredPlaylists" }

object FeaturedPlaylists {
  implicit val featuredPlaylistsReads: Reads[FeaturedPlaylists] = (
    (JsPath \ "message").read[String] and
      (JsPath \ "playlists").lazyRead[Page[SimplePlaylist]](Page.reads[SimplePlaylist])
    )(FeaturedPlaylists.apply _)
}