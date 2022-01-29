package io.bartholomews.spotify4s.core.api

import cats.data.NonEmptySet
import com.github.tomakehurst.wiremock.client.MappingBuilder
import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import eu.timepit.refined.api.Refined
import io.bartholomews.fsclient.core.http.SttpResponses.SttpResponse
import io.bartholomews.fsclient.core.oauth.NonRefreshableTokenSigner
import io.bartholomews.iso.CountryCodeAlpha2
import io.bartholomews.scalatestudo.WireWordSpec
import io.bartholomews.scalatestudo.data.ClientData.v2.sampleNonRefreshableToken
import io.bartholomews.spotify4s.core.SpotifyServerBehaviours
import io.bartholomews.spotify4s.core.api.TracksApi.{AudioFeaturesTrackIds, TrackIds}
import io.bartholomews.spotify4s.core.diff.SpotifyDiffDerivations
import io.bartholomews.spotify4s.core.entities.SpotifyId.{SpotifyArtistId, SpotifyTrackId}
import io.bartholomews.spotify4s.core.entities.TimeInterval.Bar
import io.bartholomews.spotify4s.core.entities._
import io.bartholomews.spotify4s.core.utils.SpotifyClientData.{sampleClient, sampleSpotifyId, sampleSpotifyTrackId}
import sttp.client3.UriContext

import scala.concurrent.duration._

abstract class TracksApiSpec[E[_], D[_], DE, J]
    extends WireWordSpec
    with SpotifyServerBehaviours[E, D, DE, J]
    with SpotifyDiffDerivations {
  import eu.timepit.refined.auto.autoRefineV

  implicit val signer: NonRefreshableTokenSigner = sampleNonRefreshableToken
  implicit def audioAnalysisCodec: D[AudioAnalysis]
  implicit def audioFeaturesCodec: D[AudioFeatures]
  implicit def audioFeaturesResponseCodec: D[AudioFeaturesResponse]
  implicit def fullTrackCodec: D[FullTrack]
  implicit def fullTracksResponseCodec: D[FullTracksResponse]
  implicit def recommendationsCodec: D[Recommendations]

  "`getTrack`" when {
    val sampleTrackId: SpotifyTrackId = SpotifyTrackId("3n3Ppam7vgaVa1iaRUc9Lp")
    def endpoint(trackId: SpotifyTrackId): MappingBuilder = get(urlPathEqualTo(s"$basePath/tracks/${trackId.value}"))

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

  "`getTracks`" when {
    def endpoint: MappingBuilder = get(urlPathEqualTo(s"$basePath/tracks"))

    val trackIds: TrackIds = TrackIds
      .fromNes(
        NonEmptySet.of(
          SpotifyTrackId("3n3Ppam7vgaVa1iaRUc9Lp"),
          SpotifyTrackId("3twNvmDtFQtAd5gMKedhLD")
        )
      )
      .fold(fail(_), identity)

    "market is not defined" should {
      def request: SttpResponse[DE, List[FullTrack]] =
        sampleClient.tracks.getTracks[DE](
          ids = trackIds,
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
          ids = trackIds,
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

  "`getAudioFeatures` for several tracks" should {
    def endpoint: MappingBuilder =
      get(urlPathEqualTo(s"$basePath/audio-features"))

    def request: SttpResponse[DE, List[AudioFeatures]] =
      sampleClient.tracks.getAudioFeatures[DE](
        AudioFeaturesTrackIds
          .fromNes(
            NonEmptySet.of(
              SpotifyTrackId("3n3Ppam7vgaVa1iaRUc9Lp"),
              SpotifyTrackId("3twNvmDtFQtAd5gMKedhLD")
            )
          )
          .fold(fail(_), identity)
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

  "`getAudioFeatures` for a track" should {
    def endpoint: MappingBuilder =
      get(urlPathEqualTo(s"$basePath/audio-features/${sampleSpotifyTrackId.value}"))
    def request = sampleClient.tracks.getAudioFeatures[DE](sampleSpotifyTrackId)(_)

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

  "`getAudioAnalysis`" should {
    def endpoint: MappingBuilder =
      get(urlPathEqualTo(s"$basePath/audio-analysis/${sampleSpotifyId.value}"))

    def request: SttpResponse[DE, AudioAnalysis] =
      sampleClient.tracks.getAudioAnalysis[DE](sampleSpotifyTrackId)(signer)

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

  "getRecommendations" when {
    def endpoint: MappingBuilder = get(urlPathEqualTo(s"$basePath/recommendations"))

    "all query parameters are defined" should {
      def request: SttpResponse[DE, Recommendations] =
        sampleClient.tracks.getRecommendations[DE](
          limit = 2,
          market = Some(IsoCountry(CountryCodeAlpha2.SWEDEN)),
          recommendationSeedRequest = Refined.unsafeApply(
            List(
              RecommendationSeedQuery.RecommendationSeedArtist(SpotifyArtistId("0kbYTNQb4Pb1rPbbaF0pT4")),
              RecommendationSeedQuery.RecommendationSeedGenre(SpotifyGenre("workout")),
              RecommendationSeedQuery.RecommendationSeedGenre(SpotifyGenre("jazz")),
              RecommendationSeedQuery.RecommendationSeedGenre(SpotifyGenre("drone")),
              RecommendationSeedQuery.RecommendationSeedTrack(SpotifyTrackId("267lVml7gJ9xefwgO6E2Ag"))
            )
          ),
          audioFeaturesQuery = AudioFeaturesQuery(
            acousticness = AudioFeatureParams(
              min = Some(Confidence(0.2)),
              max = Some(Confidence(0.9)),
              target = Some(Confidence(0.5))
            ),
            danceability = AudioFeatureParams(
              min = Some(Confidence(0.2)),
              max = Some(Confidence(0.9)),
              target = Some(Confidence(0.5))
            ),
            duration = AudioFeatureParams(
              min = Some(60.seconds),
              max = Some(5.minutes),
              target = Some(4.minutes)
            ),
            energy = AudioFeatureParams(
              min = Some(Confidence(0.2)),
              max = Some(Confidence(0.9)),
              target = Some(Confidence(0.5))
            ),
            instrumentalness = AudioFeatureParams(
              min = Some(Confidence(0.2)),
              max = Some(Confidence(0.9)),
              target = Some(Confidence(0.5))
            ),
            key = AudioFeatureParams(
              min = Some(2),
              max = Some(2),
              target = Some(2)
            ),
            liveness = AudioFeatureParams(
              min = Some(Confidence(0.2)),
              max = Some(Confidence(0.9)),
              target = Some(Confidence(0.5))
            ),
            loudness = AudioFeatureParams(
              min = Some(-30.0),
              max = Some(30.0),
              target = Some(-10.0)
            ),
            mode = AudioFeatureParams(
              min = Some(1),
              max = Some(1),
              target = Some(1)
            ),
            popularity = AudioFeatureParams(
              min = Some(5),
              max = Some(30),
              target = Some(20)
            ),
            speechiness = AudioFeatureParams(
              min = Some(Confidence(0.0)),
              max = Some(Confidence(0.3)),
              target = Some(Confidence(0.1))
            ),
            tempo = AudioFeatureParams(
              min = Some(24.0),
              max = Some(200.0),
              target = Some(120.0)
            ),
            timeSignature = AudioFeatureParams(
              min = Some(2),
              max = Some(8),
              target = Some(4)
            ),
            valence = AudioFeatureParams(
              min = Some(Confidence(0.2)),
              max = Some(Confidence(0.9)),
              target = Some(Confidence(0.5))
            )
          )
        )(signer)

      val endpointRequest =
        endpoint
          .withQueryParam("limit", equalTo("2"))
          .withQueryParam("market", equalTo("SE"))
          //
          .withQueryParam("seed_artists", equalTo("0kbYTNQb4Pb1rPbbaF0pT4"))
          .withQueryParam("seed_genres", equalTo("workout,jazz,drone"))
          .withQueryParam("seed_tracks", equalTo("267lVml7gJ9xefwgO6E2Ag"))
          //
          .withQueryParam("min_acousticness", equalTo("0.2"))
          .withQueryParam("max_acousticness", equalTo("0.9"))
          .withQueryParam("target_acousticness", equalTo("0.5"))
          //
          .withQueryParam("min_danceability", equalTo("0.2"))
          .withQueryParam("max_danceability", equalTo("0.9"))
          .withQueryParam("target_danceability", equalTo("0.5"))
          //
          .withQueryParam("min_duration_ms", equalTo("60000"))
          .withQueryParam("max_duration_ms", equalTo("300000"))
          .withQueryParam("target_duration_ms", equalTo("240000"))
          //
          .withQueryParam("min_energy", equalTo("0.2"))
          .withQueryParam("max_energy", equalTo("0.9"))
          .withQueryParam("target_energy", equalTo("0.5"))
          //
          .withQueryParam("min_instrumentalness", equalTo("0.2"))
          .withQueryParam("max_instrumentalness", equalTo("0.9"))
          .withQueryParam("target_instrumentalness", equalTo("0.5"))
          //
          .withQueryParam("min_key", equalTo("2"))
          .withQueryParam("max_key", equalTo("2"))
          .withQueryParam("target_key", equalTo("2"))
          //
          .withQueryParam("min_liveness", equalTo("0.2"))
          .withQueryParam("max_liveness", equalTo("0.9"))
          .withQueryParam("target_liveness", equalTo("0.5"))
          //
          .withQueryParam("min_loudness", equalTo("-30.0"))
          .withQueryParam("max_loudness", equalTo("30.0"))
          .withQueryParam("target_loudness", equalTo("-10.0"))
          //
          .withQueryParam("min_mode", equalTo("1"))
          .withQueryParam("max_mode", equalTo("1"))
          .withQueryParam("target_mode", equalTo("1"))
          //
          .withQueryParam("min_popularity", equalTo("5"))
          .withQueryParam("max_popularity", equalTo("30"))
          .withQueryParam("target_popularity", equalTo("20"))
          //
          .withQueryParam("min_speechiness", equalTo("0.0"))
          .withQueryParam("max_speechiness", equalTo("0.3"))
          .withQueryParam("target_speechiness", equalTo("0.1"))
          //
          .withQueryParam("min_tempo", equalTo("24.0"))
          .withQueryParam("max_tempo", equalTo("200.0"))
          .withQueryParam("target_tempo", equalTo("120.0"))
          //
          .withQueryParam("min_time_signature", equalTo("2"))
          .withQueryParam("max_time_signature", equalTo("8"))
          .withQueryParam("target_time_signature", equalTo("4"))
          //
          .withQueryParam("min_valence", equalTo("0.2"))
          .withQueryParam("max_valence", equalTo("0.9"))
          .withQueryParam("target_valence", equalTo("0.5"))

      behave like clientReceivingUnexpectedResponse(endpointRequest, request)

      def stub: StubMapping =
        stubFor(
          endpointRequest
            .willReturn(
              aResponse()
                .withStatus(200)
                .withBodyFile("tracks/recommendations_skalpel.json")
            )
        )

      "return the correct entity" in matchResponseBody(stub, request) {
        case Right(recommendations) =>
          recommendations.tracks.head.artists.map(_.name) shouldBe List("Skalpel")
      }
    }
  }
}
