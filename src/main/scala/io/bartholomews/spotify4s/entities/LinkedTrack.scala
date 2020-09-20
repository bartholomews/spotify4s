package io.bartholomews.spotify4s.entities

import io.circe.Decoder
import io.circe.generic.extras.semiauto.deriveConfiguredDecoder

// https://developer.spotify.com/documentation/web-api/reference/object-model/#track-link
case class LinkedTrack(
  externalUrls: ExternalResourceUrl,
  href: String,
  id: SpotifyId,
  uri: String
)

object LinkedTrack {
  implicit val decoder: Decoder[LinkedTrack] = deriveConfiguredDecoder
}
