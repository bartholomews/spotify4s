package io.bartholomews.spotify4s.core.api

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import eu.timepit.refined.api.Refined
import eu.timepit.refined.numeric.{GreaterEqual, Interval}
import io.bartholomews.fsclient.core.FsClient
import io.bartholomews.fsclient.core.http.SttpResponses.{ResponseHandler, SttpResponse}
import io.bartholomews.fsclient.core.oauth.{Signer, SignerV2}
import io.bartholomews.iso.CountryCodeAlpha2
import io.bartholomews.spotify4s.core.api.SpotifyApi.apiUri
import io.bartholomews.spotify4s.core.entities._
import sttp.model.Uri

// TODO: Tidy up docs
// https://developer.spotify.com/documentation/web-api/reference/#category-browse
private[spotify4s] class BrowseApi[F[_], S <: Signer](client: FsClient[F, S]) {
  import eu.timepit.refined.auto.autoRefineV
  import io.bartholomews.fsclient.core.http.FsClientSttpExtensions._

  private[api] val basePath: Uri = apiUri / "v1" / "browse"

  type Limit = Int Refined Interval.Closed[1, 50]
  type RecommendationsLimit = Int Refined Interval.Closed[1, 1000]
  type Offset = Int Refined GreaterEqual[0]

  /**
    * Get All New Releases
    *
    * https://developer.spotify.com/documentation/web-api/reference/#endpoint-get-new-releases
    * Get a list of new album releases featured in Spotify (shown, for example, on a Spotify player’s “Browse” tab).
    *
    * @param country A country: an ISO 3166-1 alpha-2 country code.
    *                 Provide this parameter if you want the list of returned items to be relevant to a particular country.
    *                 If omitted, the returned items will be relevant to all countries.
    * @param limit   The maximum number of items to return. Default: 20. Minimum: 1. Maximum: 50.
    * @param offset  The index of the first item to return. Default: 0 (the first object).
    *                 Use with limit to get the next set of items.
    * @param signer The OAuth V2 Signer
    * @param responseHandler The sttp `ResponseAs` handler
    * @tparam E the Deserialization Error type
    * @return On success, the HTTP status code in the response header is 200 OK and the response body contains a message and an albums object.
    *          The albums object contains an array of simplified album objects (wrapped in a paging object) in JSON format.
    *          On error, the header status code is an error code and the response body contains an error object.
    *         Once you have retrieved the list, you can use Get an Album’s Tracks to drill down further.
    *         The results are returned in an order reflected within the Spotify clients, and therefore may not be ordered by date.
    */
  def getAllNewReleases[E](country: Option[CountryCodeAlpha2], limit: Limit = 20, offset: Offset = 0)(signer: SignerV2)(
    implicit responseHandler: ResponseHandler[E, NewReleases]
  ): F[SttpResponse[E, NewReleases]] = {
    val uri: Uri = (basePath / "new-releases")
      .withOptionQueryParam("country", country.map(_.value))
      .withQueryParam("limit", limit.value.toString)
      .withQueryParam("offset", offset.value.toString)

    baseRequest(client.userAgent)
      .get(uri)
      .sign(signer)
      .response(responseHandler)
      .send(client.backend)
  }

  /**
    * Get a list of Spotify featured playlists (shown, for example, on a Spotify player’s ‘Browse’ tab).
    * https://developer.spotify.com/documentation/web-api/reference/#endpoint-get-featured-playlists
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
    * @tparam E the Deserialization Error type
    * @return On success, the HTTP status code in the response header is 200 OK
    *          and the response body contains a message and a playlists object.
    *          The playlists object contains an array of simplified playlist objects
    *          (wrapped in a paging object) in JSON format.
    *          On error, the header status code is an error code and the response body contains an error object.
    *          Once you have retrieved the list of playlist objects,
    *          you can use Get a Playlist and Get a Playlist’s Tracks to drill down further.
    */
  def getFeaturedPlaylists[E](
    country: Option[CountryCodeAlpha2],
    locale: Option[Locale],
    timestamp: Option[LocalDateTime],
    limit: Limit = 20,
    offset: Offset = 0
  )(
    signer: SignerV2
  )(implicit responseHandler: ResponseHandler[E, FeaturedPlaylists]): F[SttpResponse[E, FeaturedPlaylists]] = {
    val uri: Uri = (basePath / "featured-playlists")
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
    * Get a list of categories used to tag items in Spotify (on, for example, the Spotify player’s “Browse” tab).
    * https://developer.spotify.com/documentation/web-api/reference/#endpoint-get-categories
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
    * @tparam E the Deserialization Error type
    * @return On success, the HTTP status code in the response header is 200 OK
    *         and the response body contains an object with a categories field,
    *         with an array of category objects (wrapped in a paging object) in JSON format.
    *         On error, the header status code is an error code and the response body contains an error object.
    *         Once you have retrieved the list, you can use `getCategory` to drill down further.
    */
  def getAllCategories[E](
    country: Option[CountryCodeAlpha2],
    locale: Option[Locale],
    limit: Limit = 20,
    offset: Offset = 0
  )(
    signer: SignerV2
  )(implicit responseHandler: ResponseHandler[E, CategoriesResponse]): F[SttpResponse[E, Page[Category]]] = {
    val uri: Uri = (basePath / "categories")
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
    * Get a single category used to tag items in Spotify (on, for example, the Spotify player’s “Browse” tab).
    * https://developer.spotify.com/documentation/web-api/reference/#endpoint-get-a-category
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
    * @tparam E the Deserialization Error type
    * @return On success, the HTTP status code in the response header is 200 OK
    *         and the response body contains a category object in JSON format.
    *         On error, the header status code is an error code and the response body contains an error object.
    *         Once you have retrieved the category, you can use `getCategoryPlaylists` to drill down further.
    */
  def getCategory[E](
    categoryId: SpotifyCategoryId,
    country: Option[CountryCodeAlpha2],
    locale: Option[Locale]
  )(
    signer: SignerV2
  )(implicit responseHandler: ResponseHandler[E, Category]): F[SttpResponse[E, Category]] = {
    val uri: Uri = (basePath / "categories" / categoryId.value)
      .withOptionQueryParam("country", country.map(_.value))
      .withOptionQueryParam("locale", locale.map(_.value))

    baseRequest(client.userAgent)
      .get(uri)
      .sign(signer)
      .response(responseHandler)
      .send(client.backend)
  }

  /**
    * Get a list of Spotify playlists tagged with a particular category.
    * https://developer.spotify.com/documentation/web-api/reference/#endpoint-get-a-categories-playlists
    *
    * @param categoryId Required. The Spotify category ID for the category.
    * @param country Optional. A country: an ISO 3166-1 alpha-2 country code.
    *                Provide this parameter to ensure that the category exists for a particular country.
    * @param limit Optional. The maximum number of items to return. Default: 20. Minimum: 1. Maximum: 50.
    * @param offset Optional. The index of the first item to return. Default: 0 (the first object).
    *               Use with limit to get the next set of items.
    * @param signer A valid user access token or your client credentials.
    * @param responseHandler The sttp `ResponseAs` handler
    * @tparam E the Deserialization Error type
    * @return On success, the HTTP status code in the response header is 200 OK
    *         and the response body contains an array of simplified playlist objects
    *         (wrapped in a paging object) in JSON format.
    *         On error, the header status code is an error code
    *         and the response body contains an error object.
    *         Once you have retrieved the list, you can use `getPlaylist` and `getPlaylistItems` to drill down further.
    */
  def getCategoryPlaylists[E](
    categoryId: SpotifyCategoryId,
    country: Option[CountryCodeAlpha2],
    limit: Limit = 20,
    offset: Offset = 0
  )(
    signer: SignerV2
  )(implicit responseHandler: ResponseHandler[E, PlaylistsResponse]): F[SttpResponse[E, Page[SimplePlaylist]]] = {
    val uri: Uri = (basePath / "categories" / categoryId.value)
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

  // TODO[FB] could improve the query with a min/max/target grouping
  def getRecommendations[E](
    limit: RecommendationsLimit = 20,
    market: Option[Market],
    seedArtists: List[SpotifyId],
    seedGenres: List[String],
    seedTracks: List[SpotifyId],
    acousticnessQuery: Option[AcousticnessQuery] = None,
    danceabilityQuery: Option[DanceabilityQuery] = None,
    durationQuery: Option[DurationQuery] = None,
    energyQuery: Option[EnergyQuery] = None,
    instrumentalnessQuery: Option[InstrumentalnessQuery] = None,
    keyQuery: Option[KeyQuery] = None,
    livenessQuery: Option[LivenessQuery] = None,
    loudnessQuery: Option[LoudnessQuery] = None,
    modeQuery: Option[ModeQuery] = None,
    popularityQuery: Option[PopularityQuery] = None,
    speechinessQuery: Option[SpeechinessQuery] = None,
    tempoQuery: Option[TempoQuery] = None,
    timeSignatureQuery: Option[TimeSignatureQuery] = None,
    valenceQuery: Option[ValenceQuery] = None
  )(
    signer: SignerV2
  )(implicit responseHandler: ResponseHandler[E, Recommendations]): F[SttpResponse[E, Recommendations]] = {
    val uri: Uri = (basePath / "recommendations")
      .withQueryParam("seed_artists", seedArtists.mkString(","))
      .withQueryParam("seed_genres", seedGenres.mkString(","))
      .withQueryParam("seed_tracks", seedTracks.mkString(","))
      .withQueryParam("limit", limit.value.toString)
      .withOptionQueryParam("market", market.map(_.value))
    // TODO[FB] all those audio features

    baseRequest(client.userAgent)
      .get(uri)
      .sign(signer)
      .response(responseHandler)
      .send(client.backend)
  }
}
