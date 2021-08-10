package io.bartholomews.spotify4s.core.api

import cats.data.NonEmptyList
import eu.timepit.refined.api.Validate.Plain
import eu.timepit.refined.api.{Refined, Validate}
import eu.timepit.refined.collection.MaxSize
import eu.timepit.refined.numeric.Interval
import eu.timepit.refined.predicates.all.Size
import eu.timepit.refined.refineV
import io.bartholomews.fsclient.core.FsClient
import io.bartholomews.fsclient.core.http.SttpResponses.{ResponseHandler, SttpResponse}
import io.bartholomews.fsclient.core.oauth.{Signer, SignerV2}
import io.bartholomews.iso.CountryCodeAlpha2
import io.bartholomews.spotify4s.core.api.AlbumsApi.AlbumIds
import io.bartholomews.spotify4s.core.api.SpotifyApi.{apiUri, Offset}
import io.bartholomews.spotify4s.core.entities.SpotifyId.SpotifyAlbumId
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

  /**
    * Get Multiple Albums
    *
    * https://developer.spotify.com/documentation/web-api/reference/#endpoint-get-multiple-albums
    * Get Spotify catalog information for multiple albums identified by their Spotify IDs.
    *
    * @param ids A list of the Spotify IDs for the albums. Maximum: 20 IDs.
    * @param market An ISO 3166-1 alpha-2 country code or the string from_token. Provide this parameter if you want to apply Track Relinking.
    * @param signer A valid user access token or your client credentials.
    * @param responseHandler The sttp `ResponseAs` handler
    * @tparam E the Deserialization Error type
    * @return On success, the HTTP status code in the response header is 200 OK
    *         and the response body contains an object whose key is "albums"
    *         and whose value is an array of album objects in JSON format.
    *         Objects are returned in the order requested.
    *         If an object is not found, it is not returned.
    *         Duplicate ids in the query will result in duplicate objects in the response.
    *         On error, the header status code is an error code
    *         and the response body contains an error object.
    */
  def getAlbums[E](ids: AlbumIds, market: Option[CountryCodeAlpha2])(signer: SignerV2)(
    implicit responseHandler: ResponseHandler[E, FullAlbumsResponse]
  ): F[SttpResponse[E, List[FullAlbum]]] = {
    val uri: Uri = basePath
      .withQueryParam("ids", ids.value.toList.map(_.value).mkString(","))
      .withOptionQueryParam("market", market.map(_.value))

    baseRequest(client.userAgent)
      .get(uri)
      .sign(signer)
      .response(responseHandler)
      .mapResponseRight(_.albums)
      .send(client.backend)
  }

  /**
    * Get an Album
    *
    * https://developer.spotify.com/documentation/web-api/reference/#endpoint-get-an-album
    * Get Spotify catalog information for a single album.
    *
    * @param id The Spotify ID of the album.
    * @param market The market you’d like to request. Synonym for `country`.
    * @param signer A valid user access token or your client credentials.
    * @param responseHandler The sttp `ResponseAs` handler
    * @tparam E the Deserialization Error type
    * @return On success, the HTTP status code in the response header is 200 OK
    *         and the response body contains an album object in JSON format.
    *         On error, the header status code is an error code
    *         and the response body contains an error object.
    */
  def getAlbum[E](id: SpotifyAlbumId, market: Option[CountryCodeAlpha2])(signer: SignerV2)(
    implicit responseHandler: ResponseHandler[E, FullAlbum]
  ): F[SttpResponse[E, FullAlbum]] = {
    val uri: Uri = (basePath / id.value)
      .withOptionQueryParam("market", market.map(_.value))

    baseRequest(client.userAgent)
      .get(uri)
      .sign(signer)
      .response(responseHandler)
      .send(client.backend)
  }

  /**
    * Get an Album's Tracks
    *
    * https://developer.spotify.com/documentation/web-api/reference/#endpoint-get-an-albums-tracks
    * Get Spotify catalog information about an album’s tracks.
    * Optional parameters can be used to limit the number of tracks returned.
    *
    * @param id The Spotify ID of the album.
    * @param market An ISO 3166-1 alpha-2 country code or the string from_token. Provide this parameter if you want to apply Track Relinking.
    * @param limit The maximum number of tracks to return. Default: 20. Minimum: 1. Maximum: 50.
    * @param offset The index of the first track to return. Default: 0 (the first object). Use with limit to get the next set of tracks.
    * @param signer A valid user access token or your client credentials.
    * @param responseHandler The sttp `ResponseAs` handler
    * @tparam E the Deserialization Error type
    * @return On success, the HTTP status code in the response header is 200 OK
    *         and the response body contains an album object in JSON format.
    *         On error, the header status code is an error code and the response body contains an error object.
    */
  def getAlbumTracks[E](
    id: SpotifyAlbumId,
    market: Option[CountryCodeAlpha2],
    limit: AlbumsApi.TracksLimit = 20,
    offset: Offset = 0
  )(
    signer: SignerV2
  )(implicit responseHandler: ResponseHandler[E, Page[SimpleTrack]]): F[SttpResponse[E, Page[SimpleTrack]]] = {
    val uri: Uri = (basePath / id.value / "tracks")
      .withOptionQueryParam("market", market.map(_.value))
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
  type AlbumIds = Refined[NonEmptyList[SpotifyAlbumId], MaxSize[20]]
  type TracksLimit = Refined[Int, Interval.Closed[1, 50]]

  object AlbumIds extends NelMaxSizeValidators[SpotifyAlbumId, AlbumIds](maxSize = 20) {
    private def validateAlbumIds: Plain[NonEmptyList[SpotifyAlbumId], MaxSize[20]] = {
      Validate
        .fromPredicate(
          (d: NonEmptyList[SpotifyAlbumId]) => d.length <= 20,
          (_: NonEmptyList[SpotifyAlbumId]) => "a maximum of 20 ids can be set in one request",
          Size[Interval.Closed[_0, Witness.`20`.T]](maxSizeP)
        )
    }

    override def fromNel(xs: NonEmptyList[SpotifyAlbumId]): Either[String, AlbumIds] =
      refineV[MaxSize[20]](xs)(validateAlbumIds)
  }
}
