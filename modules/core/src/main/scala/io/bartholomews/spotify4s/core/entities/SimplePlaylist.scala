package io.bartholomews.spotify4s.core.entities

import eu.timepit.refined.api.Refined
import eu.timepit.refined.numeric.Interval
import io.bartholomews.spotify4s.core.entities.SpotifyId.SpotifyPlaylistName
import sttp.model.Uri

final case class SimplePlaylist(
  collaborative: Boolean,
  description: Option[String],
  externalUrls: ExternalResourceUrl,
  href: Uri,
  id: SpotifyId,
  images: List[SpotifyImage],
  name: SpotifyPlaylistName,
  owner: PublicUser,
  public: Option[Boolean],
  snapshotId: String,
  tracks: CollectionLink,
  uri: SpotifyUri
)

object SimplePlaylist {
  type Limit = Refined[Int, Interval.Closed[1, 50]]
}

final case class PlaylistsResponse(playlists: Page[SimplePlaylist])
