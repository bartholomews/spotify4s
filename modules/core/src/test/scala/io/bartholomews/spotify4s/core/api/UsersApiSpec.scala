package io.bartholomews.spotify4s.core.api

import com.github.tomakehurst.wiremock.client.MappingBuilder
import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import io.bartholomews.fsclient.core.http.SttpResponses.SttpResponse
import io.bartholomews.fsclient.core.oauth.SignerV2
import io.bartholomews.scalatestudo.WireWordSpec
import io.bartholomews.scalatestudo.data.ClientData.v2.sampleNonRefreshableToken
import io.bartholomews.spotify4s.core.SpotifyServerBehaviours
import io.bartholomews.spotify4s.core.entities.SpotifyId.SpotifyUserId
import io.bartholomews.spotify4s.core.entities.{Page, PrivateUser, SimplePlaylist}
import io.bartholomews.spotify4s.core.utils.SpotifyClientData.sampleClient
import sttp.client3.Identity

abstract class UsersApiSpec[Encoder[_], Decoder[_], DE, J]
    extends WireWordSpec
    with SpotifyServerBehaviours[Encoder, Decoder, DE, J] {
  import eu.timepit.refined.auto.autoRefineV
  implicit val signer: SignerV2 = sampleNonRefreshableToken

  implicit def privateUserCodec: Decoder[PrivateUser]
  implicit def simplePlaylistCodec: Decoder[SimplePlaylist]

  "me" when {
    def endpoint: MappingBuilder = get(urlPathEqualTo(s"$basePath/me"))

    "successfully authenticated" should {
      def request: Identity[SttpResponse[DE, PrivateUser]] = sampleClient.users.me(signer)

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
        case response => response.body.map(_.id) shouldBe Right(SpotifyUserId("{f_}"))
      }
    }
  }

  "`getPlaylists`" when {
    def endpoint: MappingBuilder = get(urlPathEqualTo(s"$basePath/me/playlists"))

    "`limits` and `offset` query parameters are defined" should {
      def request: Identity[SttpResponse[DE, Page[SimplePlaylist]]] =
        sampleClient.users.getPlaylists[DE](limit = 2, offset = 5)(signer)

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

      "return the correct entity" in matchResponseBody(stub, request) {
        case Right(playlistsPage) =>
          playlistsPage.items.size shouldBe 2
          playlistsPage.items(1).name shouldBe "😗👌💨"
      }
    }
  }
}
