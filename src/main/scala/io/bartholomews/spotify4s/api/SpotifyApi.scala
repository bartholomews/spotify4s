package io.bartholomews.spotify4s.api

import io.bartholomews.spotify4s.config.SpotifyConfig

object SpotifyApi {
  private[api] val apiUri = SpotifyConfig.spotify.apiUri.value
  private[api] val accountsUri = SpotifyConfig.spotify.accountsUri.value
}
