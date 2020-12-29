package io.bartholomews.spotify4s.api

import cats.data.NonEmptySet
import cats.implicits.catsKernelStdOrderForString
import io.bartholomews.fsclient.core.http.SttpResponses.CirceJsonResponse
import io.bartholomews.fsclient.core.oauth.{Signer, SignerV2}
import io.bartholomews.fsclient.core.{FsApiClient, FsClient}
import io.bartholomews.spotify4s.api.SpotifyApi.apiUri
import io.bartholomews.spotify4s.entities.{AudioAnalysis, AudioFeatures, FullTrack, Market, SpotifyId}
import io.circe.Decoder
import sttp.model.Uri

// https://developer.spotify.com/documentation/web-api/reference/tracks/
class TracksApi[F[_], S <: Signer](client: FsClient[F, S]) extends FsApiClient(client) {
  import FullTrack.decoder
  import sttp.client.circe._
  private[api] val basePath: Uri = apiUri / "v1"

  // https://developer.spotify.com/documentation/web-api/reference/tracks/get-audio-analysis/
  def getAudioAnalysis(id: SpotifyId)(implicit signer: SignerV2): F[CirceJsonResponse[AudioAnalysis]] =
    baseRequest(client)
      .get(basePath / "audio-analysis" / id.value)
      .sign
      .response(asJson[AudioAnalysis])
      .send()

  // https://developer.spotify.com/documentation/web-api/reference/tracks/get-audio-features/
  def getAudioFeatures(id: SpotifyId)(implicit signer: SignerV2): F[CirceJsonResponse[AudioFeatures]] =
    baseRequest(client)
      .get(basePath / "audio-features" / id.value)
      .sign
      .response(asJson[AudioFeatures])
      .send()

  // https://developer.spotify.com/documentation/web-api/reference/tracks/get-several-audio-features/
  def getAudioFeatures(
    ids: NonEmptySet[SpotifyId]
  )(implicit signer: SignerV2): F[CirceJsonResponse[List[AudioFeatures]]] = {
    val decodeAudioFeatures = Decoder.decodeList[AudioFeatures].at("audio_features")
    baseRequest(client)
      .get(
        (basePath / "audio-features").withQueryParam("ids", ids.value.map(_.value).toNonEmptyList.toList.mkString(","))
      )
      .sign
      .response(asJson[List[AudioFeatures]](decodeAudioFeatures, implicitly))
      .send()
  }

  // https://developer.spotify.com/documentation/web-api/reference/tracks/get-several-tracks/
  def getTracks(ids: NonEmptySet[SpotifyId], market: Option[Market])(
    implicit signer: SignerV2
  ): F[CirceJsonResponse[List[FullTrack]]] = {
    val decodeList = Decoder.decodeList[FullTrack].at("tracks")
    baseRequest(client)
      .get(
        (basePath / "tracks")
          .withQueryParam("ids", ids.value.map(_.value).toNonEmptyList.toList.mkString(","))
          .withOptionQueryParam("market", market.map(_.value))
      )
      .sign
      .response(asJson[List[FullTrack]](decodeList, implicitly))
      .send()
  }

  // https://developer.spotify.com/documentation/web-api/reference/tracks/get-track/
  def getTrack(id: SpotifyId, market: Option[Market])(
    implicit signer: SignerV2
  ): F[CirceJsonResponse[FullTrack]] =
    baseRequest(client)
      .get((basePath / "tracks" / id.value).withOptionQueryParam("market", market.map(_.value)))
      .sign
      .response(asJson[FullTrack])
      .send()
}
