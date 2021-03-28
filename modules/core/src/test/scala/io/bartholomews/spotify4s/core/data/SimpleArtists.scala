package io.bartholomews.spotify4s.core.data

import io.bartholomews.spotify4s.core.entities.{ExternalResourceUrl, SimpleArtist, SpotifyId, SpotifyUri}
import sttp.client3.UriContext

object SimpleArtists {

  private def makeSimpleArtist(name: String, id: String): SimpleArtist =
    SimpleArtist(
      externalUrls = Some(ExternalResourceUrl.SpotifyResourceUrl(uri"https://open.spotify.com/artist/$id")),
      href = Some(uri"https://api.spotify.com/v1/artists/$id"),
      id = Some(SpotifyId(id)),
      name,
      uri = Some(SpotifyUri(s"spotify:artist:$id"))
    )

  val `Miles Davis`: SimpleArtist = makeSimpleArtist(name = "Miles Davis", id = "0kbYTNQb4Pb1rPbbaF0pT4")
  val `John Coltrane`: SimpleArtist = makeSimpleArtist(name = "John Coltrane", id = "2hGh5VOeeqimQFxqXvfCUf")
  val `Cannonball Adderley`: SimpleArtist =
    makeSimpleArtist(name = "Cannonball Adderley", id = "5v74mT11KGJqadf9sLw4dA")
  val `Bill Evans`: SimpleArtist = makeSimpleArtist(name = "Bill Evans", id = "4jXfFzeP66Zy67HM2mvIIF")
  val `Wynton Kelly`: SimpleArtist = makeSimpleArtist(name = "Wynton Kelly", id = "5ncBRFyyylFng7kQJaRXN0")
  val `Paul Chambers`: SimpleArtist = makeSimpleArtist(name = "Paul Chambers", id = "0M1UOBJZ9tcKJbrbnVlHZG")
}
