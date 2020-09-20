package io.bartholomews.spotify4s.entities

import io.bartholomews.fsclient.codecs.FsJsonResponsePipe
import io.circe.Decoder
import io.circe.generic.extras.semiauto.deriveConfiguredDecoder
import org.http4s.Uri

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

object SimplePlaylist extends FsJsonResponsePipe[SimplePlaylist] {
  implicit val decoder: Decoder[SimplePlaylist] = deriveConfiguredDecoder
}
