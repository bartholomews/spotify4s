package io.bartholomews.spotify4s.core.api

import cats.data.NonEmptySet
import cats.implicits.catsKernelStdOrderForString
import io.bartholomews.fsclient.core.http.SttpResponses.SttpResponse
import io.bartholomews.fsclient.core.oauth.v2.OAuthV2.ResponseHandler
import io.bartholomews.fsclient.core.oauth.{Signer, SignerV2}
import io.bartholomews.fsclient.core.{FsApiClient, FsClient}
import io.bartholomews.spotify4s.core.api.SpotifyApi.apiUri
import io.bartholomews.spotify4s.core.entities.{
  AudioAnalysis,
  AudioFeatures,
  AudioFeaturesResponse,
  FullTrack,
  FullTracksResponse,
  Market,
  SpotifyId
}
import sttp.model.Uri

// https://developer.spotify.com/documentation/web-api/reference/tracks/
class TracksApi[F[_], S <: Signer](client: FsClient[F, S]) extends FsApiClient(client) {
  private[api] val basePath: Uri = apiUri / "v1"

  // https://developer.spotify.com/documentation/web-api/reference/tracks/get-audio-analysis/
  def getAudioAnalysis[E](
    id: SpotifyId
  )(implicit signer: SignerV2, responseHandler: ResponseHandler[E, AudioAnalysis]): F[SttpResponse[E, AudioAnalysis]] =
    baseRequest(client)
      .get(basePath / "audio-analysis" / id.value)
      .sign
      .response(responseHandler)
      .send()

  // https://developer.spotify.com/documentation/web-api/reference/tracks/get-audio-features/
  def getAudioFeatures[E](
    id: SpotifyId
  )(implicit signer: SignerV2, responseHandler: ResponseHandler[E, AudioFeatures]): F[SttpResponse[E, AudioFeatures]] =
    baseRequest(client)
      .get(basePath / "audio-features" / id.value)
      .sign
      .response(responseHandler)
      .send()

  // https://developer.spotify.com/documentation/web-api/reference/tracks/get-several-audio-features/
  def getAudioFeatures[E](
    ids: NonEmptySet[SpotifyId]
  )(
    implicit signer: SignerV2,
    responseHandler: ResponseHandler[E, AudioFeaturesResponse]
  ): F[SttpResponse[E, List[AudioFeatures]]] = {
    baseRequest(client)
      .get(
        (basePath / "audio-features").withQueryParam("ids", ids.value.map(_.value).toNonEmptyList.toList.mkString(","))
      )
      .sign
      .response(responseHandler)
      .mapResponseRight(_.audioFeatures)
      .send()
  }

  // https://developer.spotify.com/documentation/web-api/reference/tracks/get-several-tracks/
  def getTracks[E](ids: NonEmptySet[SpotifyId], market: Option[Market])(
    implicit signer: SignerV2,
    responseHandler: ResponseHandler[E, FullTracksResponse]
  ): F[SttpResponse[E, List[FullTrack]]] = {
    baseRequest(client)
      .get(
        (basePath / "tracks")
          .withQueryParam("ids", ids.value.map(_.value).toNonEmptyList.toList.mkString(","))
          .withOptionQueryParam("market", market.map(_.value))
      )
      .sign
      .response(responseHandler)
      .mapResponseRight(_.tracks)
      .send()
  }

  // https://developer.spotify.com/documentation/web-api/reference/tracks/get-track/
  def getTrack[E](id: SpotifyId, market: Option[Market])(
    implicit signer: SignerV2,
    responseHandler: ResponseHandler[E, FullTrack]
  ): F[SttpResponse[E, FullTrack]] =
    baseRequest(client)
      .get((basePath / "tracks" / id.value).withOptionQueryParam("market", market.map(_.value)))
      .sign
      .response(responseHandler)
      .send()
}
