package io.bartholomews.spotify4s.core.entities

import io.bartholomews.iso.CountryCodeAlpha2
import sttp.model.Uri

// https://developer.spotify.com/documentation/web-api/reference/#object-albumobject
final case class FullAlbum(
  albumType: AlbumType,
  artists: List[SimpleArtist],
  availableMarkets: List[CountryCodeAlpha2],
  copyrights: List[Copyright],
  externalIds: ExternalIds,
  externalUrls: ExternalResourceUrl,
  genres: List[String],
  href: Uri,
  id: SpotifyId,
  images: List[SpotifyImage],
  label: String,
  name: String,
  popularity: Int,
  releaseDate: ReleaseDate,
  restrictions: Option[Restrictions],
  tracks: Page[SimpleTrack],
  uri: SpotifyUri
)

final case class FullAlbumsResponse(albums: List[FullAlbum])
