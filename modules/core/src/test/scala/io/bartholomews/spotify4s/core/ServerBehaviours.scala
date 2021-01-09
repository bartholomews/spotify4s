package io.bartholomews.spotify4s.core

import com.github.tomakehurst.wiremock.client.MappingBuilder
import com.github.tomakehurst.wiremock.client.WireMock.{aResponse, stubFor}
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import io.bartholomews.fsclient.core.http.SttpResponses.SttpResponse
import io.bartholomews.fsclient.core.oauth.v2.OAuthV2.ResponseHandler
import io.bartholomews.scalatestudo.WireWordSpec
import io.bartholomews.spotify4s.core.entities.{ApiError, AuthError, Page, SpotifyError}
import org.apache.http.entity.ContentType
import org.scalatest.matchers.should.Matchers
import sttp.client3.{DeserializationException, HttpError}
import sttp.model.StatusCode

import scala.reflect.ClassTag

trait ServerBehaviours[Decoder[_], DE] extends Matchers {

  self: WireWordSpec =>

  implicit def ct: ClassTag[DE]
  implicit def responseHandler[T](implicit decoder: Decoder[T]): ResponseHandler[DE, T]
  implicit def pageDecoder[T](implicit decoder: Decoder[T]): Decoder[Page[T]]

  val basePath = "/api/v1"

  def parseSpotifyError: String => Either[DE, SpotifyError]

  override final val testResourcesFileRoot: String = "modules/core/src/test/resources"

  def clientReceivingUnexpectedResponse[A](
    expectedEndpoint: MappingBuilder,
    request: => SttpResponse[DE, A],
    decodingBody: Boolean = true
  ): Unit = {
    behave like clientReceivingAuthErrorResponse(expectedEndpoint, request)
    behave like clientReceivingApiErrorResponse(expectedEndpoint, request)
    if (decodingBody)
      behave like clientReceivingSuccessfulUnexpectedResponseBody(expectedEndpoint, request)
  }

  private def clientReceivingAuthErrorResponse[A](
    expectedEndpoint: MappingBuilder,
    request: => SttpResponse[DE, A]
  ): Unit = {
    "the server responds with an `invalid_grant` error" when {
      def stub: StubMapping =
        stubFor(
          expectedEndpoint
            .willReturn(
              aResponse()
                .withStatus(401)
                .withContentType(ContentType.APPLICATION_JSON)
                .withBodyFile("error/authorization_invalid_code.json")
            )
        )

      "returns a Left with appropriate message" in matchResponseBody(stub, request) {
        case Left(HttpError(body, status)) =>
          status shouldBe StatusCode.Unauthorized
          parseSpotifyError(body) shouldBe Right(
            AuthError(
              error = "invalid_grant",
              message = "Invalid authorization code"
            )
          )
      }
    }
  }

  private def clientReceivingApiErrorResponse[A](
    expectedEndpoint: MappingBuilder,
    request: => SttpResponse[DE, A]
  ): Unit = {
    "the server responds with an `invalid_id` error" when {
      def stub: StubMapping =
        stubFor(
          expectedEndpoint
            .willReturn(
              aResponse()
                .withStatus(400)
                .withContentType(ContentType.APPLICATION_JSON)
                .withBodyFile("error/invalid_id.json")
            )
        )

      "returns a Left with appropriate message" in matchResponseBody(stub, request) {
        case Left(HttpError(body, status)) =>
          status shouldBe StatusCode.BadRequest
          parseSpotifyError(body) shouldBe Right(
            ApiError(
              status = 400,
              message = "invalid id"
            )
          )
      }
    }
  }

  private def clientReceivingSuccessfulUnexpectedResponseBody[A](
    expectedEndpoint: MappingBuilder,
    request: => SttpResponse[DE, A]
  ): Unit = {
    val ezekiel = """
                    |Ezekiel 25:17.
                    |"The path of the righteous man is beset on all sides
                    |by the inequities of the selfish and the tyranny of evil men.
                    |Blessed is he who, in the name of charity and good will,
                    |shepherds the weak through the valley of the darkness.
                    |For he is truly his brother's keeper and the finder of lost children.
                    |And I will strike down upon thee with great vengeance and furious anger
                    |those who attempt to poison and destroy my brothers.
                    |And you will know I am the Lord
                    |when I lay my vengeance upon you."
                    |""".stripMargin

    "the server response is unexpected" should {
      def stub: StubMapping =
        stubFor(
          expectedEndpoint
            .willReturn(
              aResponse()
                .withStatus(200)
                .withBody(ezekiel)
            )
        )

      "return a Left with appropriate message" in matchResponseBody(stub, request) {
        case Left(DeserializationException(body, error)) =>
          body shouldBe ezekiel
//          error shouldBe a[DE]
      }
    }
  }
}
