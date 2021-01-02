package io.bartholomews.spotify4s

import io.bartholomews.spotify4s.core.SpotifyClient
import org.scalatest.matchers.must.Matchers.{noException, thrownBy}
import org.scalatest.wordspec.AnyWordSpec
import sttp.client.{HttpURLConnectionBackend, Identity, NothingT, SttpBackend}

class SpotifyClientSpec extends AnyWordSpec {
  "SpotifyClient" when {
    implicit val backend: SttpBackend[Identity, Nothing, NothingT] = HttpURLConnectionBackend()

    "initialised with an implicit configuration" should {
      "read the consumer values from resource folder" in {
        noException shouldBe thrownBy {
          SpotifyClient.unsafeFromConfig[Identity]()
        }
      }
    }
  }
}
