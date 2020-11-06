package io.bartholomews.spotify4s

import cats.effect.IO
import io.bartholomews.spotify4s.client.ClientData._
import io.bartholomews.scalatestudo.WireWordSpec

class SpotifyClientSpec extends WireWordSpec {
  "SpotifyClient" when {
    "initialised with an implicit configuration" should {
      "read the consumer values from resource folder" in {
        noException shouldBe thrownBy {
          SpotifyClient.unsafeFromConfig[IO]()
        }
      }
    }
  }
}
