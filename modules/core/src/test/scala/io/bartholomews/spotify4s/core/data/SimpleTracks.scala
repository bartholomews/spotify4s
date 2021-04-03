package io.bartholomews.spotify4s.core.data

import io.bartholomews.spotify4s.core.entities.ExternalResourceUrl.SpotifyResourceUrl
import io.bartholomews.spotify4s.core.entities.{SimpleTrack, SpotifyId, SpotifyUri}
import sttp.client3.UriContext

object SimpleTracks {
  val `So What`: SimpleTrack = SimpleTrack(
    artists = List(
      SimpleArtists.`Miles Davis`,
      SimpleArtists.`John Coltrane`,
      SimpleArtists.`Cannonball Adderley`,
      SimpleArtists.`Bill Evans`,
    ),
    availableMarkets = List.empty,
    discNumber = 1,
    durationMs = 562400,
    explicit = false,
    externalUrls = Some(SpotifyResourceUrl(uri"https://open.spotify.com/track/7q3kkfAVpmcZ8g6JUThi3o")),
    href = Some(uri"https://api.spotify.com/v1/tracks/7q3kkfAVpmcZ8g6JUThi3o"),
    id = Some(SpotifyId("7q3kkfAVpmcZ8g6JUThi3o")),
    isPlayable = Some(true),
    linkedFrom = None,
    restrictions = None,
    name = "So What (feat. John Coltrane, Cannonball Adderley & Bill Evans)",
    previewUrl = Some(
      uri"https://p.scdn.co/mp3-preview/31223c6ee13d66a93d17e741f6c759adf22f6c54?cid=07f5f9e97a9a4fddba3d205d19fcd21e"
    ),
    trackNumber = 1,
    uri = SpotifyUri("spotify:track:7q3kkfAVpmcZ8g6JUThi3o"),
    isLocal = false
  )

  val `Freddie Freeloader`: SimpleTrack = SimpleTrack(
    artists = List(
      SimpleArtists.`Miles Davis`,
      SimpleArtists.`John Coltrane`,
      SimpleArtists.`Cannonball Adderley`,
      SimpleArtists.`Wynton Kelly`,
      SimpleArtists.`Paul Chambers`,
    ),
    availableMarkets = List.empty,
    discNumber = 1,
    durationMs = 586493,
    explicit = false,
    externalUrls = Some(SpotifyResourceUrl(uri"https://open.spotify.com/track/3NvYPUNu6nwQgN31UnoDbn")),
    href = Some(uri"https://api.spotify.com/v1/tracks/3NvYPUNu6nwQgN31UnoDbn"),
    id = Some(SpotifyId("3NvYPUNu6nwQgN31UnoDbn")),
    isPlayable = Some(true),
    linkedFrom = None,
    restrictions = None,
    name = "Freddie Freeloader (feat. John Coltrane, Cannonball Adderley, Wynton Kelly & Paul Chambers)",
    previewUrl = Some(
      uri"https://p.scdn.co/mp3-preview/744d4852b9c7a68aa661829770e86db0d55863ec?cid=07f5f9e97a9a4fddba3d205d19fcd21e"
    ),
    trackNumber = 2,
    uri = SpotifyUri("spotify:track:3NvYPUNu6nwQgN31UnoDbn"),
    isLocal = false
  )

  val `Blue in Green`: SimpleTrack = SimpleTrack(
    artists = List(
      SimpleArtists.`Miles Davis`,
      SimpleArtists.`John Coltrane`,
      SimpleArtists.`Bill Evans`,
    ),
    availableMarkets = List.empty,
    discNumber = 1,
    durationMs = 337160,
    explicit = false,
    externalUrls = Some(SpotifyResourceUrl(uri"https://open.spotify.com/track/6bP4GyrKNbcKPMDqWJqpxI")),
    href = Some(uri"https://api.spotify.com/v1/tracks/6bP4GyrKNbcKPMDqWJqpxI"),
    id = Some(SpotifyId("6bP4GyrKNbcKPMDqWJqpxI")),
    isPlayable = Some(true),
    linkedFrom = None,
    restrictions = None,
    name = "Blue in Green (feat. John Coltrane & Bill Evans)",
    previewUrl = Some(
      uri"https://p.scdn.co/mp3-preview/021636a9d64620193add469ca93b4c3ef2d5e7f8?cid=07f5f9e97a9a4fddba3d205d19fcd21e"
    ),
    trackNumber = 3,
    uri = SpotifyUri("spotify:track:6bP4GyrKNbcKPMDqWJqpxI"),
    isLocal = false
  )

  val `All Blues`: SimpleTrack = SimpleTrack(
    artists = List(
      SimpleArtists.`Miles Davis`,
      SimpleArtists.`John Coltrane`,
      SimpleArtists.`Cannonball Adderley`,
      SimpleArtists.`Bill Evans`,
    ),
    availableMarkets = List.empty,
    discNumber = 1,
    durationMs = 692973,
    explicit = false,
    externalUrls = Some(SpotifyResourceUrl(uri"https://open.spotify.com/track/0TUa7gBg7LJH6B8dkNVioU")),
    href = Some(uri"https://api.spotify.com/v1/tracks/0TUa7gBg7LJH6B8dkNVioU"),
    id = Some(SpotifyId("0TUa7gBg7LJH6B8dkNVioU")),
    isPlayable = Some(true),
    linkedFrom = None,
    restrictions = None,
    name = "All Blues (feat. John Coltrane, Cannonball Adderley & Bill Evans)",
    previewUrl = Some(
      uri"https://p.scdn.co/mp3-preview/9654ed54a8011d3a19e39023d8210bc1d9678174?cid=07f5f9e97a9a4fddba3d205d19fcd21e"
    ),
    trackNumber = 4,
    uri = SpotifyUri("spotify:track:0TUa7gBg7LJH6B8dkNVioU"),
    isLocal = false
  )

  val `Flamenco Sketches`: SimpleTrack = SimpleTrack(
    artists = List(
      SimpleArtists.`Miles Davis`,
      SimpleArtists.`John Coltrane`,
      SimpleArtists.`Cannonball Adderley`,
      SimpleArtists.`Bill Evans`,
    ),
    availableMarkets = List.empty,
    discNumber = 1,
    durationMs = 566066,
    explicit = false,
    externalUrls = Some(SpotifyResourceUrl(uri"https://open.spotify.com/track/3R3fcrKaeBr3U6s9R2belr")),
    href = Some(uri"https://api.spotify.com/v1/tracks/3R3fcrKaeBr3U6s9R2belr"),
    id = Some(SpotifyId("3R3fcrKaeBr3U6s9R2belr")),
    isPlayable = Some(true),
    linkedFrom = None,
    restrictions = None,
    name = "Flamenco Sketches (feat. John Coltrane, Cannonball Adderley & Bill Evans)",
    previewUrl = Some(
      uri"https://p.scdn.co/mp3-preview/22fdba943713b875689f1c3e5ecfd3737103cc9b?cid=07f5f9e97a9a4fddba3d205d19fcd21e"
    ),
    trackNumber = 5,
    uri = SpotifyUri("spotify:track:3R3fcrKaeBr3U6s9R2belr"),
    isLocal = false
  )

  val `Shhh / Peaceful`: SimpleTrack = SimpleTrack(
    artists = List(SimpleArtists.`Miles Davis`),
    availableMarkets = List.empty,
    discNumber = 1,
    durationMs = 1094733,
    explicit = false,
    externalUrls = Some(SpotifyResourceUrl(uri"https://open.spotify.com/track/267lVml7gJ9xefwgO6E2Ag")),
    href = Some(uri"https://api.spotify.com/v1/tracks/267lVml7gJ9xefwgO6E2Ag"),
    id = Some(SpotifyId("267lVml7gJ9xefwgO6E2Ag")),
    isPlayable = Some(true),
    linkedFrom = None,
    restrictions = None,
    name = "Shhh / Peaceful",
    previewUrl = Some(
      uri"https://p.scdn.co/mp3-preview/4de96132fc9f4be9fd6dc11b3ce7f4c279e1bf2f?cid=07f5f9e97a9a4fddba3d205d19fcd21e"
    ),
    trackNumber = 1,
    uri = SpotifyUri("spotify:track:267lVml7gJ9xefwgO6E2Ag"),
    isLocal = false
  )

  val `In a Silent Way`: SimpleTrack = SimpleTrack(
    artists = List(SimpleArtists.`Miles Davis`),
    availableMarkets = List.empty,
    discNumber = 1,
    durationMs = 1192333,
    explicit = false,
    externalUrls = Some(SpotifyResourceUrl(uri"https://open.spotify.com/track/54a3lHJAwce1dOKrsDPEHv")),
    href = Some(uri"https://api.spotify.com/v1/tracks/54a3lHJAwce1dOKrsDPEHv"),
    id = Some(SpotifyId("54a3lHJAwce1dOKrsDPEHv")),
    isPlayable = Some(true),
    linkedFrom = None,
    restrictions = None,
    name = "In a Silent Way",
    previewUrl = Some(
      uri"https://p.scdn.co/mp3-preview/c8a0f0bfa72cbcc9d6cb2ea06c93a5409282e37c?cid=07f5f9e97a9a4fddba3d205d19fcd21e"
    ),
    trackNumber = 2,
    uri = SpotifyUri("spotify:track:54a3lHJAwce1dOKrsDPEHv"),
    isLocal = false
  )
}
