package io.bartholomews.spotify4s.core.api

import cats.data.NonEmptySet
import com.github.tomakehurst.wiremock.client.MappingBuilder
import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import com.softwaremill.diffx.scalatest.DiffMatcher.matchTo
import io.bartholomews.fsclient.core.http.SttpResponses.SttpResponse
import io.bartholomews.fsclient.core.oauth.NonRefreshableTokenSigner
import io.bartholomews.iso.CountryCodeAlpha2
import io.bartholomews.scalatestudo.WireWordSpec
import io.bartholomews.scalatestudo.data.ClientData.v2.sampleNonRefreshableToken
import io.bartholomews.spotify4s.core.SpotifyServerBehaviours
import io.bartholomews.spotify4s.core.diff.SpotifyDiffDerivations
import io.bartholomews.spotify4s.core.entities.TimeInterval.Bar
import io.bartholomews.spotify4s.core.entities._
import io.bartholomews.spotify4s.core.utils.SpotifyClientData.{sampleClient, sampleSpotifyId}
import sttp.client3.UriContext

abstract class TracksApiSpec[E[_], D[_], DE, J]
    extends WireWordSpec
    with SpotifyServerBehaviours[E, D, DE, J]
    with SpotifyDiffDerivations {
  implicit val signer: NonRefreshableTokenSigner = sampleNonRefreshableToken
  implicit def audioAnalysisCodec: D[AudioAnalysis]
  implicit def audioFeaturesCodec: D[AudioFeatures]
  implicit def audioFeaturesResponseCodec: D[AudioFeaturesResponse]
  implicit def fullTrackCodec: D[FullTrack]
  implicit def fullTracksResponseCodec: D[FullTracksResponse]

  "`getAudioAnalysis`" should {
    def endpoint: MappingBuilder =
      get(urlPathEqualTo(s"$basePath/audio-analysis/${sampleSpotifyId.value}"))

    def request: SttpResponse[DE, AudioAnalysis] =
      sampleClient.tracks.getAudioAnalysis[DE](sampleSpotifyId)(signer)

    behave like clientReceivingUnexpectedResponse(endpoint, request)

    def stub: StubMapping =
      stubFor(
        endpoint
          .willReturn(
            aResponse()
              .withStatus(200)
              .withBodyFile("tracks/audio_analysis.json")
          )
      )

    "return the correct entity" in matchResponseBody(stub, request) {
      case Right(audioAnalysis) =>
        audioAnalysis.bars.head should matchTo(
          Bar(
            start = 0.06443,
            duration = 2.44911,
            confidence = Confidence(0.057)
          )
        )

        audioAnalysis.sections.head should matchTo(
          AudioSection(
            start = 0.0,
            duration = 23.33163,
            confidence = Confidence(1.0),
            loudness = -21.61,
            tempo = Tempo(
              value = 98.015,
              confidence = Confidence(0.782)
            ),
            key = AudioKey(value = Some(PitchClass(7)), confidence = Confidence(0.609)),
            mode = AudioMode(value = Modality.NoResult, confidence = Confidence(0.6)),
            timeSignature = TimeSignature(value = 4, confidence = Confidence(1))
          )
        )

        audioAnalysis.sections.last.mode should matchTo(
          AudioMode(value = Modality.Major, confidence = Confidence(0.566))
        )
    }
  }

  "`getAudioFeatures` for a track" should {
    def endpoint: MappingBuilder =
      get(urlPathEqualTo(s"$basePath/audio-features/${sampleSpotifyId.value}"))
    def request = sampleClient.tracks.getAudioFeatures[DE](sampleSpotifyId)(_)

    behave like clientReceivingUnexpectedResponse(endpoint, request(signer))

    def stub: StubMapping =
      stubFor(
        endpoint
          .willReturn(
            aResponse()
              .withStatus(200)
              .withBodyFile("tracks/audio_features.json")
          )
      )

    "return the correct entity" in matchResponseBody(stub, request(signer)) {
      case Right(audioFeatures) =>
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
          id = SpotifyId("06AKEBrKUckW0KREUWRnvT"),
          uri = SpotifyUri("spotify:track:06AKEBrKUckW0KREUWRnvT"),
          trackHref = uri"https://api.spotify.com/v1/tracks/06AKEBrKUckW0KREUWRnvT",
          analysisUrl = uri"https://api.spotify.com/v1/audio-analysis/06AKEBrKUckW0KREUWRnvT"
        )
    }
  }

  "`getAudioFeatures` for several tracks" should {
    def endpoint: MappingBuilder =
      get(urlPathEqualTo(s"$basePath/audio-features"))

    def request: SttpResponse[DE, List[AudioFeatures]] =
      sampleClient.tracks.getAudioFeatures[DE](
        NonEmptySet.of(
          SpotifyId("3n3Ppam7vgaVa1iaRUc9Lp"),
          SpotifyId("3twNvmDtFQtAd5gMKedhLD")
        )
      )(signer)

    behave like clientReceivingUnexpectedResponse(endpoint, request)

    def stub: StubMapping =
      stubFor(
        endpoint
          .willReturn(
            aResponse()
              .withStatus(200)
              .withBodyFile("tracks/audio_features_list.json")
          )
      )

    "return the correct entity" in matchResponseBody(stub, request) {
      case Right(af1 :: af2 :: Nil) =>
        af1 should matchTo(
          AudioFeatures(
            durationMs = 222200,
            key = PitchClass(1),
            mode = Modality.Major,
            timeSignature = 4,
            acousticness = Confidence(0.00119),
            danceability = Confidence(0.355),
            energy = Confidence(0.918),
            instrumentalness = Confidence(0),
            liveness = Confidence(0.0971),
            loudness = -4.36,
            speechiness = Confidence(0.0746),
            valence = Confidence(0.24),
            tempo = 148.114,
            id = SpotifyId("3n3Ppam7vgaVa1iaRUc9Lp"),
            uri = SpotifyUri("spotify:track:3n3Ppam7vgaVa1iaRUc9Lp"),
            trackHref = uri"https://api.spotify.com/v1/tracks/3n3Ppam7vgaVa1iaRUc9Lp",
            analysisUrl = uri"https://api.spotify.com/v1/audio-analysis/3n3Ppam7vgaVa1iaRUc9Lp"
          )
        )

        af2 should matchTo(
          AudioFeatures(
            durationMs = 197280,
            key = PitchClass(10),
            mode = Modality.Minor,
            timeSignature = 4,
            acousticness = Confidence(0.0000678),
            danceability = Confidence(0.502),
            energy = Confidence(0.972),
            instrumentalness = Confidence(0.000702),
            liveness = Confidence(0.0627),
            loudness = -3.96,
            speechiness = Confidence(0.0793),
            valence = Confidence(0.729),
            tempo = 138.019,
            id = SpotifyId("3twNvmDtFQtAd5gMKedhLD"),
            uri = SpotifyUri("spotify:track:3twNvmDtFQtAd5gMKedhLD"),
            trackHref = uri"https://api.spotify.com/v1/tracks/3twNvmDtFQtAd5gMKedhLD",
            analysisUrl = uri"https://api.spotify.com/v1/audio-analysis/3twNvmDtFQtAd5gMKedhLD"
          )
        )
    }
  }

  "`getTracks`" when {
    def endpoint: MappingBuilder = get(urlPathEqualTo(s"$basePath/tracks"))

    "market is not defined" should {
      def request: SttpResponse[DE, List[FullTrack]] =
        sampleClient.tracks.getTracks[DE](
          ids = NonEmptySet.of(
            SpotifyId("3n3Ppam7vgaVa1iaRUc9Lp"),
            SpotifyId("3twNvmDtFQtAd5gMKedhLD")
          ),
          market = None
        )(signer)

      val endpointRequest =
        endpoint
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

      "return the correct entity" in matchResponseBody(stub, request) {
        case Right(List(track1, track2)) =>
          track1.uri shouldBe SpotifyUri("spotify:track:3n3Ppam7vgaVa1iaRUc9Lp")
          track2.uri shouldBe SpotifyUri("spotify:track:3twNvmDtFQtAd5gMKedhLD")
      }
    }

    "market is defined" should {
      def request: SttpResponse[DE, List[FullTrack]] =
        sampleClient.tracks.getTracks[DE](
          ids = NonEmptySet.of(
            SpotifyId("3n3Ppam7vgaVa1iaRUc9Lp"),
            SpotifyId("3twNvmDtFQtAd5gMKedhLD")
          ),
          market = Some(IsoCountry(CountryCodeAlpha2.SPAIN))
        )(signer)

      val endpointRequest =
        endpoint
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

      "return the correct entity" in matchResponseBody(stub, request) {
        case Left(wat) =>
          succeed
        case Right(List(track1, track2)) =>
          track1.uri shouldBe SpotifyUri("spotify:track:3n3Ppam7vgaVa1iaRUc9Lp")
          track2.uri shouldBe SpotifyUri("spotify:track:3twNvmDtFQtAd5gMKedhLD")
          track2.availableMarkets shouldBe List.empty
      }
    }
  }

  "`getTrack`" when {
    val sampleTrackId: SpotifyId = SpotifyId("3n3Ppam7vgaVa1iaRUc9Lp")
    def endpoint(trackId: SpotifyId): MappingBuilder = get(urlPathEqualTo(s"$basePath/tracks/${trackId.value}"))

    "market is not defined" should {
      def request: SttpResponse[DE, FullTrack] =
        sampleClient.tracks.getTrack[DE](
          sampleTrackId,
          market = None
        )(signer)

      behave like clientReceivingUnexpectedResponse(endpoint(sampleTrackId), request)

      def stub: StubMapping =
        stubFor(
          endpoint(sampleTrackId)
            .willReturn(
              aResponse()
                .withStatus(200)
                .withBodyFile("tracks/track.json")
            )
        )

      "return the correct entity" in matchResponseBody(stub, request) {
        case Right(track) =>
          track.uri shouldBe SpotifyUri("spotify:track:3n3Ppam7vgaVa1iaRUc9Lp")
      }
    }

    "market is defined" should {
      def request: SttpResponse[DE, FullTrack] =
        sampleClient.tracks.getTrack[DE](
          sampleTrackId,
          market = Some(IsoCountry(CountryCodeAlpha2.SPAIN))
        )(signer)

      behave like clientReceivingUnexpectedResponse(endpoint(sampleTrackId), request)

      def stub: StubMapping =
        stubFor(
          endpoint(sampleTrackId)
            .withQueryParam("market", equalTo("ES"))
            .willReturn(
              aResponse()
                .withStatus(200)
                .withBodyFile("tracks/track_es.json")
            )
        )

      "return the correct entity" in matchResponseBody(stub, request) {
        case Right(track) =>
          track.uri shouldBe SpotifyUri("spotify:track:3n3Ppam7vgaVa1iaRUc9Lp")
      }
    }

    "market is defined as `from_token`" should {
      def request: SttpResponse[DE, FullTrack] =
        sampleClient.tracks.getTrack[DE](
          sampleTrackId,
          market = Some(FromToken)
        )(signer)

      behave like clientReceivingUnexpectedResponse(endpoint(sampleTrackId), request)

      def stub: StubMapping = {
        stubFor(
          endpoint(sampleTrackId)
            .withQueryParam("market", equalTo("from_token"))
            .willReturn(
              aResponse()
                .withStatus(200)
                .withBodyFile("tracks/track_relinking.json")
            )
        )
      }

      "return the correct entity" in matchResponseBody(stub, request) {
        case Right(track) =>
          track.uri shouldBe SpotifyUri("spotify:track:3n3Ppam7vgaVa1iaRUc9Lp")
      }
    }
  }
}
