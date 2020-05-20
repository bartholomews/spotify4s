package io.bartholomews.spotify4s.entities

import io.circe.generic.extras.ConfiguredJsonCodec

// https://developer.spotify.com/documentation/web-api/reference/object-model/#track-link
@ConfiguredJsonCodec
case class LinkedTrack(
  externalUrls: ExternalResourceUrl,
  href: String,
  id: SpotifyUserId,
  uri: String
)
