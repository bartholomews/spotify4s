package io.bartholomews.spotify4s.entities

import io.circe.generic.extras.ConfiguredJsonCodec

// https://developer.spotify.com/documentation/web-api/reference/object-model/#artist-object-simplified
@ConfiguredJsonCodec
case class SimpleArtist(
  externalUrls: ExternalResourceUrl,
  href: String,
  id: SpotifyId,
  name: String,
  uri: String // FIXME `SpotifyUri`
)
