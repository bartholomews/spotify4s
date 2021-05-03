package io.bartholomews.spotify4s.core.entities

import sttp.model.Uri

// https://developer.spotify.com/documentation/web-api/reference/#object-recommendationsobject
final case class Recommendations(seeds: List[RecommendationSeed], tracks: List[SimpleTrack])

// https://developer.spotify.com/documentation/web-api/reference/#object-recommendationseedobject
final case class RecommendationSeed(
  afterFilteringSize: Int,
  afterRelinkingSize: Int,
  href: Uri,
  id: SpotifyId,
  initialPoolSize: Int,
  `type`: String // FIXME[FB] ADT
)
