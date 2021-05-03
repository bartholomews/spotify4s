package io.bartholomews.spotify4s.core.api

import cats.data.NonEmptySet
import cats.implicits.catsKernelStdOrderForString
import io.bartholomews.fsclient.core.FsClient
import io.bartholomews.fsclient.core.http.SttpResponses.{ResponseHandler, SttpResponse}
import io.bartholomews.fsclient.core.oauth.{Signer, SignerV2}
import io.bartholomews.spotify4s.core.api.SpotifyApi.apiUri
import io.bartholomews.spotify4s.core.entities._
import sttp.model.Uri

// https://developer.spotify.com/documentation/web-api/reference/tracks
private[spotify4s] class TracksApi[F[_], S <: Signer](client: FsClient[F, S]) {
  import io.bartholomews.fsclient.core.http.FsClientSttpExtensions._

  private[api] val basePath: Uri = apiUri / "v1"

  // https://developer.spotify.com/documentation/web-api/reference/tracks/get-audio-analysis/
  def getAudioAnalysis[DE](
    id: SpotifyId
  )(
    signer: SignerV2
  )(implicit responseHandler: ResponseHandler[DE, AudioAnalysis]): F[SttpResponse[DE, AudioAnalysis]] =
    baseRequest(client.userAgent)
      .get(basePath / "audio-analysis" / id.value)
      .sign(signer)
      .response(responseHandler)
      .send(client.backend)

  // https://developer.spotify.com/documentation/web-api/reference/tracks/get-audio-features/
  def getAudioFeatures[DE](id: SpotifyId)(
    signer: SignerV2
  )(implicit responseHandler: ResponseHandler[DE, AudioFeatures]): F[SttpResponse[DE, AudioFeatures]] =
    baseRequest(client.userAgent)
      .get(basePath / "audio-features" / id.value)
      .sign(signer)
      .response(responseHandler)
      .send(client.backend)

  // https://developer.spotify.com/documentation/web-api/reference/tracks/get-several-audio-features/
  def getAudioFeatures[DE](
    ids: NonEmptySet[SpotifyId]
  )(
    signer: SignerV2
  )(implicit responseHandler: ResponseHandler[DE, AudioFeaturesResponse]): F[SttpResponse[DE, List[AudioFeatures]]] =
    baseRequest(client.userAgent)
      .get(
        (basePath / "audio-features").withQueryParam("ids", ids.value.map(_.value).toNonEmptyList.toList.mkString(","))
      )
      .sign(signer)
      .response(responseHandler)
      .mapResponseRight(_.audioFeatures)
      .send(client.backend)

  // https://developer.spotify.com/documentation/web-api/reference/tracks/get-several-tracks/
  def getTracks[DE](ids: NonEmptySet[SpotifyId], market: Option[Market])(signer: SignerV2)(
    implicit
    responseHandler: ResponseHandler[DE, FullTracksResponse]
  ): F[SttpResponse[DE, List[FullTrack]]] =
    baseRequest(client.userAgent)
      .get(
        (basePath / "tracks")
          .withQueryParam("ids", ids.value.map(_.value).toNonEmptyList.toList.mkString(","))
          .withOptionQueryParam("market", market.map(_.value))
      )
      .sign(signer)
      .response(responseHandler)
      .mapResponseRight(_.tracks)
      .send(client.backend)

  // https://developer.spotify.com/documentation/web-api/reference/tracks/get-track/
  def getTrack[DE](id: SpotifyId, market: Option[Market])(signer: SignerV2)(
    implicit
    responseHandler: ResponseHandler[DE, FullTrack]
  ): F[SttpResponse[DE, FullTrack]] =
    baseRequest(client.userAgent)
      .get((basePath / "tracks" / id.value).withOptionQueryParam("market", market.map(_.value)))
      .sign(signer)
      .response(responseHandler)
      .send(client.backend)
}
