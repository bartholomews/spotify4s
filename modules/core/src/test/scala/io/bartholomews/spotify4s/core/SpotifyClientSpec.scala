package io.bartholomews.spotify4s.core

import org.scalatest.matchers.must.Matchers.{noException, thrownBy}
import org.scalatest.wordspec.AnyWordSpec
import sttp.client3.{HttpURLConnectionBackend, Identity, SttpBackend}

class SpotifyClientSpec extends AnyWordSpec {
  "SpotifyClient" when {
    val backend: SttpBackend[Identity, Any] = HttpURLConnectionBackend()

    "initialised with an implicit configuration" should {
      "read the consumer values from resource folder" in {
        noException shouldBe thrownBy {
          SpotifyAuthClient.unsafeFromConfig[Identity](backend)
        }
      }
    }
  }
}
