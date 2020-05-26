package io.bartholomews.spotify4s.api

import com.github.tomakehurst.wiremock.client.MappingBuilder
import com.github.tomakehurst.wiremock.client.WireMock.{aResponse, stubFor}
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import io.bartholomews.fsclient.entities.{ErrorBodyJson, ErrorBodyString, FsResponse}
import io.bartholomews.fsclient.utils.HttpTypes.IOResponse
import io.bartholomews.spotify4s.entities.{ApiError, AuthError}
import io.bartholomews.scalatestudo.WireWordSpec
import org.apache.http.entity.ContentType
import org.http4s.Status

trait ServerBehaviours {

  self: WireWordSpec =>

  val basePath = "/api/v1"

  def clientReceivingUnexpectedResponse[A](expectedEndpoint: MappingBuilder, request: IOResponse[A]): Unit = {
    behave like clientReceivingAuthErrorResponse(expectedEndpoint, request)
    behave like clientReceivingApiErrorResponse(expectedEndpoint, request)
    behave like clientReceivingSuccessfulUnexpectedResponseBody(expectedEndpoint, request)
  }

  private def clientReceivingAuthErrorResponse[A](expectedEndpoint: MappingBuilder, request: IOResponse[A]): Unit = {
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

      "returns a Left with appropriate message" in matchResponse(stub, request) {
        case FsResponse(_, status, Left(ErrorBodyJson(error))) =>
          status shouldBe Status.Unauthorized
          inside(error.as[AuthError]) {
            case Right(AuthError(error, message)) =>
              error shouldBe "invalid_grant"
              message shouldBe "Invalid authorization code"
          }
      }
    }
  }

  private def clientReceivingApiErrorResponse[A](expectedEndpoint: MappingBuilder, request: IOResponse[A]): Unit = {
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

      "returns a Left with appropriate message" in matchResponse(stub, request) {
        case FsResponse(_, status, Left(ErrorBodyJson(error))) =>
          status shouldBe Status.BadRequest
          inside(error.as[ApiError]) {
            case Right(ApiError(status, message)) =>
              status shouldBe 400
              message shouldBe "invalid id"
          }
      }
    }
  }

  private def clientReceivingSuccessfulUnexpectedResponseBody[A](
    expectedEndpoint: MappingBuilder,
    request: IOResponse[A]
  ): Unit = {
    "the server response is unexpected" should {
      def stub: StubMapping =
        stubFor(
          expectedEndpoint
            .willReturn(
              aResponse()
                .withStatus(200)
                .withBody("""
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
                    |""".stripMargin)
            )
        )

      "return a Left with appropriate message" in matchResponse(stub, request) {
        case FsResponse(_, status, Left(ErrorBodyString(error))) =>
          status shouldBe Status.UnprocessableEntity
          error shouldBe "There was a problem decoding or parsing this response, please check the error logs"
      }
    }
  }
}
