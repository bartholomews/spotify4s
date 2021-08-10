package io.bartholomews.spotify4s.core.entities

import io.bartholomews.spotify4s.core.entities.SpotifyId.SpotifyArtistId
import sttp.model.Uri

// https://developer.spotify.com/documentation/web-api/reference/#object-artistobject
final case class FullArtist(
  externalUrls: ExternalResourceUrl,
  followers: Followers,
  genres: List[String],
  href: Uri,
  id: SpotifyArtistId,
  images: List[SpotifyImage],
  name: String,
  popularity: Int,
  uri: SpotifyUri
)

final case class ArtistsResponse(artists: CursorPage[SpotifyArtistId, FullArtist])
