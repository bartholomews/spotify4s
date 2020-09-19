package io.bartholomews.spotify4s.api

import java.time.Month

import com.github.tomakehurst.wiremock.client.MappingBuilder
import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import io.bartholomews.fsclient.entities.FsResponse
import io.bartholomews.fsclient.entities.oauth.NonRefreshableToken
import io.bartholomews.fsclient.utils.HttpTypes.IOResponse
import io.bartholomews.iso_country.CountryCodeAlpha2
import io.bartholomews.spotify4s.client.ClientData.sampleClient
import io.bartholomews.spotify4s.entities.{AlbumType, NewReleases, ReleaseDate}
import io.bartholomews.scalatestudo.WireWordSpec
import io.bartholomews.scalatestudo.data.TestudoFsClientData.OAuthV2

class BrowseApiSpec extends WireWordSpec with ServerBehaviours {
  import eu.timepit.refined.auto.autoRefineV

  implicit val signer: NonRefreshableToken = OAuthV2.sampleNonRefreshableToken

  "`getNewReleases`" when {
    def endpoint: MappingBuilder = get(urlPathEqualTo(s"$basePath/browse/new-releases"))

    "country is defined" should {
      val request: IOResponse[NewReleases] = sampleClient.browse.getNewReleases(
        country = Some(CountryCodeAlpha2.SWEDEN),
        limit = 2,
        offset = 5
      )

      val endpointRequest =
        endpoint
          .withQueryParam("limit", equalTo("2"))
          .withQueryParam("offset", equalTo("5"))

      behave like clientReceivingUnexpectedResponse(endpointRequest, request)

      def stub: StubMapping =
        stubFor(
          endpointRequest
            .willReturn(
              aResponse()
                .withStatus(200)
                .withBodyFile("browse/new_releases_se.json")
            )
        )

      "return the correct entity" in matchResponse(stub, request) {
        case FsResponse(_, _, Right(NewReleases(albums))) =>
          albums.href shouldBe "https://api.spotify.com/v1/browse/new-releases?country=SE&offset=5&limit=2"
          albums.next shouldBe Some("https://api.spotify.com/v1/browse/new-releases?country=SE&offset=7&limit=2")
          albums.items.size shouldBe 2
          albums.items.head.albumType shouldBe Some(AlbumType.Single)
          albums.items.head.availableMarkets.head shouldBe CountryCodeAlpha2.ANDORRA
          albums.items.map(_.releaseDate).head shouldBe Some(
            ReleaseDate(
              year = 2020,
              month = Some(Month.APRIL),
              dayOfMonth = Some(17)
            )
          )
      }
    }
  }
}
