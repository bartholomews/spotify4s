package io.bartholomews.spotify4s.entities

import eu.timepit.refined.api.Refined
import eu.timepit.refined.numeric.Interval
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
  tracks: CollectionLink,
  uri: SpotifyUri
)

object SimplePlaylist {
  type Limit = Refined[Int, Interval.Closed[1, 50]]
  implicit val decoder: Decoder[SimplePlaylist] = deriveConfiguredDecoder
}
