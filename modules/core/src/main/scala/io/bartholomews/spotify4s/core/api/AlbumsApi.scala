package io.bartholomews.spotify4s.core.api

import cats.data.NonEmptyList
import eu.timepit.refined.api.Validate.Plain
import eu.timepit.refined.api.{Refined, Validate}
import eu.timepit.refined.collection.MaxSize
import eu.timepit.refined.numeric.Interval
import eu.timepit.refined.predicates.all.Size
import eu.timepit.refined.refineV
import io.bartholomews.fsclient.core.FsClient
import io.bartholomews.fsclient.core.http.SttpResponses.SttpResponse
import io.bartholomews.fsclient.core.oauth.v2.OAuthV2.ResponseHandler
import io.bartholomews.fsclient.core.oauth.{Signer, SignerV2}
import io.bartholomews.iso_country.CountryCodeAlpha2
import io.bartholomews.spotify4s.core.api.AlbumsApi.AlbumIds
import io.bartholomews.spotify4s.core.api.SpotifyApi.{apiUri, Offset}
import io.bartholomews.spotify4s.core.entities._
import io.bartholomews.spotify4s.core.validators.RefinedValidators.{maxSizeP, NelMaxSizeValidators}
import shapeless.Nat._0
import shapeless.Witness
import sttp.model.Uri

// https://developer.spotify.com/documentation/web-api/reference/#category-albums
private[spotify4s] class AlbumsApi[F[_], S <: Signer](client: FsClient[F, S]) {
  import eu.timepit.refined.auto.autoRefineV
  import io.bartholomews.fsclient.core.http.FsClientSttpExtensions._

  private[api] val basePath: Uri = apiUri / "v1" / "albums"

  // https://developer.spotify.com/documentation/web-api/reference/#endpoint-get-an-album
  def getAlbum[E](id: SpotifyId, country: Option[CountryCodeAlpha2])(signer: SignerV2)(
    implicit responseHandler: ResponseHandler[E, FullAlbum]
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
  def getAlbums[E](ids: AlbumIds, country: Option[CountryCodeAlpha2])(signer: SignerV2)(
    implicit responseHandler: ResponseHandler[E, FullAlbumsResponse]
  ): F[SttpResponse[E, List[FullAlbum]]] = {
    val uri: Uri = basePath
      .withQueryParam("ids", ids.value.toList.map(_.value).mkString(","))
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
    signer: SignerV2
  )(implicit responseHandler: ResponseHandler[E, Page[SimpleTrack]]): F[SttpResponse[E, Page[SimpleTrack]]] = {
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

object AlbumsApi {
  type AlbumIds = Refined[NonEmptyList[SpotifyId], MaxSize[20]]

  object AlbumIds extends NelMaxSizeValidators[SpotifyId, AlbumIds](maxSize = 20) {
    private def validateAlbumIds: Plain[NonEmptyList[SpotifyId], MaxSize[20]] = {
      Validate
        .fromPredicate(
          (d: NonEmptyList[SpotifyId]) => d.length <= 20,
          (_: NonEmptyList[SpotifyId]) => "a maximum of 20 ids can be set in one request",
          Size[Interval.Closed[_0, Witness.`20`.T]](maxSizeP)
        )
    }

    override def fromNel(xs: NonEmptyList[SpotifyId]): Either[String, AlbumIds] =
      refineV[MaxSize[20]](xs)(validateAlbumIds)
  }
}
