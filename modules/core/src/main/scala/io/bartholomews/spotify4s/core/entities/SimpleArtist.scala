package io.bartholomews.spotify4s.core.entities

// https://developer.spotify.com/documentation/web-api/reference/object-model/#artist-object-simplified
case class SimpleArtist(
  externalUrls: Option[ExternalResourceUrl],
  href: Option[String],
  id: Option[SpotifyId],
  name: String,
  uri: Option[SpotifyUri]
)
