package io.bartholomews.spotify4s.core.utils

import io.bartholomews.scalatestudo.data.ClientData.sampleUserAgent
import io.bartholomews.scalatestudo.data.ClientData.v2.sampleClientPassword
import io.bartholomews.spotify4s.core.SpotifyAuthClient
import io.bartholomews.spotify4s.core.entities.SpotifyId
import io.bartholomews.spotify4s.core.entities.SpotifyId.SpotifyUserId
import sttp.client3.{HttpURLConnectionBackend, Identity}

object SpotifyClientData {
  val sampleSpotifyId: SpotifyId = SpotifyId("SAMPLE_SPOTIFY_ID")
  val sampleSpotifyUserId: SpotifyUserId = SpotifyUserId("SAMPLE_SPOTIFY_USER_ID")

  val sampleClient: SpotifyAuthClient[Identity] = new SpotifyAuthClient(
    sampleUserAgent,
    sampleClientPassword,
    HttpURLConnectionBackend()
  )
}
