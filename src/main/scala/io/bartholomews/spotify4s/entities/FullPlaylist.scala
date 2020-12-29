package io.bartholomews.spotify4s.entities

import io.circe.Decoder
import io.circe.generic.extras.semiauto.deriveConfiguredDecoder
import sttp.model.Uri

/**
  * https://developer.spotify.com/documentation/web-api/reference/object-model/#playlist-object-full
  *
  * @param collaborative Returns true if context is not search and the owner allows other users to modify the playlist.
  *                      Otherwise returns false.
  *
  * @param description  The playlist description. Only returned for modified, verified playlists, otherwise None.
  *
  * @param externalUrls Known external URLs for this playlist.
  *
  * @param followers  Information about the followers of the playlist.
  *
  * @param href A link to the Web API endpoint providing full details of the playlist.
  *
  * @param id The Spotify ID for the playlist.
  *
  * @param images Images for the playlist. The array may be empty or contain up to three images.
  *               The images are returned by size in descending order.
  *               See Working with Playlists.
  *               Note: If returned, the source URL for the image ( url ) is temporary
  *               and will expire in less than a day.
  *
  * @param name  	The name of the playlist.
  *
  * @param owner  The user who owns the playlist
  *
  * @param public The playlist’s public/private status:
  *               true the playlist is public,
  *               false the playlist is private,
  *               None the playlist status is not relevant.
  *               For more about public/private status, see Working with Playlists.
  *
  * @param snapshotId The version identifier for the current playlist.
  *                   Can be supplied in other requests to target a specific playlist version:
  *                   see Remove tracks from a playlist
  *
  * @param tracks Information about the tracks of the playlist.
  *
  * @param uri  The Spotify URI for the playlist.
  */
case class FullPlaylist(
  collaborative: Boolean,
  description: Option[String],
  externalUrls: ExternalResourceUrl,
  followers: Followers,
  href: Uri,
  id: SpotifyId,
  images: List[SpotifyImage],
  name: String,
  owner: PublicUser,
  public: Option[Boolean],
  snapshotId: String,
  tracks: Page[PlaylistTrack],
  uri: SpotifyUri
)

object FullPlaylist {
  implicit val decoder: Decoder[FullPlaylist] = deriveConfiguredDecoder
}
