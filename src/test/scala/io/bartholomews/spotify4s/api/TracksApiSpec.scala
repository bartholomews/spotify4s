package io.bartholomews.spotify4s.api

import cats.data.NonEmptyList
import com.github.tomakehurst.wiremock.client.MappingBuilder
import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import io.bartholomews.fsclient.entities.FsResponse
import io.bartholomews.fsclient.entities.oauth.NonRefreshableToken
import io.bartholomews.fsclient.utils.HttpTypes.IOResponse
import io.bartholomews.iso_country.CountryCodeAlpha2
import io.bartholomews.spotify4s.client.ClientData.{sampleClient, sampleSpotifyId}
import io.bartholomews.spotify4s.entities.{
  AudioAnalysis,
  AudioFeatures,
  AudioKey,
  AudioMode,
  Bar,
  Confidence,
  FromToken,
  FullTrack,
  IsoCountry,
  Modality,
  PitchClass,
  SpotifyUri,
  SpotifyUserId
}
import io.bartholomews.testudo.WireWordSpec
import io.bartholomews.testudo.data.TestudoFsClientData.OAuthV2
import org.http4s.Uri
import org.scalatest.BeforeAndAfterEach

class TracksApiSpec extends WireWordSpec with ServerBehaviours with BeforeAndAfterEach {
  implicit val signer: NonRefreshableToken = OAuthV2.sampleNonRefreshableToken

  "`getAudioAnalysis`" should {
    def getAudioAnalysisEndpoint: MappingBuilder =
      get(urlPathEqualTo(s"$basePath/audio-analysis/${sampleSpotifyId.value}"))
    val request: IOResponse[AudioAnalysis] = sampleClient.tracks.getAudioAnalysis(sampleSpotifyId)

    behave like clientReceivingUnexpectedResponse(getAudioAnalysisEndpoint, request)

    def stub: StubMapping =
      stubFor(
        getAudioAnalysisEndpoint
          .willReturn(
            aResponse()
              .withStatus(200)
              .withBodyFile("tracks/audio_analysis.json")
          )
      )

    "return the correct entity" in matchResponse(stub, request) {
      case FsResponse(_, _, Right(audioAnalysis)) =>
        audioAnalysis.bars.head should matchTo(
          Bar(
            start = 0.06443,
            duration = 2.44911,
            confidence = Confidence(0.057)
          )
        )
        audioAnalysis.sections.head.key should matchTo(
          AudioKey(
            value = Some(PitchClass(7)),
            confidence = Confidence(0.609)
          )
        )
        audioAnalysis.sections.head.mode should matchTo(
          AudioMode(
            value = Modality.NoResult,
            confidence = Confidence(0.6)
          )
        )
        audioAnalysis.sections.last.mode should matchTo(
          AudioMode(value = Modality.Major, confidence = Confidence(0.566))
        )
    }
  }

  "`getAudioFeatures`" should {
    def getAudioFeaturesEndpoint: MappingBuilder =
      get(urlPathEqualTo(s"$basePath/audio-features/${sampleSpotifyId.value}"))
    val request: IOResponse[AudioFeatures] = sampleClient.tracks.getAudioFeatures(sampleSpotifyId)

    behave like clientReceivingUnexpectedResponse(getAudioFeaturesEndpoint, request)

    def stub: StubMapping =
      stubFor(
        getAudioFeaturesEndpoint
          .willReturn(
            aResponse()
              .withStatus(200)
              .withBodyFile("tracks/audio_features.json")
          )
      )

    "return the correct entity" in matchResponse(stub, request) {
      case FsResponse(_, _, Right(audioFeatures)) =>
        audioFeatures shouldBe AudioFeatures(
          durationMs = 255349,
          key = PitchClass(5),
          mode = Modality.Minor,
          timeSignature = 4,
          acousticness = Confidence(0.514),
          danceability = Confidence(0.735),
          energy = Confidence(0.578),
          instrumentalness = Confidence(0.0902),
          liveness = Confidence(0.159),
          loudness = -11.84,
          speechiness = Confidence(0.0461),
          valence = Confidence(0.636),
          tempo = 98.002,
          id = SpotifyUserId("06AKEBrKUckW0KREUWRnvT"),
          uri = SpotifyUri("spotify:track:06AKEBrKUckW0KREUWRnvT"),
          trackHref = Uri.unsafeFromString("https://api.spotify.com/v1/tracks/06AKEBrKUckW0KREUWRnvT"),
          analysisUrl = Uri.unsafeFromString("https://api.spotify.com/v1/audio-analysis/06AKEBrKUckW0KREUWRnvT")
        )
    }
  }

  "`getTracks`" when {
    def getTracksEndpoint: MappingBuilder = get(urlPathEqualTo(s"$basePath/tracks"))

    "market is not defined" should {
      val request: IOResponse[List[FullTrack]] = sampleClient.tracks.getTracks(
        ids = NonEmptyList.of(
          SpotifyUserId("3n3Ppam7vgaVa1iaRUc9Lp"),
          SpotifyUserId("3twNvmDtFQtAd5gMKedhLD")
        ),
        market = None
      )

      val endpointRequest =
        getTracksEndpoint
          .withQueryParam(
            "ids",
            equalTo("3n3Ppam7vgaVa1iaRUc9Lp,3twNvmDtFQtAd5gMKedhLD")
          )

      behave like clientReceivingUnexpectedResponse(endpointRequest, request)

      def stub: StubMapping =
        stubFor(
          endpointRequest
            .willReturn(
              aResponse()
                .withStatus(200)
                .withBodyFile("tracks/tracks.json")
            )
        )

      "return the correct entity" in matchResponse(stub, request) {
        case FsResponse(_, _, Right(List(track1, track2))) =>
          track1.uri shouldBe SpotifyUri("spotify:track:3n3Ppam7vgaVa1iaRUc9Lp")
          track2.uri shouldBe SpotifyUri("spotify:track:3twNvmDtFQtAd5gMKedhLD")
      }
    }

    "market is defined" should {
      val request: IOResponse[List[FullTrack]] = sampleClient.tracks.getTracks(
        ids = NonEmptyList.of(
          SpotifyUserId("3n3Ppam7vgaVa1iaRUc9Lp"),
          SpotifyUserId("3twNvmDtFQtAd5gMKedhLD")
        ),
        market = Some(IsoCountry(CountryCodeAlpha2.SPAIN))
      )

      val endpointRequest =
        getTracksEndpoint
          .withQueryParam("ids", equalTo("3n3Ppam7vgaVa1iaRUc9Lp,3twNvmDtFQtAd5gMKedhLD"))
          .withQueryParam("market", equalTo("ES"))

      behave like clientReceivingUnexpectedResponse(endpointRequest, request)

      def stub: StubMapping =
        stubFor(
          endpointRequest
            .willReturn(
              aResponse()
                .withStatus(200)
                .withBodyFile("tracks/tracks_es.json")
            )
        )

      "return the correct entity" in matchResponse(stub, request) {
        case FsResponse(_, _, Right(List(track1, track2))) =>
          track1.uri shouldBe SpotifyUri("spotify:track:3n3Ppam7vgaVa1iaRUc9Lp")
          track2.uri shouldBe SpotifyUri("spotify:track:3twNvmDtFQtAd5gMKedhLD")
          track2.availableMarkets shouldBe List.empty
      }
    }
  }

  "`getTrack`" when {
    val sampleTrackId = SpotifyUserId("3n3Ppam7vgaVa1iaRUc9Lp")
    def getTrackEndpoint: MappingBuilder = get(urlPathEqualTo(s"$basePath/tracks/${sampleTrackId.value}"))

    "market is not defined" should {
      val request: IOResponse[FullTrack] = sampleClient.tracks.getTrack(
        sampleTrackId,
        market = None
      )

      behave like clientReceivingUnexpectedResponse(getTrackEndpoint, request)

      def stub: StubMapping =
        stubFor(
          getTrackEndpoint
            .willReturn(
              aResponse()
                .withStatus(200)
                .withBodyFile("tracks/track.json")
            )
        )

      "return the correct entity" in matchResponse(stub, request) {
        case FsResponse(_, _, Right(track)) =>
          track.uri shouldBe SpotifyUri("spotify:track:3n3Ppam7vgaVa1iaRUc9Lp")
      }
    }

    "market is defined" should {
      val request: IOResponse[FullTrack] = sampleClient.tracks.getTrack(
        sampleTrackId,
        market = Some(IsoCountry(CountryCodeAlpha2.SPAIN))
      )

      behave like clientReceivingUnexpectedResponse(getTrackEndpoint, request)

      def stub: StubMapping =
        stubFor(
          getTrackEndpoint
            .withQueryParam("market", equalTo("ES"))
            .willReturn(
              aResponse()
                .withStatus(200)
                .withBodyFile("tracks/track_es.json")
            )
        )

      "return the correct entity" in matchResponse(stub, request) {
        case FsResponse(_, _, Right(track)) =>
          track.uri shouldBe SpotifyUri("spotify:track:3n3Ppam7vgaVa1iaRUc9Lp")
      }
    }

    "market is defined as `from_token`" should {
      val request: IOResponse[FullTrack] = sampleClient.tracks.getTrack(
        sampleTrackId,
        market = Some(FromToken)
      )

      behave like clientReceivingUnexpectedResponse(getTrackEndpoint, request)

      def stub: StubMapping = {
        stubFor(
          getTrackEndpoint
            .withQueryParam("market", equalTo("from_token"))
            .willReturn(
              aResponse()
                .withStatus(200)
                .withBodyFile("tracks/track_relinking.json")
            )
        )
      }

      "return the correct entity" in matchResponse(stub, request) {
        case FsResponse(_, _, Right(track)) =>
          track.uri shouldBe SpotifyUri("spotify:track:3n3Ppam7vgaVa1iaRUc9Lp")
      }
    }
  }
}
