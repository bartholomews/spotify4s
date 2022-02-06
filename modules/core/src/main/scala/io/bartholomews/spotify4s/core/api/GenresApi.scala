package io.bartholomews.spotify4s.core.api

import io.bartholomews.fsclient.core.FsClient
import io.bartholomews.fsclient.core.http.SttpResponses.{ResponseHandler, SttpResponse}
import io.bartholomews.fsclient.core.oauth.{Signer, SignerV2}
import io.bartholomews.spotify4s.core.api.SpotifyApi.basePath
import io.bartholomews.spotify4s.core.entities.{SpotifyGenre, SpotifyGenresResponse}
import sttp.model.Uri

private[spotify4s] class GenresApi[F[_], S <: Signer](client: FsClient[F, S]) {
  import io.bartholomews.fsclient.core.http.FsClientSttpExtensions._

  private[api] val recommendationsPath: Uri = basePath / "recommendations"

  /**
    * Get Available Genre Seeds
    * https://developer.spotify.com/documentation/web-api/reference/#/operations/get-recommendation-genres
    *
    * Retrieve a list of available genres seed parameter values for recommendations.
    *
    * @param signer A valid user access token or your client credentials.
    * @param responseHandler The sttp `ResponseAs` handler
    * @tparam DE the Deserialization Error type
    * @return On success, the HTTP status code in the response header is 200 OK
    *         and the response body contains a recommendations response object in JSON format.
    */
  def getAvailableGenreSeeds[DE](
    signer: SignerV2
  )(implicit responseHandler: ResponseHandler[DE, SpotifyGenresResponse]): F[SttpResponse[DE, List[SpotifyGenre]]] =
    baseRequest(client.userAgent)
      .get(recommendationsPath / "available-genre-seeds")
      .sign(signer)
      .response(responseHandler)
      .mapResponseRight(_.genres)
      .send(client.backend)
}
