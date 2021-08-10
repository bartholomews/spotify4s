package io.bartholomews.spotify4s.core.data

import io.bartholomews.spotify4s.core.entities.ExternalResourceUrl.SpotifyResourceUrl
import io.bartholomews.spotify4s.core.entities.SpotifyId.SpotifyAlbumId
import io.bartholomews.spotify4s.core.entities._
import sttp.client3.UriContext

import java.time.Month

object FullAlbums {
  val `Kind of Blue`: FullAlbum = FullAlbum(
    albumType = AlbumType.Album,
    artists = List(SimpleArtists.`Miles Davis`),
    availableMarkets = List.empty,
    copyrights = List(Copyright(text = "Originally released 1959 Sony Music Entertainment Inc.", `type` = "P")),
    externalIds = ExternalIds.UPC("5099749522428"),
    externalUrls = SpotifyResourceUrl(uri"https://open.spotify.com/album/1weenld61qoidwYuZ1GESA"),
    genres = List.empty,
    href = uri"https://api.spotify.com/v1/albums/1weenld61qoidwYuZ1GESA",
    id = SpotifyAlbumId("1weenld61qoidwYuZ1GESA"),
    images = List(
      SpotifyImage(
        height = Some(640),
        url = uri"https://i.scdn.co/image/ab67616d0000b2737ab89c25093ea3787b1995b4",
        width = Some(640)
      ),
      SpotifyImage(
        height = Some(300),
        url = uri"https://i.scdn.co/image/ab67616d00001e027ab89c25093ea3787b1995b4",
        width = Some(300)
      ),
      SpotifyImage(
        height = Some(64),
        url = uri"https://i.scdn.co/image/ab67616d000048517ab89c25093ea3787b1995b4",
        width = Some(64)
      )
    ),
    label = "Columbia",
    name = "Kind Of Blue",
    popularity = 56,
    releaseDate = ReleaseDate(
      year = 1959,
      month = Some(Month.AUGUST),
      dayOfMonth = Some(17)
    ),
    restrictions = None,
    tracks = Page(
      href = uri"https://api.spotify.com/v1/albums/1weenld61qoidwYuZ1GESA/tracks?offset=0&limit=50&market=GB",
      items = List(
        SimpleTracks.`So What`,
        SimpleTracks.`Freddie Freeloader`,
        SimpleTracks.`Blue in Green`,
        SimpleTracks.`All Blues`,
        SimpleTracks.`Flamenco Sketches`
      ),
      limit = Some(50),
      next = None,
      offset = Some(0),
      previous = None,
      total = 5
    ),
    uri = SpotifyUri("spotify:album:1weenld61qoidwYuZ1GESA")
  )

  val `In A Silent Way`: FullAlbum = FullAlbum(
    albumType = AlbumType.Album,
    artists = List(SimpleArtists.`Miles Davis`),
    availableMarkets = List.empty,
    copyrights = List(
      Copyright(
        text =
          "Originally released 1969. All rights reserved by Columbia Records, a division of Sony Music Entertainment Inc.",
        `type` = "P"
      )
    ),
    externalIds = ExternalIds.UPC("696998655621"),
    externalUrls = SpotifyResourceUrl(uri"https://open.spotify.com/album/0Hs3BomCdwIWRhgT57x22T"),
    genres = List.empty,
    href = uri"https://api.spotify.com/v1/albums/0Hs3BomCdwIWRhgT57x22T",
    id = SpotifyAlbumId("0Hs3BomCdwIWRhgT57x22T"),
    images = List(
      SpotifyImage(
        height = Some(640),
        url = uri"https://i.scdn.co/image/ab67616d0000b2737a4a3c0b5d38ec756c62d214",
        width = Some(640)
      ),
      SpotifyImage(
        height = Some(300),
        url = uri"https://i.scdn.co/image/ab67616d00001e027a4a3c0b5d38ec756c62d214",
        width = Some(300)
      ),
      SpotifyImage(
        height = Some(64),
        url = uri"https://i.scdn.co/image/ab67616d000048517a4a3c0b5d38ec756c62d214",
        width = Some(64)
      )
    ),
    label = "Columbia/Legacy",
    name = "In A Silent Way",
    popularity = 41,
    releaseDate = ReleaseDate(
      year = 1969,
      month = Some(Month.JULY),
      dayOfMonth = Some(30)
    ),
    restrictions = None,
    tracks = Page(
      href = uri"https://api.spotify.com/v1/albums/0Hs3BomCdwIWRhgT57x22T/tracks?offset=0&limit=50&market=GB",
      items = List(
        SimpleTracks.`Shhh / Peaceful`,
        SimpleTracks.`In a Silent Way`
      ),
      limit = Some(50),
      next = None,
      offset = Some(0),
      previous = None,
      total = 2
    ),
    uri = SpotifyUri("spotify:album:0Hs3BomCdwIWRhgT57x22T")
  )
}
