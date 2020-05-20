package io.bartholomews.spotify4s.api

import cats.data.NonEmptyList
import cats.effect.ConcurrentEffect
import fs2.Pipe
import io.bartholomews.fsclient.client.{FsClient, FsClientV2}
import io.bartholomews.fsclient.entities.oauth.{Signer, SignerV2}
import io.bartholomews.fsclient.requests.AuthJsonRequest
import io.bartholomews.fsclient.utils.HttpTypes.HttpResponse
import io.bartholomews.spotify4s.api.SpotifyApi.apiUri
import io.bartholomews.spotify4s.entities.{AudioAnalysis, AudioFeatures, FullTrack, Market, SpotifyUserId}
import io.circe.Json
import org.http4s.Uri

// https://developer.spotify.com/documentation/web-api/reference/tracks/
class TracksApi[F[_]: ConcurrentEffect, S <: Signer](client: FsClient[F, S]) {
  import FullTrack.decoder
  import io.bartholomews.fsclient.implicits.{decodeListAtKey, emptyEntityEncoder, rawJsonPipe}
  private[api] val basePath: Uri = apiUri / "v1"

  // https://developer.spotify.com/documentation/web-api/reference/tracks/get-audio-analysis/
  def getAudioAnalysis(id: SpotifyUserId)(implicit signer: SignerV2): F[HttpResponse[AudioAnalysis]] =
    new AuthJsonRequest.Get[AudioAnalysis] {
      override val uri: Uri = basePath / "audio-analysis" / id.value
    }.runWith(client)

  // https://developer.spotify.com/documentation/web-api/reference/tracks/get-audio-features/
  def getAudioFeatures(id: SpotifyUserId)(implicit signer: SignerV2): F[HttpResponse[AudioFeatures]] =
    new AuthJsonRequest.Get[AudioFeatures] {
      override val uri: Uri = basePath / "audio-features" / id.value
    }.runWith(client)

  // https://developer.spotify.com/documentation/web-api/reference/tracks/get-several-tracks/
  def getTracks(ids: NonEmptyList[SpotifyUserId], market: Option[Market])(
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
  def getTrack(id: SpotifyUserId, market: Option[Market])(
    implicit signer: SignerV2
  ): F[HttpResponse[FullTrack]] =
    new AuthJsonRequest.Get[FullTrack] {
      override val uri: Uri = (basePath / "tracks" / id.value)
        .withOptionQueryParam("market", market.map(_.value))
    }.runWith(client)
}
