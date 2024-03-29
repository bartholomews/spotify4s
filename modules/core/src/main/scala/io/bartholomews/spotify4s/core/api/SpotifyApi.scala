package io.bartholomews.spotify4s.core.api

import cats.data.NonEmptyList
import eu.timepit.refined.api.Refined
import eu.timepit.refined.collection.MaxSize
import eu.timepit.refined.numeric.GreaterEqual
import io.bartholomews.fsclient.core.http.FsClientSttpExtensions.UriExtensions
import io.bartholomews.spotify4s.core.config.SpotifyConfig
import io.bartholomews.spotify4s.core.entities.SpotifyUri
import sttp.model.Uri

object SpotifyApi {
  private[api] val apiUri = SpotifyConfig.spotify.apiUri.value
  private[api] val accountsUri = SpotifyConfig.spotify.accountsUri.value

  private[api] val basePath: Uri = apiUri / "v1"

  type Offset = Int Refined GreaterEqual[0]
  type SpotifyUris = Refined[NonEmptyList[SpotifyUri], MaxSize[100]]
}
