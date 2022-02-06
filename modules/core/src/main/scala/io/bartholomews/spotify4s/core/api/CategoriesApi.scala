package io.bartholomews.spotify4s.core.api

import eu.timepit.refined.api.Refined
import eu.timepit.refined.numeric.Interval
import io.bartholomews.fsclient.core.FsClient
import io.bartholomews.fsclient.core.http.SttpResponses.{ResponseHandler, SttpResponse}
import io.bartholomews.fsclient.core.oauth.{Signer, SignerV2}
import io.bartholomews.iso.CountryCodeAlpha2
import io.bartholomews.spotify4s.core.api.CategoriesApi.Limit
import io.bartholomews.spotify4s.core.api.SpotifyApi.{basePath, Offset}
import io.bartholomews.spotify4s.core.entities._
import sttp.model.Uri

// https://developer.spotify.com/documentation/web-api/reference/#category-browse
private[spotify4s] class CategoriesApi[F[_], S <: Signer](client: FsClient[F, S]) {
  import eu.timepit.refined.auto.autoRefineV
  import io.bartholomews.fsclient.core.http.FsClientSttpExtensions._

  private[api] val browsePath: Uri = basePath / "browse"

  /**
    * Get Several Browse Categories
    * https://developer.spotify.com/documentation/web-api/reference/#/operations/get-categories
    *
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
  def getBrowseCategories[DE](
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
    * Get Single Browse Category
    * https://developer.spotify.com/documentation/web-api/reference/#/operations/get-a-category
    *
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
  def getBrowseCategory[DE](
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
}

object CategoriesApi {
  type Limit = Int Refined Interval.Closed[1, 50]
}
