package io.bartholomews.spotify4s.core.entities

import sttp.model.Uri

// https://developer.spotify.com/documentation/web-api/reference/object-model/#artist-object-simplified
// FIXME ? I think everything is optional only because of local tracks,
//  maybe we can check if `SpotifyId` is defined and define an ADT based on that
//  as most fields should be required for a "real" Spotify artist (same for tracks etc)
//  also double check `type` field, maybe it's different for local I hope
case class SimpleArtist(
  externalUrls: Option[ExternalResourceUrl],
  href: Option[Uri],
  id: Option[SpotifyId],
  name: String,
  uri: Option[SpotifyUri]
)
