package io.bartholomews.spotify4s

import io.bartholomews.spotify4s.client.ClientData._
import io.bartholomews.scalatestudo.WireWordSpec

class SpotifyClientSpec extends WireWordSpec {
  "SpotifyClient" when {
    "initialised with an implicit configuration" should {
      "read the consumer values from resource folder" in {
        noException shouldBe thrownBy {
          SpotifyClient.unsafeFromConfig()
        }
      }
    }

    "initialised with an explicit configuration" should {
      "read the consumer values from the injected configuration" in {
        val client = sampleClient
        client shouldBe a[SpotifyClient]
      }
    }
  }
}
