package io.bartholomews.spotify4s.core.entities

import io.bartholomews.iso_country.CountryCodeAlpha2
import sttp.model.Uri

// https://developer.spotify.com/documentation/web-api/reference/object-model/#track-object-full
case class FullTrack(
  album: SimpleAlbum,
  artists: List[SimpleArtist],
  availableMarkets: List[CountryCodeAlpha2],
  discNumber: Int,
  durationMs: Int,
  explicit: Boolean,
  externalIds: Option[ExternalIds],
  externalUrls: Option[ExternalResourceUrl],
  href: Option[Uri],
  id: Option[SpotifyId],
  isPlayable: Option[Boolean],
  linkedFrom: Option[LinkedTrack],
  restrictions: Option[Restrictions],
  name: String,
  popularity: Int,
  previewUrl: Option[Uri],
  trackNumber: Int,
  uri: SpotifyUri,
  isLocal: Boolean
)

case class FullTracksResponse(tracks: List[FullTrack])
