package io.bartholomews.spotify4s.core.api

import eu.timepit.refined.api.Refined
import eu.timepit.refined.numeric.{GreaterEqual, Interval}
import io.bartholomews.fsclient.core.FsClient
import io.bartholomews.fsclient.core.http.SttpResponses.SttpResponse
import io.bartholomews.fsclient.core.oauth.v2.OAuthV2.ResponseHandler
import io.bartholomews.fsclient.core.oauth.{Signer, SignerV2}
import io.bartholomews.iso_country.CountryCodeAlpha2
import io.bartholomews.spotify4s.core.api.SpotifyApi.apiUri
import io.bartholomews.spotify4s.core.entities.NewReleases
import sttp.model.Uri

// https://developer.spotify.com/documentation/web-api/reference/browse
private[spotify4s] class BrowseApi[F[_], S <: Signer](client: FsClient[F, S]) {
  import eu.timepit.refined.auto.autoRefineV
  import io.bartholomews.fsclient.core.http.FsClientSttpExtensions._

  private[api] val basePath: Uri = apiUri / "v1" / "browse"

  type Limit = Int Refined Interval.Closed[1, 50]
  type Offset = Int Refined GreaterEqual[0]

  /**
    * Get a List of New Releases
    * https://developer.spotify.com/documentation/web-api/reference/browse/get-list-new-releases
    *
    * @param country Optional. A country: an ISO 3166-1 alpha-2 country code.
    *                (https://en.wikipedia.org/wiki/ISO_3166-1_alpha-2)
    *                Provide this parameter if you want the list of returned items
    *                to be relevant to a particular country.
    *                If omitted, the returned items will be relevant to all countries.
    * @param limit   Optional. The maximum number of items to return.
    *                Default: 20. Minimum: 1. Maximum: 50.
    * @param offset  Optional. The index of the first item to return.
    *                Default: 0 (the first object). Use with limit to get the next set of items.
    * @param signer  The OAuth V2 Signer
    * @return `NewReleases`
    */
  def getNewReleases[E](country: Option[CountryCodeAlpha2], limit: Limit = 20, offset: Offset = 0)(signer: SignerV2)(
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
}
