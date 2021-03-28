package io.bartholomews.spotify4s.core.api

import com.github.tomakehurst.wiremock.client.MappingBuilder
import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import com.softwaremill.diffx.scalatest.DiffMatcher.matchTo
import io.bartholomews.fsclient.core.http.SttpResponses.SttpResponse
import io.bartholomews.fsclient.core.oauth.NonRefreshableTokenSigner
import io.bartholomews.iso_country.CountryCodeAlpha2
import io.bartholomews.scalatestudo.WireWordSpec
import io.bartholomews.spotify4s.core.ServerBehaviours
import io.bartholomews.spotify4s.core.data.{SimpleArtists, SimpleTracks}
import io.bartholomews.spotify4s.core.entities.ExternalResourceUrl.SpotifyResourceUrl
import io.bartholomews.spotify4s.core.entities._
import io.bartholomews.spotify4s.core.utils.ClientData.{sampleClient, sampleNonRefreshableToken}
import sttp.client3.UriContext

import java.time.Month

abstract class AlbumsApiSpec[E[_], D[_], DE] extends WireWordSpec with ServerBehaviours[E, D, DE] {
  // import eu.timepit.refined.auto.autoRefineV

  implicit val signer: NonRefreshableTokenSigner = sampleNonRefreshableToken
  implicit def fullAlbumDecoder: D[FullAlbum]

  "getAlbum" when {
    val albumId = SpotifyId("")
    def endpoint: MappingBuilder = get(urlPathEqualTo(s"$basePath/albums/${albumId.value}"))

    "country is defined" should {
      def request: SttpResponse[DE, FullAlbum] =
        sampleClient.albums.getAlbum[DE](
          id = albumId,
          country = Some(CountryCodeAlpha2.UNITED_KINGDOM_OF_GREAT_BRITAIN_AND_NORTHERN_IRELAND)
        )

      val endpointRequest =
        endpoint
          .withQueryParam(
            "market",
            equalTo(CountryCodeAlpha2.UNITED_KINGDOM_OF_GREAT_BRITAIN_AND_NORTHERN_IRELAND.value)
          )
//          .withQueryParam("limit", equalTo("2"))
//          .withQueryParam("offset", equalTo("5"))

      behave like clientReceivingUnexpectedResponse(endpointRequest, request)

      def stub: StubMapping =
        stubFor(
          endpointRequest
            .willReturn(
              aResponse()
                .withStatus(200)
                .withBodyFile("albums/album_gb.json")
            )
        )

      val expected = FullAlbum(
        albumType = AlbumType.Album,
        artists = List(SimpleArtists.`Miles Davis`),
        availableMarkets = List.empty,
        copyrights = List(Copyright(text = "Originally released 1959 Sony Music Entertainment Inc.", `type` = "P")),
        externalIds = ExternalIds.UPC("5099749522428"),
        externalUrls = SpotifyResourceUrl(uri"https://open.spotify.com/album/1weenld61qoidwYuZ1GESA"),
        genres = List.empty,
        href = uri"https://api.spotify.com/v1/albums/1weenld61qoidwYuZ1GESA",
        id = SpotifyId("1weenld61qoidwYuZ1GESA"),
        images = List(
          SpotifyImage(
            height = Some(640),
            url = uri"https://i.scdn.co/image/ab67616d0000b2737ab89c25093ea3787b1995b4",
            width = Some(640)
          ),
          SpotifyImage(
            height = Some(300),
            url = uri"https://i.scdn.co/image/ab67616d00001e027ab89c25093ea3787b1995b4",
            width = Some(300)
          ),
          SpotifyImage(
            height = Some(64),
            url = uri"https://i.scdn.co/image/ab67616d000048517ab89c25093ea3787b1995b4",
            width = Some(64)
          )
        ),
        label = "Columbia",
        name = "Kind Of Blue",
        popularity = 56,
        releaseDate = ReleaseDate(
          year = 1959,
          month = Some(Month.AUGUST),
          dayOfMonth = Some(17)
        ),
        restrictions = None,
        tracks = Page(
          href = uri"https://api.spotify.com/v1/albums/1weenld61qoidwYuZ1GESA/tracks?offset=0&limit=50&market=GB",
          items = List(
            SimpleTracks.`So What`,
            SimpleTracks.`Freddie Freeloader`,
            SimpleTracks.`Blue in Green`,
            SimpleTracks.`All Blues`,
            SimpleTracks.`Flamenco Sketches`
          ),
          limit = Some(50),
          next = None,
          offset = Some(0),
          previous = None,
          total = 5
        ),
        uri = SpotifyUri("spotify:album:1weenld61qoidwYuZ1GESA")
      )

      "return the correct entity" in matchResponseBody(stub, request) {
        case Right(album) => album should matchTo(expected)
      }
    }
  }
}
