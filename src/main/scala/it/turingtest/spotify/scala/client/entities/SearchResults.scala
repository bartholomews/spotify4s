package it.turingtest.spotify.scala.client.entities

import play.api.libs.json.{Json, Reads}

sealed trait SearchResults

case class AlbumSearchResult(albums: Page[SimpleAlbum]) extends SearchResults
object AlbumSearchResult {
  implicit val reader: Reads[AlbumSearchResult] = Json.reads[AlbumSearchResult]
}

case class ArtistSearchResult(artists: Page[SimpleArtist]) extends SearchResults
object ArtistSearchResult {
  implicit val reader: Reads[ArtistSearchResult] = Json.reads[ArtistSearchResult]
}

case class PlaylistSearchResult(playlists: Page[SimplePlaylist]) extends SearchResults
object PlaylistSearchResult {
  implicit val reader: Reads[PlaylistSearchResult] = Json.reads[PlaylistSearchResult]
}

case class TrackSearchResult(tracks: Page[Track]) extends SearchResults
object TrackSearchResult {
  implicit val reader: Reads[TrackSearchResult] = Json.reads[TrackSearchResult]
}
