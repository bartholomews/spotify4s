package io.bartholomews.spotify4s.core.api

import com.github.tomakehurst.wiremock.client.MappingBuilder
import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import io.bartholomews.fsclient.core.http.SttpResponses.SttpResponse
import io.bartholomews.fsclient.core.oauth.NonRefreshableTokenSigner
import io.bartholomews.iso.{CountryCodeAlpha2, LanguageCode}
import io.bartholomews.scalatestudo.WireWordSpec
import io.bartholomews.scalatestudo.data.ClientData.v2.sampleNonRefreshableToken
import io.bartholomews.spotify4s.core.SpotifyServerBehaviours
import io.bartholomews.spotify4s.core.entities._
import io.bartholomews.spotify4s.core.utils.SpotifyClientData.sampleClient

abstract class CategoriesApiSpec[E[_], D[_], DE, J] extends WireWordSpec with SpotifyServerBehaviours[E, D, DE, J] {
  import eu.timepit.refined.auto.autoRefineV

  implicit val signer: NonRefreshableTokenSigner = sampleNonRefreshableToken
  implicit def categoriesResponseCodec: D[CategoriesResponse]
  implicit def categoryCodec: D[Category]

  "getBrowseCategories" when {
    def endpoint: MappingBuilder = get(urlPathEqualTo(s"$basePath/browse/categories"))

    "all query parameters are defined" should {
      def request: SttpResponse[DE, Page[Category]] =
        sampleClient.categories.getBrowseCategories[DE](
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

  "getBrowseCategory" when {
    def endpoint: MappingBuilder = get(urlPathEqualTo(s"$basePath/browse/categories/workout"))

    "all query parameters are defined" should {
      def request: SttpResponse[DE, Category] =
        sampleClient.categories.getBrowseCategory[DE](
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
}
