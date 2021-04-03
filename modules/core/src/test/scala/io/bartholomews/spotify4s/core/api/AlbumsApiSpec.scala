package io.bartholomews.spotify4s.core.api

import cats.data.NonEmptyList
import com.github.tomakehurst.wiremock.client.MappingBuilder
import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import com.softwaremill.diffx.scalatest.DiffMatcher.matchTo
import io.bartholomews.fsclient.core.http.SttpResponses.SttpResponse
import io.bartholomews.fsclient.core.oauth.NonRefreshableTokenSigner
import io.bartholomews.iso_country.CountryCodeAlpha2
import io.bartholomews.scalatestudo.WireWordSpec
import io.bartholomews.spotify4s.core.ServerBehaviours
import io.bartholomews.spotify4s.core.api.AlbumsApi.AlbumIds
import io.bartholomews.spotify4s.core.data.{FullAlbums, SimpleTracks}
import io.bartholomews.spotify4s.core.entities._
import io.bartholomews.spotify4s.core.utils.ClientData.{sampleClient, sampleNonRefreshableToken}
import sttp.client3.UriContext

abstract class AlbumsApiSpec[E[_], D[_], DE, J] extends WireWordSpec with ServerBehaviours[E, D, DE, J] {
  implicit val signer: NonRefreshableTokenSigner = sampleNonRefreshableToken
  implicit def fullAlbumDecoder: D[FullAlbum]
  implicit def fullAlbumsResponseDecoder: D[FullAlbumsResponse]
  implicit def simpleTrackDecoder: D[SimpleTrack]

  private val UK = CountryCodeAlpha2.UNITED_KINGDOM_OF_GREAT_BRITAIN_AND_NORTHERN_IRELAND

  "getAlbum" when {
    val albumId = FullAlbums.`Kind of Blue`.id
    def endpointRequest: MappingBuilder =
      get(urlPathEqualTo(s"$basePath/albums/${albumId.value}"))
        .withQueryParam("market", equalTo(UK.value))

    "country is defined" should {
      def request: SttpResponse[DE, FullAlbum] =
        sampleClient.albums.getAlbum[DE](
          id = albumId,
          country = Some(UK)
        )

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

      "return the correct entity" in matchResponseBody(stub, request) {
        case Right(album) => album should matchTo(FullAlbums.`Kind of Blue`)
      }
    }
  }

  "getAlbums" when {
    def endpointRequest: MappingBuilder =
      get(urlPathEqualTo(s"$basePath/albums"))
        .withQueryParam("market", equalTo(UK.value))

    "country is defined" should {
      val maybeAlbumIds = AlbumIds.fromNel(
        NonEmptyList.of(FullAlbums.`Kind of Blue`.id, FullAlbums.`In A Silent Way`.id)
      )

      def request: SttpResponse[DE, List[FullAlbum]] =
        sampleClient.albums.getAlbums[DE](
          ids = maybeAlbumIds.getOrElse(fail(s"$maybeAlbumIds")),
          country = Some(UK)
        )

      behave like clientReceivingUnexpectedResponse(endpointRequest, request)

      def stub: StubMapping =
        stubFor(
          endpointRequest
            .willReturn(
              aResponse()
                .withStatus(200)
                .withBodyFile("albums/albums_gb.json")
            )
        )

      "return the correct entity" in matchResponseBody(stub, request) {
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
      def request: SttpResponse[DE, Page[SimpleTrack]] =
        sampleClient.albums.getAlbumTracks[DE](id = albumId, country = Some(UK))

      behave like clientReceivingUnexpectedResponse(endpointRequest, request)

      def stub: StubMapping =
        stubFor(
          endpointRequest
            .willReturn(
              aResponse()
                .withStatus(200)
                .withBodyFile("albums/album_tracks.json")
            )
        )

      "return the correct entity" in matchResponseBody(stub, request) {
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
}
