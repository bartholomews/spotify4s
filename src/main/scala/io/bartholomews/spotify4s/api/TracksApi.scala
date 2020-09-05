package io.bartholomews.spotify4s.api

import cats.effect.ConcurrentEffect
import fs2.Pipe
import io.bartholomews.fsclient.client.FsClient
import io.bartholomews.fsclient.entities.oauth.{Signer, SignerV2}
import io.bartholomews.fsclient.requests.AuthJsonRequest
import io.bartholomews.spotify4s.api.SpotifyApi.apiUri
import io.bartholomews.fsclient.utils.HttpTypes.HttpResponse
import io.bartholomews.spotify4s.entities.{AudioAnalysis, AudioFeatures, FullTrack, Market, SpotifyId}
import io.circe.Json
import org.http4s.Uri

// https://developer.spotify.com/documentation/web-api/reference/tracks/
class TracksApi[F[_]: ConcurrentEffect, S <: Signer](client: FsClient[F, S]) {
  import FullTrack.decoder
  import io.bartholomews.fsclient.implicits.{decodeListAtKey, emptyEntityEncoder, rawJsonPipe}
  private[api] val basePath: Uri = apiUri / "v1"

  // https://developer.spotify.com/documentation/web-api/reference/tracks/get-audio-analysis/
  def getAudioAnalysis(id: SpotifyId)(implicit signer: SignerV2): F[HttpResponse[AudioAnalysis]] =
    new AuthJsonRequest.Get[AudioAnalysis] {
      override val uri: Uri = basePath / "audio-analysis" / id.value
    }.runWith(client)

  // https://developer.spotify.com/documentation/web-api/reference/tracks/get-audio-features/
  def getAudioFeatures(id: SpotifyId)(implicit signer: SignerV2): F[HttpResponse[AudioFeatures]] =
    new AuthJsonRequest.Get[AudioFeatures] {
      override val uri: Uri = basePath / "audio-features" / id.value
    }.runWith(client)

  // https://developer.spotify.com/documentation/web-api/reference/tracks/get-several-audio-features/
  def getAudioFeatures(ids: Set[SpotifyId])(implicit signer: SignerV2): F[HttpResponse[List[AudioFeatures]]] = {
    implicit val pipeDecoder: Pipe[F, Json, List[AudioFeatures]] = decodeListAtKey[F, AudioFeatures]("audio_features")
    new AuthJsonRequest.Get[List[AudioFeatures]] {
      override val uri: Uri = (basePath / "audio-features")
        .withQueryParam("ids", ids.map(_.value).toList.mkString(","))
    }.runWith(client)
  }

  // https://developer.spotify.com/documentation/web-api/reference/tracks/get-several-tracks/
  def getTracks(ids: Set[SpotifyId], market: Option[Market])(
    implicit signer: SignerV2
  ): F[HttpResponse[List[FullTrack]]] = {
    implicit val pipeDecoder: Pipe[F, Json, List[FullTrack]] = decodeListAtKey[F, FullTrack]("tracks")
    new AuthJsonRequest.Get[List[FullTrack]] {
      override val uri: Uri = (basePath / "tracks")
        .withQueryParam("ids", ids.map(_.value).toList.mkString(","))
        .withOptionQueryParam("market", market.map(_.value))
    }.runWith(client)
  }

  // https://developer.spotify.com/documentation/web-api/reference/tracks/get-track/
  def getTrack(id: SpotifyId, market: Option[Market])(
    implicit signer: SignerV2
  ): F[HttpResponse[FullTrack]] =
    new AuthJsonRequest.Get[FullTrack] {
      override val uri: Uri = (basePath / "tracks" / id.value)
        .withOptionQueryParam("market", market.map(_.value))
    }.runWith(client)
}
