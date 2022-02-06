package io.bartholomews.spotify4s.core.api

import com.github.tomakehurst.wiremock.client.MappingBuilder
import com.github.tomakehurst.wiremock.client.WireMock.{aResponse, get, stubFor, urlPathEqualTo}
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import io.bartholomews.fsclient.core.http.SttpResponses.SttpResponse
import io.bartholomews.fsclient.core.oauth.NonRefreshableTokenSigner
import io.bartholomews.scalatestudo.WireWordSpec
import io.bartholomews.scalatestudo.data.ClientData.v2.sampleNonRefreshableToken
import io.bartholomews.spotify4s.core.SpotifyServerBehaviours
import io.bartholomews.spotify4s.core.entities.{SpotifyGenre, SpotifyGenresResponse}
import io.bartholomews.spotify4s.core.utils.SpotifyClientData.sampleClient

abstract class GenresApiSpec[E[_], D[_], DE, J] extends WireWordSpec with SpotifyServerBehaviours[E, D, DE, J] {
  implicit val signer: NonRefreshableTokenSigner = sampleNonRefreshableToken
  implicit def spotifyGenresResponseCodec: D[SpotifyGenresResponse]

  "getAvailableGenreSeeds" should {
    def endpointRequest: MappingBuilder = get(urlPathEqualTo(s"$basePath/recommendations/available-genre-seeds"))

    def request: SttpResponse[DE, List[SpotifyGenre]] =
      sampleClient.genres.getAvailableGenreSeeds[DE](signer)

    behave like clientReceivingUnexpectedResponse(endpointRequest, request)

    def stub: StubMapping =
      stubFor(
        endpointRequest
          .willReturn(
            aResponse()
              .withStatus(200)
              .withBodyFile("browse/recommendation_genres.json")
          )
      )

    "return the correct entity" in matchResponseBody(stub, request) {
      case Right(genres) =>
        genres should contain(SpotifyGenre("death-metal"))
    }
  }
}
