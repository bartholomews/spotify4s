package io.bartholomews.spotify4s.core.entities

import eu.timepit.refined.api.Refined
import eu.timepit.refined.numeric.Interval
import io.bartholomews.iso_country.CountryCodeAlpha2
import sttp.model.Uri

sealed trait SimplePlaylistItem
object SimplePlaylistItem {
  type Limit = Refined[Int, Interval.Closed[1, 100]]
}

// https://developer.spotify.com/documentation/web-api/reference/object-model/#track-object-simplified
case class SimpleTrack(
  artists: List[SimpleArtist],
  availableMarkets: List[CountryCodeAlpha2],
  discNumber: Int,
  durationMs: Int,
  explicit: Boolean,
  externalUrls: Option[ExternalResourceUrl],
  href: Option[Uri],
  id: Option[SpotifyId],
  isPlayable: Option[Boolean],
  linkedFrom: Option[LinkedTrack],
  restrictions: Option[Restrictions],
  name: String,
  previewUrl: Option[Uri],
  trackNumber: Int,
  uri: SpotifyUri,
  isLocal: Boolean
) extends SimplePlaylistItem

// https://developer.spotify.com/documentation/web-api/reference/object-model/#episode-object-simplified
case class SimpleEpisode(
  audioPreviewUrl: Option[Uri],
  description: String,
  durationMs: Int,
  explicit: Boolean,
  externalUrls: ExternalResourceUrl,
  href: Uri,
  id: SpotifyId,
  images: List[SpotifyImage],
  isExternallyHosted: Boolean,
  isPlayable: Boolean,
  languages: List[String],
  name: String,
  releaseDate: ReleaseDate,
// TODO: resumePoint
  uri: SpotifyUri
) extends SimplePlaylistItem
