package io.bartholomews.spotify4s.core.api

import cats.data.NonEmptyList
import com.github.tomakehurst.wiremock.client.MappingBuilder
import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import io.bartholomews.fsclient.core.http.SttpResponses.SttpResponse
import io.bartholomews.fsclient.core.oauth.{NonRefreshableTokenSigner, SignerV2}
import io.bartholomews.iso.CountryCodeAlpha2
import io.bartholomews.scalatestudo.WireWordSpec
import io.bartholomews.scalatestudo.data.ClientData.v2.sampleNonRefreshableToken
import io.bartholomews.spotify4s.core.SpotifyServerBehaviours
import io.bartholomews.spotify4s.core.api.AlbumsApi.AlbumIds
import io.bartholomews.spotify4s.core.data.{FullAlbums, SimpleTracks}
import io.bartholomews.spotify4s.core.diff.SpotifyDiffDerivations
import io.bartholomews.spotify4s.core.entities._
import io.bartholomews.spotify4s.core.utils.SpotifyClientData.sampleClient
import sttp.client3.UriContext

import java.time.Month

abstract class AlbumsApiSpec[E[_], D[_], DE, J]
    extends WireWordSpec
    with SpotifyServerBehaviours[E, D, DE, J]
    with SpotifyDiffDerivations {
  import eu.timepit.refined.auto.autoRefineV

  implicit val signer: NonRefreshableTokenSigner = sampleNonRefreshableToken
  implicit def fullAlbumCodec: D[FullAlbum]
  implicit def fullAlbumsResponseCodec: D[FullAlbumsResponse]
  implicit def simpleTrackCodec: D[SimpleTrack]
  implicit def newReleasesCodec: D[NewReleases]

  private val UK = CountryCodeAlpha2.UNITED_KINGDOM_OF_GREAT_BRITAIN_AND_NORTHERN_IRELAND

  "getAlbum" when {
    val albumId = FullAlbums.`Kind of Blue`.id
    def endpointRequest: MappingBuilder =
      get(urlPathEqualTo(s"$basePath/albums/${albumId.value}"))
        .withQueryParam("market", equalTo(UK.value))

    "country is defined" should {
      def request: SignerV2 => SttpResponse[DE, FullAlbum] =
        sampleClient.albums.getAlbum[DE](
          id = albumId,
          market = Some(UK)
        )

      behave like clientReceivingUnexpectedResponse(endpointRequest, request(signer))

      def stub: StubMapping =
        stubFor(
          endpointRequest
            .willReturn(
              aResponse()
                .withStatus(200)
                .withBodyFile("albums/album_gb.json")
            )
        )

      "return the correct entity" in matchResponseBody(stub, request(signer)) {
        case Right(album) => album should matchTo(FullAlbums.`Kind of Blue`)
      }
    }
  }

  "getAlbums" when {
    def endpointRequest: MappingBuilder =
      get(urlPathEqualTo(s"$basePath/albums"))
        .withQueryParam("ids", equalTo("1weenld61qoidwYuZ1GESA,0Hs3BomCdwIWRhgT57x22T"))
        .withQueryParam("market", equalTo(UK.value))

    "country is defined" should {
      val albumIds: AlbumIds = AlbumIds
        .fromNel(
          NonEmptyList.of(FullAlbums.`Kind of Blue`.id, FullAlbums.`In A Silent Way`.id)
        )
        .fold(fail(_), identity)

      def request: SignerV2 => SttpResponse[DE, List[FullAlbum]] =
        sampleClient.albums.getAlbums[DE](
          ids = albumIds,
          market = Some(UK)
        )

      behave like clientReceivingUnexpectedResponse(endpointRequest, request(signer))

      def stub: StubMapping =
        stubFor(
          endpointRequest
            .willReturn(
              aResponse()
                .withStatus(200)
                .withBodyFile("albums/albums_gb.json")
            )
        )

      "return the correct entity" in matchResponseBody(stub, request(signer)) {
        case Right(album) =>
          album should matchTo(
            List(
              FullAlbums.`Kind of Blue`,
              FullAlbums.`In A Silent Way`
            )
          )
      }
    }
  }

  "getAlbumTracks" when {
    val albumId = FullAlbums.`Kind of Blue`.id

    def endpointRequest: MappingBuilder =
      get(urlPathEqualTo(s"$basePath/albums/${albumId.value}/tracks"))
        .withQueryParam("market", equalTo(UK.value))
        .withQueryParam("limit", equalTo("20"))
        .withQueryParam("offset", equalTo("0"))

    "country is defined" should {
      def request: SignerV2 => SttpResponse[DE, Page[SimpleTrack]] =
        sampleClient.albums.getAlbumTracks[DE](id = albumId, market = Some(UK))

      behave like clientReceivingUnexpectedResponse(endpointRequest, request(signer))

      def stub: StubMapping =
        stubFor(
          endpointRequest
            .willReturn(
              aResponse()
                .withStatus(200)
                .withBodyFile("albums/album_tracks.json")
            )
        )

      "return the correct entity" in matchResponseBody(stub, request(signer)) {
        case Right(simpleTracks) =>
          simpleTracks should matchTo(
            Page(
              href = uri"https://api.spotify.com/v1/albums/0Hs3BomCdwIWRhgT57x22T/tracks?offset=0&limit=20&market=GB",
              items = List(
                SimpleTracks.`Shhh / Peaceful`,
                SimpleTracks.`In a Silent Way`
              ),
              limit = Some(20),
              next = None,
              offset = Some(0),
              previous = None,
              total = 2
            )
          )
      }
    }
  }

  "getNewReleases" when {
    def endpoint: MappingBuilder = get(urlPathEqualTo(s"$basePath/browse/new-releases"))

    "all query parameters are defined" should {
      def request: SttpResponse[DE, NewReleases] =
        sampleClient.albums
          .getNewReleases[DE](country = Some(CountryCodeAlpha2.SWEDEN), limit = 2, offset = 5)(signer)

      val endpointRequest =
        endpoint
          .withQueryParam("country", equalTo("SE"))
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

      "return the correct entity" in matchResponseBody(stub, request) {
        case Right(NewReleases(albums)) =>
          albums.href shouldBe uri"https://api.spotify.com/v1/browse/new-releases?country=SE&offset=5&limit=2"
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
