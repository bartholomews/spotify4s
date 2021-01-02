package io.bartholomews.spotify4s.core.entities

// https://developer.spotify.com/documentation/web-api/reference/object-model/#track-link
case class LinkedTrack(
  externalUrls: ExternalResourceUrl,
  href: String,
  id: SpotifyId,
  uri: String
)
