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

abstract class AlbumsApiSpec[E[_], D[_], DE, J]
    extends WireWordSpec
    with SpotifyServerBehaviours[E, D, DE, J]
    with SpotifyDiffDerivations {
  implicit val signer: NonRefreshableTokenSigner = sampleNonRefreshableToken
  implicit def fullAlbumCodec: D[FullAlbum]
  implicit def fullAlbumsResponseCodec: D[FullAlbumsResponse]
  implicit def simpleTrackCodec: D[SimpleTrack]

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
          country = Some(UK)
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
          country = Some(UK)
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
        sampleClient.albums.getAlbumTracks[DE](id = albumId, country = Some(UK))

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
}
