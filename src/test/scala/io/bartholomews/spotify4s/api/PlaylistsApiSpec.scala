package io.bartholomews.spotify4s.api

import cats.data.NonEmptyList
import com.github.tomakehurst.wiremock.client.MappingBuilder
import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import io.bartholomews.fsclient.core.http.SttpResponses.CirceJsonResponse
import io.bartholomews.fsclient.core.oauth.NonRefreshableTokenSigner
import io.bartholomews.iso_country.CountryCodeAlpha2
import io.bartholomews.scalatestudo.WireWordSpec
import io.bartholomews.spotify4s.DiffDerivations.matchTo
import io.bartholomews.spotify4s.api.SpotifyApi.SpotifyUris
import io.bartholomews.spotify4s.client.ClientData.{sampleClient, sampleNonRefreshableToken}
import io.bartholomews.spotify4s.entities.{
  CollectionLink,
  FullPlaylist,
  IsoCountry,
  Page,
  PublicUser,
  SimplePlaylist,
  SnapshotId,
  SpotifyId,
  SpotifyImage,
  SpotifyResourceUrl,
  SpotifyUri,
  SpotifyUserId
}
import io.circe.{Decoder, HCursor}
import org.scalatest.matchers.should.Matchers
import sttp.client.{HttpError, Response, UriContext}
import sttp.model.{StatusCode, Uri}

class PlaylistsApiSpec extends WireWordSpec with ServerBehaviours with Matchers {
  import cats.implicits._
  import eu.timepit.refined.auto._

  implicit val signer: NonRefreshableTokenSigner = sampleNonRefreshableToken

  "`replacePlaylistItems`" when {
    def endpoint: MappingBuilder = put(urlPathEqualTo(s"$basePath/users/playlists"))

    "`uris` query parameter is correct" should {
      val uri1 = SpotifyUri("spotify:track:4iV5W9uYEdYUVa79Axb7Rh")
      val uri2 = SpotifyUri("spotify:episode:512ojhOuo1ktJprKbVcKyQ")

      val maybeUris: Either[Throwable, SpotifyUris] =
        SpotifyUri.fromNel(NonEmptyList.of(uri1, uri2)).leftMap(msg => new Exception(msg))

      def request: Response[Either[HttpError, Unit]] =
        sampleClient.playlists.replacePlaylistItems(
          playlistId = SpotifyId("2LZYIzBoCXAdx8buWmUwQe"),
          uris = maybeUris.getOrElse(fail(s"$maybeUris"))
        )

      val endpointRequest =
        endpoint
          .withQueryParam("uris", equalTo(s"${uri1.value},${uri2.value}"))

      behave like clientReceivingUnexpectedResponse(endpointRequest, request, decodingBody = false)

      def stub: StubMapping =
        stubFor(endpointRequest.willReturn(aResponse().withStatus(201)))

      "return the correct entity" in matchIdResponse(stub, request) {
        case Response(Right(()), status, _, _, _) => status shouldBe StatusCode.Created
      }
    }

    "`uris` parameter is too large" should {
      val tooManyUris: List[SpotifyUri] =
        (1 to 101).map(_ => SpotifyUri("just one extra uri...")).toList

      "return a compile time error" in {
        val ex1: Either[String, SpotifyUris] = SpotifyUri.fromList(List.empty)
        assert(ex1 == Left("Predicate failed: need to provide at least one uri."))

        val ex2: Either[String, SpotifyUris] = SpotifyUri.fromNel(
          NonEmptyList.fromListUnsafe((1 to 101).map(_ => SpotifyUri("just one extra uri...")).toList)
        )
        assert(ex2 == Left("Predicate failed: a maximum of 100 uris can be set in one request."))

        inside(SpotifyUri.fromList(tooManyUris)) {
          case Left(error) => error shouldBe "Predicate failed: a maximum of 100 uris can be set in one request."
        }
      }
    }
  }

  "`getUserPlaylists`" when {
    def endpoint: MappingBuilder = get(urlPathEqualTo(s"$basePath/users/wizzler/playlists"))

    "`limits` and `offset` query parameters are defined" should {
      def request: CirceJsonResponse[Page[SimplePlaylist]] = sampleClient.playlists.getUserPlaylists(
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
                .withBodyFile("playlists/get_user_playlists.json")
            )
        )

      "return the correct entity" in matchResponseBody(stub, request) {
        case Right(playlistsPage: Page[SimplePlaylist]) =>
          playlistsPage should matchTo(
            Page[SimplePlaylist](
              href = uri"https://api.spotify.com/v1/users/wizzler/playlists?offset=5&limit=2",
              items = List(
                SimplePlaylist(
                  collaborative = false,
                  description = Some(
                    "Get psyched for your night out. The best tunes hand-picked by Barney Stinson himself. It's going to be legen... wait for it.... daaaaary!"
                  ),
                  externalUrls = SpotifyResourceUrl(
                    uri"https://open.spotify.com/playlist/2LZYIzBoCXAdx8buWmUwQe"
                  ),
                  href = uri"https://api.spotify.com/v1/playlists/2LZYIzBoCXAdx8buWmUwQe",
                  id = SpotifyId("2LZYIzBoCXAdx8buWmUwQe"),
                  images = List(
                    SpotifyImage(
                      height = Some(640),
                      url =
                        uri"https://mosaic.scdn.co/640/ab67616d0000b2731336b31b6a1799f0de5807acab67616d0000b2736a916e0b410279803d867fbdab67616d0000b273a51d5896371922e3fbe26d05ab67616d0000b273aa07223229c3f264be0ca653",
                      width = Some(640)
                    ),
                    SpotifyImage(
                      height = Some(300),
                      url =
                        uri"https://mosaic.scdn.co/300/ab67616d0000b2731336b31b6a1799f0de5807acab67616d0000b2736a916e0b410279803d867fbdab67616d0000b273a51d5896371922e3fbe26d05ab67616d0000b273aa07223229c3f264be0ca653",
                      width = Some(300)
                    ),
                    SpotifyImage(
                      height = Some(60),
                      url =
                        uri"https://mosaic.scdn.co/60/ab67616d0000b2731336b31b6a1799f0de5807acab67616d0000b2736a916e0b410279803d867fbdab67616d0000b273a51d5896371922e3fbe26d05ab67616d0000b273aa07223229c3f264be0ca653",
                      width = Some(60)
                    )
                  ),
                  name = "Barney's Get Psyched Mix",
                  owner = PublicUser(
                    displayName = Some("Ronald Pompa"),
                    externalUrls = SpotifyResourceUrl(uri"https://open.spotify.com/user/wizzler"),
                    followers = None,
                    href = uri"https://api.spotify.com/v1/users/wizzler",
                    id = SpotifyUserId("wizzler"),
                    images = List.empty,
                    uri = SpotifyUri("spotify:user:wizzler")
                  ),
                  public = Some(true),
                  snapshotId = "MzEsYjJiMWU4ZTlmZTU2MTcwYmNjZTMzMjdiMmQ4YjQwMTU1N2UzMjJhYg==",
                  tracks = CollectionLink(
                    href = SpotifyUri("https://api.spotify.com/v1/playlists/2LZYIzBoCXAdx8buWmUwQe/tracks"),
                    total = 11
                  ),
                  uri = SpotifyUri("spotify:playlist:2LZYIzBoCXAdx8buWmUwQe")
                ),
                SimplePlaylist(
                  collaborative = false,
                  description = Some("For medicinal use only!"),
                  externalUrls = SpotifyResourceUrl(
                    uri"https://open.spotify.com/playlist/7K0UB4wqdztK4jc3nrw9an"
                  ),
                  href = uri"https://api.spotify.com/v1/playlists/7K0UB4wqdztK4jc3nrw9an",
                  id = SpotifyId("7K0UB4wqdztK4jc3nrw9an"),
                  images = List(
                    SpotifyImage(
                      height = None,
                      url = uri"https://i.scdn.co/image/ab67706c0000da849899faadb08d52ace2662432",
                      width = None
                    )
                  ),
                  name = "😗👌💨",
                  owner = PublicUser(
                    displayName = Some("Ronald Pompa"),
                    externalUrls = SpotifyResourceUrl(uri"https://open.spotify.com/user/wizzler"),
                    followers = None,
                    href = uri"https://api.spotify.com/v1/users/wizzler",
                    id = SpotifyUserId("wizzler"),
                    images = List.empty,
                    uri = SpotifyUri("spotify:user:wizzler")
                  ),
                  public = Some(true),
                  snapshotId = "MjEsNTIzM2ZlOWU4YzZmYWYzZjgxOTFlMTEzOTA1YmFjN2E2OTlmYzRjNg==",
                  tracks = CollectionLink(
                    href = SpotifyUri("https://api.spotify.com/v1/playlists/7K0UB4wqdztK4jc3nrw9an/tracks"),
                    total = 14
                  ),
                  uri = SpotifyUri("spotify:playlist:7K0UB4wqdztK4jc3nrw9an")
                )
              ),
              limit = Some(2),
              next = Some("https://api.spotify.com/v1/users/wizzler/playlists?offset=7&limit=2"),
              offset = Some(5),
              previous = Some("https://api.spotify.com/v1/users/wizzler/playlists?offset=3&limit=2"),
              total = 8
            )
          )
      }
    }
  }

  "`changePlaylistDetails`" when {
    def endpoint: MappingBuilder =
      put(urlPathEqualTo(s"$basePath/playlists/2LZYIzBoCXAdx8buWmUwQe"))
        .withRequestBody(equalToJson("""
                                       |{
                                       | "name": "New name for Playlist",
                                       | "public": false
                                       |}
                                       |""".stripMargin))

    "updating `name` and `public` fields" should {
      def request: Response[Either[HttpError, Unit]] = sampleClient.playlists.changePlaylistDetails(
        playlistId = SpotifyId("2LZYIzBoCXAdx8buWmUwQe"),
        playlistName = Some("New name for Playlist"),
        public = Some(false),
        collaborative = None,
        description = None
      )

      behave like clientReceivingUnexpectedResponse(endpoint, request, decodingBody = false)

      def stub: StubMapping =
        stubFor(
          endpoint
            .willReturn(aResponse().withStatus(200))
        )

      "return the correct entity" in matchIdResponse(stub, request) {
        case Response(Right(()), status, _, _, _) => status shouldBe StatusCode.Ok
      }
    }
  }

  "`addTracksToPlaylist`" when {
    val uriTrack1 = SpotifyUri("spotify:track:4iV5W9uYEdYUVa79Axb7Rh")
    val uriTrack2 = SpotifyUri("spotify:track:1301WleyT98MSxVHPZCA6M")
    val uriTrack3 = SpotifyUri("spotify:episode:512ojhOuo1ktJprKbVcKyQ")
    def endpoint: MappingBuilder =
      post(urlPathEqualTo(s"$basePath/playlists/2LZYIzBoCXAdx8buWmUwQe/tracks"))
        .withRequestBody(equalToJson(s"""
                                       |{
                                       | "uris": [
                                       |    "${uriTrack1.value}",
                                       |    "${uriTrack2.value}",
                                       |    "${uriTrack3.value}"
                                       |  ],
                                       |  "position": 1
                                       |}
                                       |""".stripMargin))

    "adding tracks to a playlist at specified position" should {
      val maybeUris: Either[Throwable, SpotifyUris] =
        SpotifyUri
          .fromNel(NonEmptyList.of(uriTrack1, uriTrack2, uriTrack3))
          .leftMap(msg => new Exception(msg))

      def request: CirceJsonResponse[SnapshotId] =
        sampleClient.playlists.addTracksToPlaylist(
          playlistId = SpotifyId("2LZYIzBoCXAdx8buWmUwQe"),
          uris = maybeUris.getOrElse(fail(maybeUris.toString)),
          position = Some(1)
        )

      behave like clientReceivingUnexpectedResponse(endpoint, request)

      def stub: StubMapping =
        stubFor(
          endpoint
            .willReturn(
              aResponse()
                .withStatus(201)
                .withBodyFile("playlists/add_tracks_to_playlist.json")
            )
        )

      "return the correct entity" in matchResponseBody(stub, request) {
        case Right(snapshotId) =>
          snapshotId shouldBe SnapshotId("JbtmHBDBAYu3/bt8BOXKjzKx3i0b6LCa/wVjyl6qQ2Yf6nFXkbmzuEa+ZI/U1yF+")
      }
    }
  }

  "`getPlaylist`" when {
    def endpoint: MappingBuilder = get(urlPathEqualTo(s"$basePath/playlists/3cEYpjA9oz9GiPac4AsH4n"))

    "optional query parameters are not defined" should {
      def request: CirceJsonResponse[FullPlaylist] =
        sampleClient.playlists.getPlaylist(playlistId = SpotifyId("3cEYpjA9oz9GiPac4AsH4n"))
      behave like clientReceivingUnexpectedResponse(endpoint, request)

      def stub: StubMapping =
        stubFor(
          endpoint
            .willReturn(
              aResponse()
                .withStatus(200)
                .withBodyFile("playlists/get_playlist.json")
            )
        )

      "return the correct entity" in matchResponseBody(stub, request) {
        case Right(fullPlaylist) =>
          fullPlaylist.owner.displayName shouldBe Some("JMPerez²")
      }
    }

    "`fields` and `market` query parameters are defined" should {
      case class PartialPlaylist(description: String, href: Uri, tracks: List[PartialTrack])
      object PartialPlaylist {
        implicit val decoder: Decoder[PartialPlaylist] = (c: HCursor) =>
          for {
            description <- c.downField("description").as[String]
            href <- c.downField("href").as[Uri](Decoder.decodeString.emap(Uri.parse))
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

      def request: CirceJsonResponse[PartialPlaylist] = sampleClient.playlists.getPlaylistFields[PartialPlaylist](
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
                .withBodyFile("playlists/get_playlist_fields_2.json")
            )
        )

      "return the correct entity" in matchResponseBody(stub, request) {
        case Right(playlist) =>
          playlist shouldBe PartialPlaylist(
            description = "A playlist for testing pourposes",
            href =
              uri"https://api.spotify.com/v1/playlists/3cEYpjA9oz9GiPac4AsH4n?fields=href,description,tracks.items(added_by(id),track(name))",
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
      def request: CirceJsonResponse[FullPlaylist] = sampleClient.playlists.getPlaylist(
        customPlaylistId,
        market = None
      )

      def stub: StubMapping = {
        stubFor(
          get(urlPathEqualTo(s"$basePath/playlists/4YUV9hthjX0LOvg8Oe8w85"))
            .willReturn(
              aResponse()
                .withStatus(200)
                .withBodyFile(s"playlists/get_playlist_fields.json")
            )
        )
      }

      "return the correct entity" in matchResponseBody(stub, request) {
        case Right(playlist) =>
          playlist.uri.value shouldBe "spotify:playlist:4YUV9hthjX0LOvg8Oe8w85"
      }
    }
  }

  "`createPlaylist`" when {
    def endpoint: MappingBuilder =
      post(urlPathEqualTo(s"$basePath/users/thelinmichael/playlists"))
        .withRequestBody(equalToJson("""
            |{
            | "name": "A New Playlist",
            | "public": false,
            | "collaborative": false
            |}
            |""".stripMargin))

    "creating a private playlist with default parameters" should {
      def request: CirceJsonResponse[FullPlaylist] = sampleClient.playlists.createPlaylist(
        userId = SpotifyUserId("thelinmichael"),
        playlistName = "A New Playlist",
        public = false
      )

      behave like clientReceivingUnexpectedResponse(endpoint, request)

      def stub: StubMapping =
        stubFor(
          endpoint
            .willReturn(
              aResponse()
                .withStatus(200)
                .withBodyFile("playlists/create_playlist_response.json")
            )
        )

      "return the correct entity" in matchResponseBody(stub, request) {
        case Right(fullPlaylist) =>
          fullPlaylist.name shouldBe "A New Playlist"
          fullPlaylist.description shouldBe None
          fullPlaylist.owner.id shouldBe SpotifyUserId("thelinmichael")
          fullPlaylist.tracks.items shouldBe List.empty
      }
    }
  }
}
