package io.bartholomews.spotify4s.api

import cats.effect.IO
import com.github.tomakehurst.wiremock.client.MappingBuilder
import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import io.bartholomews.fsclient.entities.FsResponse
import io.bartholomews.fsclient.entities.oauth.NonRefreshableToken
import io.bartholomews.fsclient.utils.HttpTypes.{HttpResponse, IOResponse}
import io.bartholomews.iso_country.CountryCodeAlpha2
import io.bartholomews.scalatestudo.WireWordSpec
import io.bartholomews.scalatestudo.data.TestudoFsClientData.OAuthV2
import io.bartholomews.spotify4s.client.ClientData.sampleClient
import io.bartholomews.spotify4s.entities.{FullPlaylist, IsoCountry, Page, SimplePlaylist, SpotifyId, SpotifyUserId}
import io.circe.{Decoder, HCursor}
import org.http4s.Uri

class PlaylistsApiSpec extends WireWordSpec with ServerBehaviours {
  import eu.timepit.refined.auto.autoRefineV

  implicit val signer: NonRefreshableToken = OAuthV2.sampleNonRefreshableToken

  "`getUserPlaylists`" when {
    def endpoint: MappingBuilder = get(urlPathEqualTo(s"$basePath/users/wizzler/playlists"))

    "`limits` and `offset` query parameters are defined" should {
      val request: IO[HttpResponse[Page[SimplePlaylist]]] = sampleClient.playlists.getUserPlaylists(
        userId = SpotifyUserId("wizzler"),
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
                .withBodyFile("playlists/user_playlists.json")
            )
        )

      "return the correct entity" in matchResponse(stub, request) {
        case FsResponse(_, _, Right(playlistsPage)) =>
          playlistsPage.items.size shouldBe 2
          playlistsPage.items(1).name shouldBe "😗👌💨"
      }
    }
  }

  "`getPlaylist`" when {
    def endpoint: MappingBuilder = get(urlPathEqualTo(s"$basePath/playlists/3cEYpjA9oz9GiPac4AsH4n"))

    "optional query parameters are not defined" should {
      val request = sampleClient.playlists.getPlaylist(playlistId = SpotifyId("3cEYpjA9oz9GiPac4AsH4n"))
      behave like clientReceivingUnexpectedResponse(endpoint, request)

      def stub: StubMapping =
        stubFor(
          endpoint
            .willReturn(
              aResponse()
                .withStatus(200)
                .withBodyFile("playlists/playlist.json")
            )
        )

      "return the correct entity" in matchResponse(stub, request) {
        case FsResponse(_, _, Right(fullPlaylist)) =>
          fullPlaylist.owner.displayName shouldBe Some("JMPerez²")
      }
    }

    "`fields` and `market` query parameters are defined" should {
      case class PartialPlaylist(description: String, href: Uri, tracks: List[PartialTrack])
      object PartialPlaylist {
        implicit val decoder: Decoder[PartialPlaylist] = (c: HCursor) =>
          for {
            description <- c.downField("description").as[String]
            href <- c.downField("href").as[Uri](org.http4s.circe.decodeUri)
            tracks <- c.downField("tracks").downField("items").as[List[PartialTrack]]
          } yield PartialPlaylist(description, href, tracks)
      }

      case class PartialTrack(trackName: String, addedBy: String)
      object PartialTrack {
        implicit val decoder: Decoder[PartialTrack] = (c: HCursor) =>
          for {
            trackName <- c.downField("track").downField("name").as[String]
            addedBy <- c.downField("added_by").downField("id").as[String]
          } yield PartialTrack(trackName, addedBy)
      }

      val request: IO[HttpResponse[PartialPlaylist]] = sampleClient.playlists.getPlaylistFields(
        playlistId = SpotifyId("3cEYpjA9oz9GiPac4AsH4n"),
        fields = "href,description,tracks.items(added_by(id),track(name))",
        market = Some(IsoCountry(CountryCodeAlpha2.SPAIN))
      )

      val endpointRequest =
        endpoint
          .withQueryParam("fields", equalTo("href,description,tracks.items(added_by(id),track(name))"))
          .withQueryParam("market", equalTo("ES"))

      behave like clientReceivingUnexpectedResponse(endpointRequest, request)

      def stub: StubMapping =
        stubFor(
          endpointRequest
            .willReturn(
              aResponse()
                .withStatus(200)
                .withBodyFile("playlists/playlist_es_tracks_added_by_name.json")
            )
        )

      "return the correct entity" in matchResponse(stub, request) {
        case FsResponse(_, _, Right(playlist)) =>
          playlist shouldBe PartialPlaylist(
            description = "A playlist for testing pourposes",
            href = Uri.unsafeFromString(
              "https://api.spotify.com/v1/playlists/3cEYpjA9oz9GiPac4AsH4n?fields=href,description,tracks.items(added_by(id),track(name))"
            ),
            tracks = List(
              PartialTrack(addedBy = "jmperezperez", trackName = "Api"),
              PartialTrack(addedBy = "jmperezperez", trackName = "Is"),
              PartialTrack(addedBy = "jmperezperez", trackName = "All I Want"),
              PartialTrack(addedBy = "jmperezperez", trackName = "Endpoints"),
              PartialTrack(addedBy = "jmperezperez", trackName = "You Are So Beautiful")
            )
          )
      }
    }

    "entity has many null fields" should {
      val customPlaylistId = SpotifyId("4YUV9hthjX0LOvg8Oe8w85")
      val request: IOResponse[FullPlaylist] = sampleClient.playlists.getPlaylist(
        customPlaylistId,
        market = None
      )

      def stub: StubMapping = {
        stubFor(
          get(urlPathEqualTo(s"$basePath/playlists/4YUV9hthjX0LOvg8Oe8w85"))
            .willReturn(
              aResponse()
                .withStatus(200)
                .withBodyFile(s"playlists/${customPlaylistId.value}.json")
            )
        )
      }

      "return the correct entity" in matchResponse(stub, request) {
        case FsResponse(_, _, Right(playlist)) =>
          playlist.uri.value shouldBe "spotify:playlist:4YUV9hthjX0LOvg8Oe8w85"
      }
    }
  }
}
