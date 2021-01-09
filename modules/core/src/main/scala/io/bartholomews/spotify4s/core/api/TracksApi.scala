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
  def getAudioAnalysis[DE](
    id: SpotifyId
  )(
    implicit signer: SignerV2,
    responseHandler: ResponseHandler[DE, AudioAnalysis]
  ): F[SttpResponse[DE, AudioAnalysis]] =
    backend.send(
      baseRequest(client)
        .get(basePath / "audio-analysis" / id.value)
        .sign
        .response(responseHandler)
    )

  // https://developer.spotify.com/documentation/web-api/reference/tracks/get-audio-features/
  def getAudioFeatures[DE](
    id: SpotifyId
  )(
    implicit signer: SignerV2,
    responseHandler: ResponseHandler[DE, AudioFeatures]
  ): F[SttpResponse[DE, AudioFeatures]] =
    backend.send(
      baseRequest(client)
        .get(basePath / "audio-features" / id.value)
        .sign
        .response(responseHandler)
    )

  // https://developer.spotify.com/documentation/web-api/reference/tracks/get-several-audio-features/
  def getAudioFeatures[DE](
    ids: NonEmptySet[SpotifyId]
  )(
    implicit signer: SignerV2,
    responseHandler: ResponseHandler[DE, AudioFeaturesResponse]
  ): F[SttpResponse[DE, List[AudioFeatures]]] =
    backend.send(
      baseRequest(client)
        .get(
          (basePath / "audio-features")
            .withQueryParam("ids", ids.value.map(_.value).toNonEmptyList.toList.mkString(","))
        )
        .sign
        .response(responseHandler)
        .mapResponseRight(_.audioFeatures)
    )

  // https://developer.spotify.com/documentation/web-api/reference/tracks/get-several-tracks/
  def getTracks[DE](ids: NonEmptySet[SpotifyId], market: Option[Market])(
    implicit signer: SignerV2,
    responseHandler: ResponseHandler[DE, FullTracksResponse]
  ): F[SttpResponse[DE, List[FullTrack]]] =
    backend.send(
      baseRequest(client)
        .get(
          (basePath / "tracks")
            .withQueryParam("ids", ids.value.map(_.value).toNonEmptyList.toList.mkString(","))
            .withOptionQueryParam("market", market.map(_.value))
        )
        .sign
        .response(responseHandler)
        .mapResponseRight(_.tracks)
    )

  // https://developer.spotify.com/documentation/web-api/reference/tracks/get-track/
  def getTrack[DE](id: SpotifyId, market: Option[Market])(
    implicit signer: SignerV2,
    responseHandler: ResponseHandler[DE, FullTrack]
  ): F[SttpResponse[DE, FullTrack]] =
    backend.send(
      baseRequest(client)
        .get((basePath / "tracks" / id.value).withOptionQueryParam("market", market.map(_.value)))
        .sign
        .response(responseHandler)
    )
}
