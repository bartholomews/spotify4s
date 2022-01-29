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
import io.bartholomews.spotify4s.core.api.TracksApi.{
  AudioFeaturesTrackIds,
  RecommendationSeedRequest,
  RecommendationsLimit,
  TrackIds
}
import io.bartholomews.spotify4s.core.entities.AudioFeaturesQuery.toParams
import io.bartholomews.spotify4s.core.entities.SpotifyId.SpotifyTrackId
import io.bartholomews.spotify4s.core.entities._
import io.bartholomews.spotify4s.core.validators.RefinedValidators.{maxSizeP, NesMaxSizeValidators}
import shapeless.Nat._0
import shapeless.Witness
import sttp.model.Uri

private[spotify4s] class TracksApi[F[_], S <: Signer](client: FsClient[F, S]) {
  import eu.timepit.refined.auto.autoRefineV
  import io.bartholomews.fsclient.core.http.FsClientSttpExtensions._

  private[api] val tracksPath: Uri = basePath / "tracks"
  private[api] val recommendationsPath: Uri = basePath / "recommendations"

  /**
    * Get Track
    * https://developer.spotify.com/documentation/web-api/reference/#/operations/get-track
    *
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
      .get((tracksPath / id.value).withOptionQueryParam("market", market.map(_.value)))
      .sign(signer)
      .response(responseHandler)
      .send(client.backend)

  /**
    * Get Several Tracks
    * https://developer.spotify.com/documentation/web-api/reference/#/operations/get-several-tracks
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
        tracksPath
          .withQueryParam("ids", ids.value.map(_.value).toNonEmptyList.toList.mkString(","))
          .withOptionQueryParam("market", market.map(_.value))
      )
      .sign(signer)
      .response(responseHandler)
      .mapResponseRight(_.tracks)
      .send(client.backend)

  /**
    * Get Tracks' Audio Features
    * https://developer.spotify.com/documentation/web-api/reference/#/operations/get-several-audio-features
    *
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
    * Get Track's Audio Features
    * https://developer.spotify.com/documentation/web-api/reference/#/operations/get-audio-features
    *
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
    * Get Track's Audio Analysis
    * https://developer.spotify.com/documentation/web-api/reference/#/operations/get-audio-analysis
    *
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

  /**
    * Get Recommendations
    * https://developer.spotify.com/documentation/web-api/reference/#/operations/get-recommendations
    *
    * Recommendations are generated based on the available information for a given seed entity and matched against similar artists and tracks.
    * If there is sufficient information about the provided seeds, a list of tracks will be returned together with pool size details.
    * For artists and tracks that are very new or obscure there might not be enough data to generate a list of tracks.
    *
    * @param limit The target size of the list of recommended tracks.
    *              For seeds with unusually small pools or when highly restrictive filtering is applied,
    *              it may be impossible to generate the requested number of recommended tracks.
    *              Debugging information for such cases is available in the response. Default: 20. Minimum: 1. Maximum: 100.
    * @param market An ISO 3166-1 alpha-2 country code or the string from_token. Provide this parameter if you want to apply Track Relinking.
    *               Because min_*, max_* and target_* are applied to pools before relinking,
    *               the generated results may not precisely match the filters applied.
    *               Original, non-relinked tracks are available via the linked_from attribute of the relinked track response.
    * @param recommendationSeedRequest A list of Spotify IDs for seed artists/genres/tracks.
    *                                  Up to 5 seed values may be provided in any combination of seed_artists, seed_tracks and seed_genres.
    * @param audioFeaturesQuery min_* - For each tunable track attribute, a hard floor on the selected track attribute’s value can be provided.
    *                           For example, min_tempo=140 would restrict results to only those tracks with a tempo of greater than 140 beats per minute.
    *                           max_* - For each tunable track attribute, a hard ceiling on the selected track attribute’s value can be provided.
    *                           For example, max_instrumentalness=0.35 would filter out most tracks that are likely to be instrumental.
    *                           target_* - For each of the tunable track attributes (below) a target value may be provided.
    *                           Tracks with the attribute values nearest to the target values will be preferred.
    *                           For example, you might request target_energy=0.6 and target_danceability=0.8. All target values will be weighed equally in ranking results.
    *                           See tunable track attributes below for the list of available options.
    * @param signer A valid user access token or your client credentials.
    * @param responseHandler The sttp `ResponseAs` handler
    * @tparam DE the Deserialization Error type
    * @return On success, the HTTP status code in the response header is 200 OK and the response body contains an array of simplified playlist objects (wrapped in a paging object) in JSON format.
    *         On error, the header status code is an error code and the response body contains an error object.
    *         Once you have retrieved the list, you can use Get a Playlist and Get a Playlist’s Tracks to drill down further.
    */
  def getRecommendations[DE](
    limit: RecommendationsLimit = 20,
    market: Option[Market],
    recommendationSeedRequest: RecommendationSeedRequest = Refined.unsafeApply(List.empty[RecommendationSeedQuery]),
    audioFeaturesQuery: AudioFeaturesQuery = AudioFeaturesQuery.empty
  )(
    signer: SignerV2
  )(implicit responseHandler: ResponseHandler[DE, Recommendations]): F[SttpResponse[DE, Recommendations]] = {
    val uri: Uri = recommendationsPath
      .withQueryParam("limit", limit.value.toString)
      .withOptionQueryParam("market", market.map(_.value))
      .addParams(recommendationSeedRequest.value.groupMapReduce(_.key)(_.strValue)((v1, v2) => s"$v1,$v2"))
      .addParams(toParams(audioFeaturesQuery): _*)

    baseRequest(client.userAgent)
      .get(uri)
      .sign(signer)
      .response(responseHandler)
      .send(client.backend)
  }
}

object TracksApi {
  type TrackIds = Refined[NonEmptySet[SpotifyTrackId], MaxSize[50]]
  type AudioFeaturesTrackIds = Refined[NonEmptySet[SpotifyTrackId], MaxSize[100]]
  type RecommendationsLimit = Int Refined Interval.Closed[1, 1000]
  type RecommendationSeedRequest = Refined[List[RecommendationSeedQuery], MaxSize[5]]

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

  object RecommendationSeedRequest {
    import io.bartholomews.spotify4s.core.validators.RefinedValidators._
    private def validateRecommendationSeedQuery: Plain[List[RecommendationSeedQuery], MaxSize[5]] = {
      Validate
        .fromPredicate(
          (d: List[RecommendationSeedQuery]) => d.size <= 5,
          (_: List[RecommendationSeedQuery]) => "a maximum of 5 values can be set in one request",
          Size[Interval.Closed[_0, Witness.`5`.T]](maxSizeP)
        )
    }

    def fromList(xs: List[RecommendationSeedQuery]): Either[String, RecommendationSeedRequest] =
      refineV[MaxSize[5]](xs)(validateRecommendationSeedQuery)
  }
}
