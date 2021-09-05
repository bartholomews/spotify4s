package io.bartholomews.spotify4s.core.entities

import sttp.model.Uri

// https://developer.spotify.com/documentation/web-api/reference/#object-recommendationsobject
final case class Recommendations(seeds: List[RecommendationSeed], tracks: List[SimpleTrack])

// https://developer.spotify.com/documentation/web-api/reference/#object-recommendationseedobject
final case class RecommendationSeed(
  initialPoolSize: Int,
  afterFilteringSize: Int,
  afterRelinkingSize: Int,
  href: Option[Uri],
  id: SpotifyId,
  `type`: String // FIXME[FB] ADT
)
