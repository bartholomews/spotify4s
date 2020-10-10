package io.bartholomews.spotify4s.api

import cats.effect.IO
import com.github.tomakehurst.wiremock.client.MappingBuilder
import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import io.bartholomews.fsclient.entities.FsResponse
import io.bartholomews.fsclient.entities.oauth.NonRefreshableToken
import io.bartholomews.fsclient.utils.HttpTypes.HttpResponse
import io.bartholomews.spotify4s.client.ClientData.sampleClient
import io.bartholomews.spotify4s.entities.{Page, SimplePlaylist, SpotifyUserId}
import io.bartholomews.scalatestudo.WireWordSpec
import io.bartholomews.scalatestudo.data.TestudoFsClientData.OAuthV2

class UsersApiSpec extends WireWordSpec with ServerBehaviours {
  import eu.timepit.refined.auto.autoRefineV
  implicit val signer: NonRefreshableToken = OAuthV2.sampleNonRefreshableToken

  "`me`" when {
    def endpoint: MappingBuilder = get(urlPathEqualTo(s"$basePath/me"))

    "successfully authenticated" should {
      val request = sampleClient.users.me

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

      "return the correct entity" in matchResponse(stub, request) {
        case FsResponse(_, _, Right(privateUser)) =>
          privateUser.id shouldBe SpotifyUserId("{f_}")
      }
    }
  }

  "`getPlaylists`" when {
    def endpoint: MappingBuilder = get(urlPathEqualTo(s"$basePath/me/playlists"))

    "`limits` and `offset` query parameters are defined" should {
      val request: IO[HttpResponse[Page[SimplePlaylist]]] = sampleClient.users.getPlaylists(
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

      "return the correct entity" in matchResponse(stub, request) {
        case FsResponse(_, _, Right(playlistsPage)) =>
          playlistsPage.items.size shouldBe 2
          playlistsPage.items(1).name shouldBe "😗👌💨"
      }
    }
  }
}
