package io.bartholomews.spotify4s.core.api

import com.github.tomakehurst.wiremock.client.MappingBuilder
import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import eu.timepit.refined.api.Refined
import io.bartholomews.fsclient.core.http.SttpResponses.SttpResponse
import io.bartholomews.fsclient.core.oauth.NonRefreshableTokenSigner
import io.bartholomews.iso.{CountryCodeAlpha2, LanguageCode}
import io.bartholomews.scalatestudo.WireWordSpec
import io.bartholomews.scalatestudo.data.ClientData.v2.sampleNonRefreshableToken
import io.bartholomews.spotify4s.core.SpotifyServerBehaviours
import io.bartholomews.spotify4s.core.entities.SpotifyId.{SpotifyArtistId, SpotifyPlaylistName, SpotifyTrackId}
import io.bartholomews.spotify4s.core.entities._
import io.bartholomews.spotify4s.core.utils.SpotifyClientData.sampleClient
import sttp.client3.UriContext

import java.time.{LocalDateTime, Month}
import scala.concurrent.duration.DurationInt

abstract class BrowseApiSpec[E[_], D[_], DE, J] extends WireWordSpec with SpotifyServerBehaviours[E, D, DE, J] {
  import eu.timepit.refined.auto.autoRefineV

  implicit val signer: NonRefreshableTokenSigner = sampleNonRefreshableToken
  implicit def newReleasesCodec: D[NewReleases]
  implicit def featuredPlaylistsCodec: D[FeaturedPlaylists]
  implicit def categoriesResponseCodec: D[CategoriesResponse]
  implicit def categoryCodec: D[Category]
  implicit def playlistsResponseCodec: D[PlaylistsResponse]
  implicit def recommendationsCodec: D[Recommendations]
  implicit def spotifyGenresResponseCodec: D[SpotifyGenresResponse]

  "getAllNewReleases" when {
    def endpoint: MappingBuilder = get(urlPathEqualTo(s"$basePath/browse/new-releases"))

    "all query parameters are defined" should {
      def request: SttpResponse[DE, NewReleases] =
        sampleClient.browse
          .getAllNewReleases[DE](country = Some(CountryCodeAlpha2.SWEDEN), limit = 2, offset = 5)(signer)

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

  "getAllFeaturedPlaylists" when {
    def endpoint: MappingBuilder = get(urlPathEqualTo(s"$basePath/browse/featured-playlists"))

    "all query parameters are defined" should {
      def request: SttpResponse[DE, FeaturedPlaylists] =
        sampleClient.browse.getAllFeaturedPlaylists[DE](
          country = Some(CountryCodeAlpha2.SWEDEN),
          locale = Some(Locale(LanguageCode.SPANISH, CountryCodeAlpha2.MEXICO)),
          timestamp = Some(LocalDateTime.of(2014, Month.OCTOBER, 23, 9, 0)),
          limit = 2,
          offset = 5
        )(signer)

      val endpointRequest =
        endpoint
          .withQueryParam("country", equalTo("SE"))
          .withQueryParam("locale", equalTo("es_MX"))
          .withQueryParam("timestamp", equalTo("2014-10-23T09:00:00"))
          .withQueryParam("limit", equalTo("2"))
          .withQueryParam("offset", equalTo("5"))

      behave like clientReceivingUnexpectedResponse(endpointRequest, request)

      def stub: StubMapping =
        stubFor(
          endpointRequest
            .willReturn(
              aResponse()
                .withStatus(200)
                .withBodyFile("browse/featured_playlists_se.json")
            )
        )

      "return the correct entity" in matchResponseBody(stub, request) {
        case Right(FeaturedPlaylists(message, playlists)) =>
          message shouldBe "Ny dag, nya tag"
          playlists.items.size shouldBe 2
      }
    }
  }

  "getAllCategories" when {
    def endpoint: MappingBuilder = get(urlPathEqualTo(s"$basePath/browse/categories"))

    "all query parameters are defined" should {
      def request: SttpResponse[DE, Page[Category]] =
        sampleClient.browse.getAllCategories[DE](
          country = Some(CountryCodeAlpha2.SWEDEN),
          locale = Some(Locale(LanguageCode.SPANISH, CountryCodeAlpha2.MEXICO)),
          limit = 2,
          offset = 5
        )(signer)

      val endpointRequest =
        endpoint
          .withQueryParam("country", equalTo("SE"))
          .withQueryParam("locale", equalTo("es_MX"))
          .withQueryParam("limit", equalTo("2"))
          .withQueryParam("offset", equalTo("5"))

      behave like clientReceivingUnexpectedResponse(endpointRequest, request)

      def stub: StubMapping =
        stubFor(
          endpointRequest
            .willReturn(
              aResponse()
                .withStatus(200)
                .withBodyFile("browse/categories_se.json")
            )
        )

      "return the correct entity" in matchResponseBody(stub, request) {
        case Right(categories) =>
          categories.items.map(_.name) shouldBe List(CategoryName("Decades"), CategoryName("Hip Hop"))
      }
    }
  }

  "getCategory" when {
    def endpoint: MappingBuilder = get(urlPathEqualTo(s"$basePath/browse/categories/workout"))

    "all query parameters are defined" should {
      def request: SttpResponse[DE, Category] =
        sampleClient.browse.getCategory[DE](
          categoryId = SpotifyCategoryId("workout"),
          country = Some(CountryCodeAlpha2.SWEDEN),
          locale = Some(Locale(LanguageCode.SPANISH, CountryCodeAlpha2.MEXICO))
        )(signer)

      val endpointRequest =
        endpoint
          .withQueryParam("country", equalTo("SE"))
          .withQueryParam("locale", equalTo("es_MX"))

      behave like clientReceivingUnexpectedResponse(endpointRequest, request)

      def stub: StubMapping =
        stubFor(
          endpointRequest
            .willReturn(
              aResponse()
                .withStatus(200)
                .withBodyFile("browse/category_workout_se.json")
            )
        )

      "return the correct entity" in matchResponseBody(stub, request) {
        case Right(category) =>
          category.name shouldBe CategoryName("Entrenamiento")
      }
    }
  }

  "getCategoryPlaylists" when {
    def endpoint: MappingBuilder = get(urlPathEqualTo(s"$basePath/browse/categories/workout/playlists"))

    "all query parameters are defined" should {
      def request: SttpResponse[DE, Page[SimplePlaylist]] =
        sampleClient.browse.getCategoryPlaylists[DE](
          categoryId = SpotifyCategoryId("workout"),
          country = Some(CountryCodeAlpha2.SWEDEN),
          limit = 2,
          offset = 5
        )(signer)

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
                .withBodyFile("browse/category_playlists_workout_se.json")
            )
        )

      "return the correct entity" in matchResponseBody(stub, request) {
        case Right(categories) =>
          categories.items.map(_.name) shouldBe List(
            SpotifyPlaylistName("Motivation Mix"),
            SpotifyPlaylistName("Yoga & Meditation")
          )
      }
    }
  }

  "getRecommendations" when {
    def endpoint: MappingBuilder = get(urlPathEqualTo(s"$basePath/recommendations"))

    "all query parameters are defined" should {
      def request: SttpResponse[DE, Recommendations] =
        sampleClient.browse.getRecommendations[DE](
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
                .withBodyFile("browse/recommendations_skalpel.json")
            )
        )

      "return the correct entity" in matchResponseBody(stub, request) {
        case Right(recommendations) =>
          recommendations.tracks.head.artists.map(_.name) shouldBe List("Skalpel")
      }
    }
  }

  "getRecommendationGenres" should {
    def endpointRequest: MappingBuilder = get(urlPathEqualTo(s"$basePath/recommendations/available-genre-seeds"))

    def request: SttpResponse[DE, List[SpotifyGenre]] =
      sampleClient.browse.getRecommendationGenres[DE](signer)

    behave like clientReceivingUnexpectedResponse(endpointRequest, request)

    def stub: StubMapping =
      stubFor(
        endpointRequest
          .willReturn(
            aResponse()
              .withStatus(200)
              .withBodyFile("browse/recommendation_genres.json")
          )
      )

    "return the correct entity" in matchResponseBody(stub, request) {
      case Right(genres) =>
        genres should contain(SpotifyGenre("death-metal"))
    }
  }
}
