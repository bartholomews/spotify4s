package io.bartholomews.spotify4s.api

import cats.data.NonEmptyList
import eu.timepit.refined.api.Refined
import eu.timepit.refined.collection.MaxSize
import eu.timepit.refined.numeric.Interval
import io.bartholomews.spotify4s.config.SpotifyConfig

object SpotifyApi {
  private[api] val apiUri = SpotifyConfig.spotify.apiUri.value
  private[api] val accountsUri = SpotifyConfig.spotify.accountsUri.value

  type Limit = Int Refined Interval.Closed[1, 50]
  type Offset = Int Refined Interval.Closed[0, 100]
  type OneToHundred[A] = NonEmptyList[A] Refined MaxSize[100]
}
