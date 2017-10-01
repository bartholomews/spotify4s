import org.scalatest.{FunSpec, Matchers}
import org.scalatestplus.play.guice.GuiceOneServerPerTest

class ProfilesApiSpec extends FunSpec with Matchers with GuiceOneServerPerTest with SpotifyWebMock {

  describe("GET /v1/me/playlists") {

    it("should ge me") {
      withProfilesApi(profiles => {
        val result = await { profiles.me }
        result.id shouldBe "{f_}"
      })
    }

    it("should get a user's profile") {
      withProfilesApi(profiles => {
        val result = await { profiles.user("foo") }
        result.id shouldBe "foo"
      })
    }

    it("should get my playlists") {
      withProfilesApi(profiles => {
        val result = await { profiles.myPlaylists }
        result.total shouldBe 13
        result.limit shouldBe 20
        result.offset shouldBe 0
        result.previous shouldBe None
        result.items.head.name shouldBe "John Frusciante"
      })
    }

    it("should get my playlists with limit and offset") {
      withProfilesApi(profiles => {
        val result = await { profiles.myPlaylists(
          limit = Some(4), offset = Some(3))
        }
        result.total shouldBe 13
        result.limit shouldBe 4
        result.offset shouldBe 3
        result.previous shouldBe Some(
          "https://api.spotify.com/v1/users/%7Bf_%7D/playlists?offset=0&limit=4")
        result.items.head.name shouldBe "Aphex Twin – Melodies from Mars"
      })
    }

  }

}
