package io.bartholomews.spotify4s.circe.api

import com.github.tomakehurst.wiremock.client.MappingBuilder
import com.github.tomakehurst.wiremock.client.WireMock.{aResponse, stubFor}
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import io.bartholomews.fsclient.core.http.SttpResponses.SttpResponse
import io.bartholomews.scalatestudo.matchers.StubbedIO
import io.bartholomews.scalatestudo.wiremock.WiremockUtils.ResponseDefinitionImplicits
import io.bartholomews.spotify4s.core.entities.{ApiError, AuthError, SpotifyError}
import io.circe.ParsingFailure
import org.apache.http.entity.ContentType
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import sttp.client.{DeserializationError, HttpError}
import sttp.model.StatusCode

trait ServerBehaviours extends Matchers {

  self: AnyWordSpec with StubbedIO =>

  val basePath = "/api/v1"

  import io.bartholomews.spotify4s.circe._
  import io.circe.parser._

  def clientReceivingUnexpectedResponse[E, A](
    expectedEndpoint: MappingBuilder,
    request: => SttpResponse[E, A],
    decodingBody: Boolean = true
  ): Unit = {
    behave like clientReceivingAuthErrorResponse(expectedEndpoint, request)
    behave like clientReceivingApiErrorResponse(expectedEndpoint, request)
    if (decodingBody)
      behave like clientReceivingSuccessfulUnexpectedResponseBody(expectedEndpoint, request)
  }

  private def clientReceivingAuthErrorResponse[E, A](
    expectedEndpoint: MappingBuilder,
    request: => SttpResponse[E, A]
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
          parse(body).flatMap(_.as[SpotifyError]) shouldBe Right(
            AuthError(
              error = "invalid_grant",
              message = "Invalid authorization code"
            )
          )
      }
    }
  }

  private def clientReceivingApiErrorResponse[E, A](
    expectedEndpoint: MappingBuilder,
    request: => SttpResponse[E, A]
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
          parse(body).flatMap(_.as[SpotifyError]) shouldBe Right(
            ApiError(
              status = 400,
              message = "invalid id"
            )
          )
      }
    }
  }

  private def clientReceivingSuccessfulUnexpectedResponseBody[E, A](
    expectedEndpoint: MappingBuilder,
    request: => SttpResponse[E, A]
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
        case Left(DeserializationError(body, _ @ParsingFailure(message, _))) =>
          body shouldBe ezekiel
          message shouldBe "expected json value got 'Ezekie...' (line 2, column 1)"
      }
    }
  }
}
