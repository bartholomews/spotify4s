package io.bartholomews.spotify4s.core.entities

import sttp.model.Uri

// https://developer.spotify.com/documentation/web-api/reference/#object-artistobject
case class FullArtist(
  externalUrls: ExternalResourceUrl,
  followers: Followers,
  genres: List[String],
  href: Uri,
  id: SpotifyId,
  images: List[SpotifyImage],
  name: String,
  popularity: Int,
  uri: SpotifyUri
)
