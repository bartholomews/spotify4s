import com.vitorsvieira.iso.ISOCountry
import it.turingtest.spotify.scala.client.{AuthApi, BaseApi}
import it.turingtest.spotify.scala.client.entities.Track
import org.scalatest.{FunSpec, Matchers}
import org.scalatestplus.play.guice.GuiceOneServerPerTest
import play.api.test.WsTestClient

/**
  * @see https://www.playframework.com/documentation/2.5.x/ScalaTestingWebServiceClients
  */
class PlaylistApiSpec extends FunSpec with Matchers with GuiceOneServerPerTest with SpotifyWebMock {

  describe("Playlist Api") {

    describe("GET /v1/users/{user_id}/playlists/{playlist_id}") {

      it("should get a playlist") {
        withPlaylistsApi { api =>
          val result = await { api.playlist("spotify", "59ZbFPES4DQwEjBpWHzrtC") }
          result.description shouldBe Some("Having friends over for dinner? Here's the perfect playlist.")
          result.tracks.href shouldBe "https://api.spotify.com/v1/users/spotify/playlists/59ZbFPES4DQwEjBpWHzrtC/tracks?offset=0&limit=100"
        }
      }

      it("should get a playlist for a specific market") {
        withPlaylistsApi { api =>
          val result = await { api.playlist("spotify", "59ZbFPES4DQwEjBpWHzrtC", market = Some(ISOCountry.SPAIN)) }
          result.description shouldBe Some("Having friends over for dinner? Here's the perfect playlist.")
          result.tracks.href shouldBe "https://api.spotify.com/v1/users/spotify/playlists/59ZbFPES4DQwEjBpWHzrtC/tracks?offset=0&limit=100&market=ES"
        }
      }
    }

    describe("GET /v1/{user_id}/playlists") {

      it("should get list of user's playlists") {
        withPlaylistsApi { api =>
          val result = await { api.playlists("wizzler") }
          result.items.head.name shouldBe "Video Game Masterpieces"
        }
      }
    }

    describe("GET /v1/me/playlists") {}

    describe("PUT /v1/users/{owner_id}/playlists/{playlist_id}/followers") {}

    describe("DELETE /v1/users/{owner_id}/playlists/{playlist_id}/followers") {}

    describe("GET /v1/search?type=playlist") {}

    describe("POST /v1/users/{user_id}/playlists") {}

    describe("PUT /v1/users/{user_id}/playlists/{playlist_id}") {}

    describe("GET /v1/users/{user_id}/playlists/{playlist_id}/followers/contains") {}

    describe("PUT /v1/users/{user_id}/playlists/{playlist_id}/images") {}

    describe("GET /v1/users/{user_id}/playlists/{playlist_id}/tracks") {}

    describe("POST /v1/users/{user_id}/playlists/{playlist_id}/tracks") {}

    describe("DELETE /v1/users/{user_id}/playlists/{playlist_id}/tracks") {}

    describe("PUT /v1/users/{user_id}/playlists/{playlist_id}/tracks") {}

    describe("PUT /v1/users/{user_id}/playlists/{playlist_id}/tracks") {}

  }

}
