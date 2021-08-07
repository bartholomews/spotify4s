package io.bartholomews.spotify4s.core.api

import cats.data.NonEmptyList
import com.github.tomakehurst.wiremock.client.MappingBuilder
import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import io.bartholomews.fsclient.core.http.SttpResponses.SttpResponse
import io.bartholomews.fsclient.core.oauth.NonRefreshableTokenSigner
import io.bartholomews.scalatestudo.WireWordSpec
import io.bartholomews.scalatestudo.data.ClientData.v2.sampleNonRefreshableToken
import io.bartholomews.spotify4s.core.SpotifyServerBehaviours
import io.bartholomews.spotify4s.core.api.FollowApi.{ArtistsFollowingIds, UserIdsFollowingPlaylist, UsersFollowingIds}
import io.bartholomews.spotify4s.core.entities.SpotifyId.{SpotifyArtistId, SpotifyPlaylistId, SpotifyUserId}
import io.bartholomews.spotify4s.core.entities.{ArtistsResponse, FullArtist, Page, SpotifyId}
import io.bartholomews.spotify4s.core.utils.SpotifyClientData.sampleClient
import sttp.model.StatusCode

abstract class FollowApiSpec[E[_], D[_], DE, J] extends WireWordSpec with SpotifyServerBehaviours[E, D, DE, J] {
  import eu.timepit.refined.auto.autoRefineV

  implicit val signer: NonRefreshableTokenSigner = sampleNonRefreshableToken

  implicit def artistsResponseDecoder: D[ArtistsResponse]

  "followPlaylist" should {
    def endpointRequest: MappingBuilder = put(urlPathEqualTo(s"$basePath/playlists/2v3iNvBX8Ay1Gt2uXtUKUT/followers"))
    def request: SttpResponse[Nothing, Unit] =
      sampleClient.follow.followPlaylist(playlistId = SpotifyPlaylistId("2v3iNvBX8Ay1Gt2uXtUKUT"))(signer)

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
      sampleClient.follow.unfollowPlaylist(playlistId = SpotifyPlaylistId("2v3iNvBX8Ay1Gt2uXtUKUT"))(signer)

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
      sampleClient.follow
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

  "getFollowedArtists" should {
    def endpointRequest: MappingBuilder =
      get(urlPathEqualTo(s"$basePath/me/following"))
        .withQueryParam("type", equalTo("artist"))
        .withQueryParam("limit", equalTo("3"))
        .withQueryParam("after", equalTo("0f8MDDzIc6M4uH1xH0o0gy"))

    def request: SttpResponse[DE, Page[FullArtist]] =
      sampleClient.follow
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
    }
  }

  // TODO
  "followArtists" should {
    def endpointRequest: MappingBuilder =
      put(urlPathEqualTo(s"$basePath/me/following"))
        .withQueryParam("type", equalTo("artist"))
        .withQueryParam("ids", equalTo("0kbYTNQb4Pb1rPbbaF0pT4"))

    val artistsIds: ArtistsFollowingIds = ArtistsFollowingIds
      .fromNel(NonEmptyList.one(SpotifyArtistId("0kbYTNQb4Pb1rPbbaF0pT4")))
      .fold(fail(_), identity)

    def request: SttpResponse[Nothing, Unit] =
      sampleClient.follow.followArtists(ids = artistsIds)(signer)

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
      sampleClient.follow.followUsers(ids = usersIds)(signer)

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
      sampleClient.follow.unfollowArtists(ids = artistsIds)(signer)

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
      sampleClient.follow.unfollowUsers(ids = usersIds)(signer)

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
      sampleClient.follow.isFollowingArtists[DE](ids = artistsIds)(signer)

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
      sampleClient.follow.isFollowingUsers[DE](ids = usersIds)(signer)

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
}
