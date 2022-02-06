package io.bartholomews.spotify4s.core.api

import cats.data.NonEmptyList
import com.github.tomakehurst.wiremock.client.MappingBuilder
import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import io.bartholomews.fsclient.core.http.SttpResponses.SttpResponse
import io.bartholomews.fsclient.core.oauth.SignerV2
import io.bartholomews.scalatestudo.WireWordSpec
import io.bartholomews.scalatestudo.data.ClientData.v2.sampleNonRefreshableToken
import io.bartholomews.spotify4s.core.SpotifyServerBehaviours
import io.bartholomews.spotify4s.core.api.UsersApi._
import io.bartholomews.spotify4s.core.entities.SpotifyId.{SpotifyArtistId, SpotifyPlaylistId, SpotifyUserId}
import io.bartholomews.spotify4s.core.entities._
import io.bartholomews.spotify4s.core.utils.SpotifyClientData.sampleClient
import sttp.client3.Identity
import sttp.model.StatusCode

abstract class UsersApiSpec[Encoder[_], Decoder[_], DE, J]
    extends WireWordSpec
    with SpotifyServerBehaviours[Encoder, Decoder, DE, J] {
  import eu.timepit.refined.auto.autoRefineV

  implicit val signer: SignerV2 = sampleNonRefreshableToken

  implicit def artistsResponseCodec: Decoder[ArtistsResponse]
  implicit def privateUserCodec: Decoder[PrivateUser]
  implicit def publicUserCodec: Decoder[PublicUser]

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

  "getUserTopItems" ignore {}

  "getUserProfile" should {
    def endpoint: MappingBuilder = get(urlPathEqualTo(s"$basePath/users/test"))

    def request: Identity[SttpResponse[DE, PublicUser]] =
      sampleClient.users.getUserProfile(SpotifyUserId("test"))(signer)

    behave like clientReceivingUnexpectedResponse(endpoint, request)

    def stub: StubMapping =
      stubFor(
        endpoint
          .willReturn(
            aResponse()
              .withStatus(200)
              .withBodyFile("users/user_profile.json")
          )
      )

    "return the correct entity" in matchIdResponse(stub, request) {
      case response => response.body.map(_.id) shouldBe Right(SpotifyUserId("test"))
    }
  }

  "followPlaylist" should {
    def endpointRequest: MappingBuilder = put(urlPathEqualTo(s"$basePath/playlists/2v3iNvBX8Ay1Gt2uXtUKUT/followers"))
    def request: SttpResponse[Nothing, Unit] =
      sampleClient.users.followPlaylist(playlistId = SpotifyPlaylistId("2v3iNvBX8Ay1Gt2uXtUKUT"))(signer)

    behave like clientReceivingUnexpectedResponse(endpointRequest, request, decodingBody = false)

    def stub: StubMapping =
      stubFor(
        endpointRequest
          .willReturn(aResponse().withStatus(200))
      )

    "return the correct entity" in matchIdResponse[Nothing, Unit](stub, request) {
      case response =>
        response.body shouldBe Right(())
        response.code shouldBe StatusCode.Ok
    }
  }

  "unfollowPlaylist" should {
    def endpointRequest: MappingBuilder =
      delete(urlPathEqualTo(s"$basePath/playlists/2v3iNvBX8Ay1Gt2uXtUKUT/followers"))
    def request: SttpResponse[Nothing, Unit] =
      sampleClient.users.unfollowPlaylist(playlistId = SpotifyPlaylistId("2v3iNvBX8Ay1Gt2uXtUKUT"))(signer)

    behave like clientReceivingUnexpectedResponse(endpointRequest, request, decodingBody = false)

    def stub: StubMapping =
      stubFor(
        endpointRequest
          .willReturn(aResponse().withStatus(200))
      )

    "return the correct entity" in matchIdResponse[Nothing, Unit](stub, request) {
      case response =>
        response.body shouldBe Right(())
        response.code shouldBe StatusCode.Ok
    }
  }

  "getFollowedArtists" should {
    def endpointRequest: MappingBuilder =
      get(urlPathEqualTo(s"$basePath/me/following"))
        .withQueryParam("type", equalTo("artist"))
        .withQueryParam("limit", equalTo("3"))
        .withQueryParam("after", equalTo("0f8MDDzIc6M4uH1xH0o0gy"))

    def request: SttpResponse[DE, CursorPage[SpotifyArtistId, FullArtist]] =
      sampleClient.users
        .getFollowedArtists(after = Some(SpotifyArtistId("0f8MDDzIc6M4uH1xH0o0gy")), limit = 3)(signer)

    behave like clientReceivingUnexpectedResponse(endpointRequest, request, decodingBody = false)

    def stub: StubMapping =
      stubFor(
        endpointRequest
          .willReturn(
            aResponse()
              .withStatus(200)
              .withBodyFile("follow/user_followed_artists.json")
          )
      )

    "return the correct entity" in matchResponseBody(stub, request) {
      case Right(page) =>
        page.items.map(_.name) shouldBe List("Trent Reznor", "Pink Floyd", "Miles Davis")
        page.cursors.after shouldBe SpotifyArtistId("0kbYTNQb4Pb1rPbbaF0pT4")
    }
  }

  "followArtists" should {
    def endpointRequest: MappingBuilder =
      put(urlPathEqualTo(s"$basePath/me/following"))
        .withQueryParam("type", equalTo("artist"))
        .withQueryParam("ids", equalTo("0kbYTNQb4Pb1rPbbaF0pT4"))

    val artistsIds: ArtistsFollowingIds = ArtistsFollowingIds
      .fromNel(NonEmptyList.one(SpotifyArtistId("0kbYTNQb4Pb1rPbbaF0pT4")))
      .fold(fail(_), identity)

    def request: SttpResponse[Nothing, Unit] =
      sampleClient.users.followArtists(ids = artistsIds)(signer)

    behave like clientReceivingUnexpectedResponse(endpointRequest, request, decodingBody = false)

    def stub: StubMapping =
      stubFor(
        endpointRequest
          .willReturn(aResponse().withStatus(204))
      )

    "return the correct entity" in matchIdResponse[Nothing, Unit](stub, request) {
      case response =>
        response.body shouldBe Right(())
        response.code shouldBe StatusCode.NoContent
    }
  }

  "followUsers" should {
    def endpointRequest: MappingBuilder =
      put(urlPathEqualTo(s"$basePath/me/following"))
        .withQueryParam("type", equalTo("user"))
        .withQueryParam("ids", equalTo("exampleuser01"))

    val usersIds: UsersFollowingIds = UsersFollowingIds
      .fromNel(
        NonEmptyList.one(SpotifyUserId("exampleuser01"))
      )
      .fold(fail(_), identity)

    def request: SttpResponse[Nothing, Unit] =
      sampleClient.users.followUsers(ids = usersIds)(signer)

    behave like clientReceivingUnexpectedResponse(endpointRequest, request, decodingBody = false)

    def stub: StubMapping =
      stubFor(
        endpointRequest
          .willReturn(aResponse().withStatus(204))
      )

    "return the correct entity" in matchIdResponse[Nothing, Unit](stub, request) {
      case response =>
        response.body shouldBe Right(())
        response.code shouldBe StatusCode.NoContent
    }
  }

  "unfollowArtists" should {
    def endpointRequest: MappingBuilder =
      delete(urlPathEqualTo(s"$basePath/me/following"))
        .withQueryParam("type", equalTo("artist"))
        .withQueryParam("ids", equalTo("0kbYTNQb4Pb1rPbbaF0pT4"))

    val artistsIds: ArtistsFollowingIds = ArtistsFollowingIds
      .fromNel(NonEmptyList.one(SpotifyArtistId("0kbYTNQb4Pb1rPbbaF0pT4")))
      .fold(fail(_), identity)

    def request: SttpResponse[Nothing, Unit] =
      sampleClient.users.unfollowArtists(ids = artistsIds)(signer)

    behave like clientReceivingUnexpectedResponse(endpointRequest, request, decodingBody = false)

    def stub: StubMapping =
      stubFor(
        endpointRequest
          .willReturn(aResponse().withStatus(204))
      )

    "return the correct entity" in matchIdResponse[Nothing, Unit](stub, request) {
      case response =>
        response.body shouldBe Right(())
        response.code shouldBe StatusCode.NoContent
    }
  }

  "unfollowUsers" should {
    def endpointRequest: MappingBuilder =
      delete(urlPathEqualTo(s"$basePath/me/following"))
        .withQueryParam("type", equalTo("user"))
        .withQueryParam("ids", equalTo("exampleuser01"))

    val usersIds: UsersFollowingIds = UsersFollowingIds
      .fromNel(
        NonEmptyList.one(SpotifyUserId("exampleuser01"))
      )
      .fold(fail(_), identity)

    def request: SttpResponse[Nothing, Unit] =
      sampleClient.users.unfollowUsers(ids = usersIds)(signer)

    behave like clientReceivingUnexpectedResponse(endpointRequest, request, decodingBody = false)

    def stub: StubMapping =
      stubFor(
        endpointRequest
          .willReturn(aResponse().withStatus(204))
      )

    "return the correct entity" in matchIdResponse[Nothing, Unit](stub, request) {
      case response =>
        response.body shouldBe Right(())
        response.code shouldBe StatusCode.NoContent
    }
  }

  "isFollowingArtists" should {
    def endpointRequest: MappingBuilder =
      get(urlPathEqualTo(s"$basePath/me/following/contains"))
        .withQueryParam("type", equalTo("artist"))
        .withQueryParam("ids", equalTo("0kbYTNQb4Pb1rPbbaF0pT4"))

    val artistsIds: ArtistsFollowingIds = ArtistsFollowingIds
      .fromNel(NonEmptyList.one(SpotifyArtistId("0kbYTNQb4Pb1rPbbaF0pT4")))
      .fold(fail(_), identity)

    def request: SttpResponse[DE, Map[SpotifyArtistId, Boolean]] =
      sampleClient.users.isFollowingArtists[DE](ids = artistsIds)(signer)

    behave like clientReceivingUnexpectedResponse(endpointRequest, request, decodingBody = false)

    def stub: StubMapping =
      stubFor(
        endpointRequest
          .willReturn(
            aResponse()
              .withStatus(200)
              .withBodyFile("follow/following_state_for_artists.json")
          )
      )

    "return the correct entity" in matchIdResponse(stub, request) {
      case response =>
        response.body shouldBe Right(Map(SpotifyArtistId("0kbYTNQb4Pb1rPbbaF0pT4") -> true))
        response.code shouldBe StatusCode.Ok
    }
  }

  "isFollowingUsers" should {
    def endpointRequest: MappingBuilder =
      get(urlPathEqualTo(s"$basePath/me/following/contains"))
        .withQueryParam("type", equalTo("user"))
        .withQueryParam("ids", equalTo("exampleuser01"))

    val usersIds: UsersFollowingIds = UsersFollowingIds
      .fromNel(
        NonEmptyList.one(SpotifyUserId("exampleuser01"))
      )
      .fold(fail(_), identity)

    def request: SttpResponse[DE, Map[SpotifyUserId, Boolean]] =
      sampleClient.users.isFollowingUsers[DE](ids = usersIds)(signer)

    behave like clientReceivingUnexpectedResponse(endpointRequest, request, decodingBody = false)

    def stub: StubMapping =
      stubFor(
        endpointRequest
          .willReturn(
            aResponse()
              .withStatus(200)
              .withBodyFile("follow/following_state_for_users.json")
          )
      )

    "return the correct entity" in matchIdResponse(stub, request) {
      case response =>
        response.body shouldBe Right(Map(SpotifyUserId("exampleuser01") -> false))
        response.code shouldBe StatusCode.Ok
    }
  }

  "usersFollowingPlaylist" should {
    def endpointRequest: MappingBuilder =
      get(urlPathEqualTo(s"$basePath/playlists/2v3iNvBX8Ay1Gt2uXtUKUT/followers/contains"))
        .withQueryParam("ids", equalTo("jmperezperez,thelinmichael,wizzler,{f_}"))

    val usersIds: UserIdsFollowingPlaylist = UserIdsFollowingPlaylist
      .fromNel(
        NonEmptyList.of(
          SpotifyUserId("jmperezperez"),
          SpotifyUserId("thelinmichael"),
          SpotifyUserId("wizzler"),
          SpotifyUserId("{f_}")
        )
      )
      .fold(fail(_), identity)

    def request: SttpResponse[DE, Map[SpotifyUserId, Boolean]] =
      sampleClient.users
        .usersFollowingPlaylist[DE](playlistId = SpotifyPlaylistId("2v3iNvBX8Ay1Gt2uXtUKUT"), usersIds)(signer)

    behave like clientReceivingUnexpectedResponse(endpointRequest, request, decodingBody = false)

    def stub: StubMapping =
      stubFor(
        endpointRequest
          .willReturn(
            aResponse()
              .withStatus(200)
              .withBodyFile("follow/users_following_playlist.json")
          )
      )

    "return the correct entity" in matchIdResponse(stub, request) {
      case response =>
        response.body shouldBe Right(
          Map(
            SpotifyUserId("jmperezperez") -> false,
            SpotifyUserId("thelinmichael") -> false,
            SpotifyUserId("wizzler") -> false,
            SpotifyUserId("{f_}") -> true
          )
        )
        response.code shouldBe StatusCode.Ok
    }
  }
}
