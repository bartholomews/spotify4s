package io.bartholomews.spotify4s.entities

import io.bartholomews.fsclient.codecs.FsJsonResponsePipe
import io.circe.generic.extras.ConfiguredJsonCodec
import org.http4s.Uri

@ConfiguredJsonCodec
case class SimplePlaylist(
  collaborative: Boolean,
  description: Option[String],
  externalUrls: ExternalResourceUrl,
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

object SimplePlaylist extends FsJsonResponsePipe[SimplePlaylist]
