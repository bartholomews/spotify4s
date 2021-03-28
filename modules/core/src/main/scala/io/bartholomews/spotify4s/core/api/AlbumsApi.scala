package io.bartholomews.spotify4s.core.api

import cats.data.NonEmptyList
import eu.timepit.refined.api.Refined
import eu.timepit.refined.collection.MaxSize
import io.bartholomews.fsclient.core.FsClient
import io.bartholomews.fsclient.core.http.SttpResponses.SttpResponse
import io.bartholomews.fsclient.core.oauth.v2.OAuthV2.ResponseHandler
import io.bartholomews.fsclient.core.oauth.{Signer, SignerV2}
import io.bartholomews.iso_country.CountryCodeAlpha2
import io.bartholomews.spotify4s.core.api.SpotifyApi.{apiUri, Offset}
import io.bartholomews.spotify4s.core.entities._
import sttp.model.Uri

// https://developer.spotify.com/documentation/web-api/reference/#category-albums
class AlbumsApi[F[_], S <: Signer](client: FsClient[F, S]) {
  import eu.timepit.refined.auto.autoRefineV
  import io.bartholomews.fsclient.core.http.FsClientSttpExtensions._

  private[api] val basePath: Uri = apiUri / "v1" / "albums"

  type AlbumIds = Refined[NonEmptyList[SpotifyId], MaxSize[20]]

  // https://developer.spotify.com/documentation/web-api/reference/#endpoint-get-an-album
  def getAlbum[E](id: SpotifyId, country: Option[CountryCodeAlpha2])(
    implicit signer: SignerV2,
    responseHandler: ResponseHandler[E, FullAlbum]
  ): F[SttpResponse[E, FullAlbum]] = {
    val uri: Uri = (basePath / id.value)
      .withOptionQueryParam("market", country.map(_.value))

    baseRequest(client.userAgent)
      .get(uri)
      .sign(signer)
      .response(responseHandler)
      .send(client.backend)
  }

  // https://developer.spotify.com/documentation/web-api/reference/#endpoint-get-multiple-albums
  def getAlbums[E](ids: AlbumIds, country: Option[CountryCodeAlpha2])(
    implicit signer: SignerV2,
    responseHandler: ResponseHandler[E, FullAlbumsResponse]
  ): F[SttpResponse[E, List[FullAlbum]]] = {
    val uri: Uri = basePath
      .withQueryParam("ids", ids.value.toList.mkString(","))
      .withOptionQueryParam("market", country.map(_.value))

    baseRequest(client.userAgent)
      .get(uri)
      .sign(signer)
      .response(responseHandler)
      .mapResponseRight(_.albums)
      .send(client.backend)
  }

  // https://developer.spotify.com/documentation/web-api/reference/#endpoint-get-an-albums-tracks
  def getAlbumTracks[E](
    id: SpotifyId,
    country: Option[CountryCodeAlpha2],
    limit: FullTrack.Limit = 20,
    offset: Offset = 0
  )(
    implicit signer: SignerV2,
    responseHandler: ResponseHandler[E, Page[FullTrack]]
  ): F[SttpResponse[E, Page[FullTrack]]] = {
    val uri: Uri = (basePath / id.value / "tracks")
      .withOptionQueryParam("market", country.map(_.value))
      .withQueryParam("limit", limit.value.toString)
      .withQueryParam("offset", offset.value.toString)

    baseRequest(client.userAgent)
      .get(uri)
      .sign(signer)
      .response(responseHandler)
      .send(client.backend)
  }
}
