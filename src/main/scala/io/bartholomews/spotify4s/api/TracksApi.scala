package io.bartholomews.spotify4s.api

import cats.data.NonEmptySet
import cats.effect.ConcurrentEffect
import cats.implicits.catsKernelStdOrderForString
import io.bartholomews.fsclient.client.FsClient
import io.bartholomews.fsclient.entities.oauth.{Signer, SignerV2}
import io.bartholomews.fsclient.requests.FsAuthJson
import io.bartholomews.fsclient.utils.HttpTypes.HttpResponse
import io.bartholomews.spotify4s.api.SpotifyApi.apiUri
import io.bartholomews.spotify4s.entities.{AudioAnalysis, AudioFeatures, FullTrack, Market, SpotifyId}
import io.circe.Decoder
import org.http4s.Uri

// https://developer.spotify.com/documentation/web-api/reference/tracks/
class TracksApi[F[_]: ConcurrentEffect, S <: Signer](client: FsClient[F, S]) {
  import FullTrack.decoder
  private[api] val basePath: Uri = apiUri / "v1"

  // https://developer.spotify.com/documentation/web-api/reference/tracks/get-audio-analysis/
  def getAudioAnalysis(id: SpotifyId)(implicit signer: SignerV2): F[HttpResponse[AudioAnalysis]] =
    new FsAuthJson.Get[AudioAnalysis] {
      override val uri: Uri = basePath / "audio-analysis" / id.value
    }.runWith(client)

  // https://developer.spotify.com/documentation/web-api/reference/tracks/get-audio-features/
  def getAudioFeatures(id: SpotifyId)(implicit signer: SignerV2): F[HttpResponse[AudioFeatures]] =
    new FsAuthJson.Get[AudioFeatures] {
      override val uri: Uri = basePath / "audio-features" / id.value
    }.runWith(client)

  // https://developer.spotify.com/documentation/web-api/reference/tracks/get-several-audio-features/
  def getAudioFeatures(ids: NonEmptySet[SpotifyId])(implicit signer: SignerV2): F[HttpResponse[List[AudioFeatures]]] =
    new FsAuthJson.Get[List[AudioFeatures]]()(Decoder.decodeList[AudioFeatures].at("audio_features")) {
      override val uri: Uri = (basePath / "audio-features")
        .withQueryParam("ids", ids.value.map(_.value).toNonEmptyList.toList.mkString(","))
    }.runWith(client)

  // https://developer.spotify.com/documentation/web-api/reference/tracks/get-several-tracks/
  def getTracks(ids: NonEmptySet[SpotifyId], market: Option[Market])(
    implicit signer: SignerV2
  ): F[HttpResponse[List[FullTrack]]] = {
    new FsAuthJson.Get[List[FullTrack]]()(Decoder.decodeList[FullTrack].at("tracks")) {
      override val uri: Uri = (basePath / "tracks")
        .withQueryParam("ids", ids.map(_.value).toNonEmptyList.toList.mkString(","))
        .withOptionQueryParam("market", market.map(_.value))
    }.runWith(client)
  }

  // https://developer.spotify.com/documentation/web-api/reference/tracks/get-track/
  def getTrack(id: SpotifyId, market: Option[Market])(
    implicit signer: SignerV2
  ): F[HttpResponse[FullTrack]] =
    new FsAuthJson.Get[FullTrack] {
      override val uri: Uri = (basePath / "tracks" / id.value)
        .withOptionQueryParam("market", market.map(_.value))
    }.runWith(client)
}
