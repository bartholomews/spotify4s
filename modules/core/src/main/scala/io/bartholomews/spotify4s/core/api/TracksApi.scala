package io.bartholomews.spotify4s.core.api

import cats.data.NonEmptySet
import cats.implicits.catsKernelStdOrderForString
import eu.timepit.refined.api.Validate.Plain
import eu.timepit.refined.api.{Refined, Validate}
import eu.timepit.refined.collection.MaxSize
import eu.timepit.refined.numeric.Interval
import eu.timepit.refined.predicates.all.Size
import eu.timepit.refined.refineV
import io.bartholomews.fsclient.core.FsClient
import io.bartholomews.fsclient.core.http.SttpResponses.{ResponseHandler, SttpResponse}
import io.bartholomews.fsclient.core.oauth.{Signer, SignerV2}
import io.bartholomews.spotify4s.core.api.SpotifyApi.basePath
import io.bartholomews.spotify4s.core.api.TracksApi.{AudioFeaturesTrackIds, TrackIds}
import io.bartholomews.spotify4s.core.entities.SpotifyId.SpotifyTrackId
import io.bartholomews.spotify4s.core.entities._
import io.bartholomews.spotify4s.core.validators.RefinedValidators.{maxSizeP, NesMaxSizeValidators}
import shapeless.Nat._0
import shapeless.Witness

// https://developer.spotify.com/documentation/web-api/reference/#category-tracks
private[spotify4s] class TracksApi[F[_], S <: Signer](client: FsClient[F, S]) {
  import io.bartholomews.fsclient.core.http.FsClientSttpExtensions._

  /**
    * Get Several Tracks
    * https://developer.spotify.com/documentation/web-api/reference/#endpoint-get-several-tracks
    *
    * Get Spotify catalog information for multiple tracks based on their Spotify IDs.
    *
    * @param ids             A list of the Spotify IDs for the tracks. Maximum: 50 IDs.
    * @param market          An ISO 3166-1 alpha-2 country code or the string from_token. Provide this parameter if you want to apply Track Relinking.
    * @param signer          A valid access token from the Spotify Accounts service: see the Web API Authorization Guide for details.
    * @param responseHandler The sttp `ResponseAs` handler
    * @tparam DE the Deserialization Error type
    * @return On success, the HTTP status code in the response header is 200 OK and the response body contains an object
    *         whose key is tracks and whose value is an array of track objects in JSON format.
    *         Objects are returned in the order requested. If an object is not found, a null value is returned in the appropriate position.
    *         Duplicate ids in the query will result in duplicate objects in the response.
    *         On error, the header status code is an error code and the response body contains an error object.
    */
  def getTracks[DE](ids: TrackIds, market: Option[Market])(signer: SignerV2)(
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

  /**
    * Get a Track
    *
    * https://developer.spotify.com/documentation/web-api/reference/#endpoint-get-track
    * Get Spotify catalog information for a single track identified by its unique Spotify ID.
    *
    * @param id     The Spotify ID for the track.
    * @param market An ISO 3166-1 alpha-2 country code or the string from_token. Provide this parameter if you want to apply Track Relinking.
    * @param signer A valid access token from the Spotify Accounts service: see the Web API Authorization Guide for details.
    * @param responseHandler The sttp `ResponseAs` handler
    * @tparam DE the Deserialization Error type
    * @return On success, the HTTP status code in the response header is 200 OK and the response body contains a track object in JSON format.
    *         On error, the header status code is an error code and the response body contains an error object.
    */
  def getTrack[DE](id: SpotifyTrackId, market: Option[Market])(signer: SignerV2)(
    implicit
    responseHandler: ResponseHandler[DE, FullTrack]
  ): F[SttpResponse[DE, FullTrack]] =
    baseRequest(client.userAgent)
      .get((basePath / "tracks" / id.value).withOptionQueryParam("market", market.map(_.value)))
      .sign(signer)
      .response(responseHandler)
      .send(client.backend)

  /**
    * Get Audio Features for Several Tracks
    *
    * https://developer.spotify.com/documentation/web-api/reference/#endpoint-get-several-audio-features
    * Get audio features for multiple tracks based on their Spotify IDs.
    *
    * @param ids A list of the Spotify IDs for the tracks. Maximum: 100 IDs.
    * @param signer A valid access token from the Spotify Accounts service: see the Web API Authorization Guide for details.
    * @param responseHandler The sttp `ResponseAs` handler
    * @tparam DE the Deserialization Error type
    * @return On success, the HTTP status code in the response header is 200 OK and the response body contains an object
    *         whose key is "audio_features" and whose value is an array of audio features objects in JSON format.
    *         Objects are returned in the order requested. If an object is not found, a null value is returned in the appropriate position.
    *         Duplicate ids in the query will result in duplicate objects in the response.
    *         On error, the header status code is an error code and the response body contains an error object.
    */
  def getAudioFeatures[DE](ids: AudioFeaturesTrackIds)(
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

  /**
    * Get Audio Features for a Track
    *
    * https://developer.spotify.com/documentation/web-api/reference/#endpoint-get-audio-features
    * Get audio feature information for a single track identified by its unique Spotify ID.
    *
    * @param id The Spotify ID for the track.
    * @param signer A valid access token from the Spotify Accounts service: see the Web API Authorization Guide for details.
    * @param responseHandler The sttp `ResponseAs` handler
    * @tparam DE the Deserialization Error type
    * @return On success, the HTTP status code in the response header is 200 OK
    *         and the response body contains an audio features object in JSON format.
    *         On error, the header status code is an error code and the response body contains an error object.
    */
  def getAudioFeatures[DE](id: SpotifyTrackId)(
    signer: SignerV2
  )(implicit responseHandler: ResponseHandler[DE, AudioFeatures]): F[SttpResponse[DE, AudioFeatures]] =
    baseRequest(client.userAgent)
      .get(basePath / "audio-features" / id.value)
      .sign(signer)
      .response(responseHandler)
      .send(client.backend)

  /**
    * Get Audio Analysis for a Track
    *
    * https://developer.spotify.com/documentation/web-api/reference/#endpoint-get-audio-analysis
    * Get a detailed audio analysis for a single track identified by its unique Spotify ID.
    *
    * @param id The Spotify ID for the track.
    * @param signer valid access token from the Spotify Accounts service: see the Web API Authorization Guide for details.
    * @param responseHandler The sttp `ResponseAs` handler
    * @tparam DE the Deserialization Error type
    * @return On success, the HTTP status code in the response header is 200 OK and the response body contains an audio analysis object in JSON format.
    *         On error, the header status code is an error code and the response body contains an error object.
    */
  def getAudioAnalysis[DE](
    id: SpotifyTrackId
  )(
    signer: SignerV2
  )(implicit responseHandler: ResponseHandler[DE, AudioAnalysis]): F[SttpResponse[DE, AudioAnalysis]] =
    baseRequest(client.userAgent)
      .get(basePath / "audio-analysis" / id.value)
      .sign(signer)
      .response(responseHandler)
      .send(client.backend)
}

object TracksApi {
  type TrackIds = Refined[NonEmptySet[SpotifyTrackId], MaxSize[50]]
  type AudioFeaturesTrackIds = Refined[NonEmptySet[SpotifyTrackId], MaxSize[100]]

  object TrackIds extends NesMaxSizeValidators[SpotifyTrackId, TrackIds](maxSize = 50) {
    private def validateTrackIds: Plain[NonEmptySet[SpotifyTrackId], MaxSize[50]] = {
      Validate
        .fromPredicate(
          (d: NonEmptySet[SpotifyTrackId]) => d.length <= 50,
          (_: NonEmptySet[SpotifyTrackId]) => "a maximum of 50 ids can be set in one request",
          Size[Interval.Closed[_0, Witness.`50`.T]](maxSizeP)
        )
    }

    override def fromNes(xs: NonEmptySet[SpotifyTrackId]): Either[String, TrackIds] =
      refineV[MaxSize[50]](xs)(validateTrackIds)
  }

  object AudioFeaturesTrackIds extends NesMaxSizeValidators[SpotifyTrackId, AudioFeaturesTrackIds](maxSize = 100) {
    private def validateAudioFeaturesTrackIds: Plain[NonEmptySet[SpotifyTrackId], MaxSize[100]] = {
      Validate
        .fromPredicate(
          (d: NonEmptySet[SpotifyTrackId]) => d.length <= 100,
          (_: NonEmptySet[SpotifyTrackId]) => "a maximum of 100 ids can be set in one request",
          Size[Interval.Closed[_0, Witness.`100`.T]](maxSizeP)
        )
    }

    override def fromNes(xs: NonEmptySet[SpotifyTrackId]): Either[String, AudioFeaturesTrackIds] =
      refineV[MaxSize[100]](xs)(validateAudioFeaturesTrackIds)
  }
}
