package io.bartholomews.spotify4s.circe.api

import com.github.tomakehurst.wiremock.client.MappingBuilder
import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import io.bartholomews.fsclient.core.oauth.NonRefreshableTokenSigner
import io.bartholomews.spotify4s.CirceWordSpec
import io.bartholomews.spotify4s.client.ClientData.{sampleClient, sampleNonRefreshableToken}
import io.bartholomews.spotify4s.core.entities.SpotifyUserId
import sttp.client.Response

class UsersApiSpec extends CirceWordSpec with ServerBehaviours {
  import eu.timepit.refined.auto.autoRefineV
  implicit val signer: NonRefreshableTokenSigner = sampleNonRefreshableToken

  import io.bartholomews.spotify4s.circe._

  "me" when {
    def endpoint: MappingBuilder = get(urlPathEqualTo(s"$basePath/me"))

    "successfully authenticated" should {
      def request =
        sampleClient.users.me

      behave like clientReceivingUnexpectedResponse(endpoint, request)

      def stub: StubMapping =
        stubFor(
          endpoint
            .willReturn(
              aResponse()
                .withStatus(200)
                .withBodyFile("users/me.json")
            )
        )

      "return the correct entity" in matchIdResponse(stub, request) {
        case Response(Right(privateUser), _, _, _, _) =>
          privateUser.id shouldBe SpotifyUserId("{f_}")
      }
    }
  }

  "`getPlaylists`" when {
    def endpoint: MappingBuilder = get(urlPathEqualTo(s"$basePath/me/playlists"))

    "`limits` and `offset` query parameters are defined" should {
      def request = sampleClient.users.getPlaylists(
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
                .withBodyFile("playlists/get_user_playlists.json")
            )
        )

      "return the correct entity" in matchIdResponse(stub, request) {
        case Response(Right(playlistsPage), _, _, _, _) =>
          playlistsPage.items.size shouldBe 2
          playlistsPage.items(1).name shouldBe "😗👌💨"
      }
    }
  }
}
