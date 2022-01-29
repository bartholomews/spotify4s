package io.bartholomews.spotify4s.core.api

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
import io.bartholomews.spotify4s.core.api.BrowseApi.{Limit, RecommendationSeedRequest, RecommendationsLimit}
import io.bartholomews.spotify4s.core.api.SpotifyApi.{basePath, Offset}
import io.bartholomews.spotify4s.core.entities.AudioFeaturesQuery.toParams
import io.bartholomews.spotify4s.core.entities._
import shapeless.Nat._0
import shapeless.Witness
import sttp.model.Uri

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// https://developer.spotify.com/documentation/web-api/reference/#category-browse
private[spotify4s] class BrowseApi[F[_], S <: Signer](client: FsClient[F, S]) {
  import eu.timepit.refined.auto.autoRefineV
  import io.bartholomews.fsclient.core.http.FsClientSttpExtensions._

  private[api] val browsePath: Uri = basePath / "browse"
  private[api] val recommendationsPath: Uri = basePath / "recommendations"

  /**
    * Get All Featured Playlists
    *
    * https://developer.spotify.com/documentation/web-api/reference/#endpoint-get-featured-playlists
    * Get a list of Spotify featured playlists (shown, for example, on a Spotify player’s ‘Browse’ tab).
    *
    * @param country Optional. A country: an ISO 3166-1 alpha-2 country code.
    *                (https://en.wikipedia.org/wiki/ISO_3166-1_alpha-2)
    *                Provide this parameter if you want the list of returned items
    *                to be relevant to a particular country.
    *                If omitted, the returned items will be relevant to all countries.
    * @param locale Optional. The desired language, consisting of a lowercase ISO 639-1 language code
    *               and an uppercase ISO 3166-1 alpha-2 country code, joined by an underscore.
    *               For example: es_MX, meaning “Spanish (Mexico)”.
    *               Provide this parameter if you want the results returned in a particular language (where available).
    *               Note that, if locale is not supplied, or if the specified language is not available,
    *               all strings will be returned in the Spotify default language (American English).
    *               The locale parameter, combined with the country parameter,
    *               may give odd results if not carefully matched.
    *               For example country=SE&locale=de_DE will return a list of categories relevant to Sweden
    *               but as German language strings.
    * @param timestamp Optional. A timestamp in ISO 8601 format: yyyy-MM-ddTHH:mm:ss.
    *                  Use this parameter to specify the user’s local time
    *                  to get results tailored for that specific date and time in the day.
    *                  If not provided, the response defaults to the current UTC time.
    *                  Example: “2014-10-23T09:00:00” for a user whose local time is 9AM.
    *                  If there were no featured playlists (or there is no data) at the specified time,
    *                  the response will revert to the current UTC time.
    * @param limit Optional. The maximum number of items to return. Default: 20. Minimum: 1. Maximum: 50.
    * @param offset Optional. The index of the first item to return. Default: 0 (the first object).
    *               Use with limit to get the next set of items.
    * @param signer A valid user access token or your client credentials.
    * @param responseHandler The sttp `ResponseAs` handler
    * @tparam DE the Deserialization Error type
    * @return On success, the HTTP status code in the response header is 200 OK
    *          and the response body contains a message and a playlists object.
    *          The playlists object contains an array of simplified playlist objects
    *          (wrapped in a paging object) in JSON format.
    *          On error, the header status code is an error code and the response body contains an error object.
    *          Once you have retrieved the list of playlist objects,
    *          you can use Get a Playlist and Get a Playlist’s Tracks to drill down further.
    */
  def getAllFeaturedPlaylists[DE](
    country: Option[CountryCodeAlpha2],
    locale: Option[Locale],
    timestamp: Option[LocalDateTime],
    limit: Limit = 20,
    offset: Offset = 0
  )(
    signer: SignerV2
  )(implicit responseHandler: ResponseHandler[DE, FeaturedPlaylists]): F[SttpResponse[DE, FeaturedPlaylists]] = {
    val uri: Uri = (browsePath / "featured-playlists")
      .withOptionQueryParam("country", country.map(_.value))
      .withOptionQueryParam("locale", locale.map(_.value))
      .withOptionQueryParam("timestamp", timestamp.map(_.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
      .withQueryParam("limit", limit.value.toString)
      .withQueryParam("offset", offset.value.toString)

    baseRequest(client.userAgent)
      .get(uri)
      .sign(signer)
      .response(responseHandler)
      .send(client.backend)
  }

  /**
    * Get All Categories
    *
    * https://developer.spotify.com/documentation/web-api/reference/#endpoint-get-categories
    * Get a list of categories used to tag items in Spotify (on, for example, the Spotify player’s “Browse” tab).
    *
    * @param country Optional. A country: an ISO 3166-1 alpha-2 country code.
    *                Provide this parameter if you want to narrow the list of returned categories
    *                to those relevant to a particular country.
    *                If omitted, the returned items will be globally relevant
    * @param locale The desired language, consisting of an ISO 639-1 language code and an ISO 3166-1 alpha-2 country code,
    *               joined by an underscore. For example: es_MX, meaning “Spanish (Mexico)”.
    *               Provide this parameter if you want the category metadata returned in a particular language.
    *               Note that, if locale is not supplied, or if the specified language is not available,
    *               all strings will be returned in the Spotify default language (American English).
    *               The locale parameter, combined with the country parameter,
    *               may give odd results if not carefully matched.
    *               For example country=SE&locale=de_DE will return a list of categories relevant to Sweden
    *               but as German language strings.
    * @param limit Optional. The maximum number of categories to return. Default: 20. Minimum: 1. Maximum: 50.
    * @param offset Optional. The index of the first item to return. Default: 0 (the first object). Use with limit to get the next set of categories.
    * @param signer A valid user access token or your client credentials.
    * @param responseHandler The sttp `ResponseAs` handler
    * @tparam DE the Deserialization Error type
    * @return On success, the HTTP status code in the response header is 200 OK
    *         and the response body contains an object with a categories field,
    *         with an array of category objects (wrapped in a paging object) in JSON format.
    *         On error, the header status code is an error code and the response body contains an error object.
    *         Once you have retrieved the list, you can use `getCategory` to drill down further.
    */
  def getAllCategories[DE](
    country: Option[CountryCodeAlpha2],
    locale: Option[Locale],
    limit: Limit = 20,
    offset: Offset = 0
  )(
    signer: SignerV2
  )(implicit responseHandler: ResponseHandler[DE, CategoriesResponse]): F[SttpResponse[DE, Page[Category]]] = {
    val uri: Uri = (browsePath / "categories")
      .withOptionQueryParam("country", country.map(_.value))
      .withOptionQueryParam("locale", locale.map(_.value))
      .withQueryParam("limit", limit.value.toString)
      .withQueryParam("offset", offset.value.toString)

    baseRequest(client.userAgent)
      .get(uri)
      .sign(signer)
      .response(responseHandler)
      .mapResponseRight(_.categories)
      .send(client.backend)
  }

  /**
    * Get a Category
    *
    * https://developer.spotify.com/documentation/web-api/reference/#endpoint-get-a-category
    * Get a single category used to tag items in Spotify (on, for example, the Spotify player’s “Browse” tab).
    *
    * @param categoryId Required. The Spotify category ID for the category.
    * @param country Optional. A country: an ISO 3166-1 alpha-2 country code. Provide this parameter to ensure that the category exists for a particular country.
    * @param locale Optional. The desired language, consisting of an ISO 639-1 language code
    *               and an ISO 3166-1 alpha-2 country code, joined by an underscore.
    *               For example: es_MX, meaning "Spanish (Mexico)".
    *               Provide this parameter if you want the category strings returned in a particular language.
    *               Note that, if locale is not supplied, or if the specified language is not available,
    *               the category strings returned will be in the Spotify default language (American English).
    * @param signer A valid user access token or your client credentials.
    * @param responseHandler The sttp `ResponseAs` handler
    * @tparam DE the Deserialization Error type
    * @return On success, the HTTP status code in the response header is 200 OK
    *         and the response body contains a category object in JSON format.
    *         On error, the header status code is an error code and the response body contains an error object.
    *         Once you have retrieved the category, you can use `getCategoryPlaylists` to drill down further.
    */
  def getCategory[DE](
    categoryId: SpotifyCategoryId,
    country: Option[CountryCodeAlpha2],
    locale: Option[Locale]
  )(
    signer: SignerV2
  )(implicit responseHandler: ResponseHandler[DE, Category]): F[SttpResponse[DE, Category]] = {
    val uri: Uri = (browsePath / "categories" / categoryId.value)
      .withOptionQueryParam("country", country.map(_.value))
      .withOptionQueryParam("locale", locale.map(_.value))

    baseRequest(client.userAgent)
      .get(uri)
      .sign(signer)
      .response(responseHandler)
      .send(client.backend)
  }

  /**
    * Get a Category's Playlists
    *
    * https://developer.spotify.com/documentation/web-api/reference/#endpoint-get-a-categories-playlists
    * Get a list of Spotify playlists tagged with a particular category.
    *
    * @param categoryId Required. The Spotify category ID for the category.
    * @param country Optional. A country: an ISO 3166-1 alpha-2 country code.
    *                Provide this parameter to ensure that the category exists for a particular country.
    * @param limit Optional. The maximum number of items to return. Default: 20. Minimum: 1. Maximum: 50.
    * @param offset Optional. The index of the first item to return. Default: 0 (the first object).
    *               Use with limit to get the next set of items.
    * @param signer A valid user access token or your client credentials.
    * @param responseHandler The sttp `ResponseAs` handler
    * @tparam DE the Deserialization Error type
    * @return On success, the HTTP status code in the response header is 200 OK
    *         and the response body contains an array of simplified playlist objects
    *         (wrapped in a paging object) in JSON format.
    *         On error, the header status code is an error code
    *         and the response body contains an error object.
    *         Once you have retrieved the list, you can use `getPlaylist` and `getPlaylistItems` to drill down further.
    */
  def getCategoryPlaylists[DE](
    categoryId: SpotifyCategoryId,
    country: Option[CountryCodeAlpha2],
    limit: Limit = 20,
    offset: Offset = 0
  )(
    signer: SignerV2
  )(implicit responseHandler: ResponseHandler[DE, PlaylistsResponse]): F[SttpResponse[DE, Page[SimplePlaylist]]] = {
    val uri: Uri = (browsePath / "categories" / categoryId.value / "playlists")
      .withOptionQueryParam("country", country.map(_.value))
      .withQueryParam("limit", limit.value.toString)
      .withQueryParam("offset", offset.value.toString)

    baseRequest(client.userAgent)
      .get(uri)
      .sign(signer)
      .response(responseHandler)
      .mapResponseRight(_.playlists)
      .send(client.backend)
  }

  /**
    * Get Recommendations
    *
    * https://developer.spotify.com/documentation/web-api/reference/#endpoint-get-recommendations
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

  /**
    * Get Recommendation Genres
    *
    * https://developer.spotify.com/documentation/web-api/reference/#endpoint-get-recommendation-genres
    * Retrieve a list of available genres seed parameter values for recommendations.
    *
    * @param signer A valid user access token or your client credentials.
    * @param responseHandler The sttp `ResponseAs` handler
    * @tparam DE the Deserialization Error type
    * @return On success, the HTTP status code in the response header is 200 OK
    *         and the response body contains a recommendations response object in JSON format.
    */
  def getRecommendationGenres[DE](
    signer: SignerV2
  )(implicit responseHandler: ResponseHandler[DE, SpotifyGenresResponse]): F[SttpResponse[DE, List[SpotifyGenre]]] =
    baseRequest(client.userAgent)
      .get(recommendationsPath / "available-genre-seeds")
      .sign(signer)
      .response(responseHandler)
      .mapResponseRight(_.genres)
      .send(client.backend)
}

object BrowseApi {
  type Limit = Int Refined Interval.Closed[1, 50]
  type RecommendationsLimit = Int Refined Interval.Closed[1, 1000]
  type RecommendationSeedRequest = Refined[List[RecommendationSeedQuery], MaxSize[5]]
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
