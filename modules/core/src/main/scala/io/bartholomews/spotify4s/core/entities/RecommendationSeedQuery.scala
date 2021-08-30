package io.bartholomews.spotify4s.core.entities

import io.bartholomews.spotify4s.core.entities.SpotifyId.{SpotifyArtistId, SpotifyTrackId}

sealed trait RecommendationSeedQuery {
  private[spotify4s] def strValue: String
  private[spotify4s] def key: String
}

object RecommendationSeedQuery {
  final case class RecommendationSeedArtist(id: SpotifyArtistId) extends RecommendationSeedQuery {
    override val key: String = "seed_artists"
    override val strValue: String = id.value
  }

  final case class RecommendationSeedGenre(genre: SpotifyGenre) extends RecommendationSeedQuery {
    override val key: String = "seed_genres"
    override val strValue: String = genre.value
  }

  final case class RecommendationSeedTrack(id: SpotifyTrackId) extends RecommendationSeedQuery {
    override val key: String = "seed_tracks"
    override val strValue: String = id.value
  }
}
