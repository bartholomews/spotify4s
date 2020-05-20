package io.bartholomews.spotify4s.entities

import io.bartholomews.iso_country.CountryCodeAlpha2
import io.circe.generic.extras.ConfiguredJsonCodec
import io.circe.{Decoder, HCursor}
import org.http4s.Uri
import org.http4s.circe.decodeUri

/**
  * Album Object (simplified)
  * https://developer.spotify.com/documentation/web-api/reference/object-model/#album-object-simplified
  *
  * @param albumGroup The field is present when getting an artist’s albums.
  *                    Possible values are “album”, “single”, “compilation”, “appears_on”.
  *                    Compare to album_type this field represents relationship between the artist and the album.
  *
  * @param albumType  The type of the album: one of “album”, “single”, or “compilation”.
  *
  * @param artists  The artists of the album.
  *                 Each artist object includes a link in href to more detailed information about the artist.
  *                 (https://developer.spotify.com/documentation/web-api/reference/object-model/#artist-object-simplified)
  *
  * @param availableMarkets The markets in which the album is available: ISO 3166-1 alpha-2 country codes.
  *                         Note that an album is considered available in a market
  *                         when at least 1 of its tracks is available in that market.
  *
  * @param externalUrls Known external URLs for this album.
  *                      (https://developer.spotify.com/documentation/web-api/reference/object-model/#external-url-object)
  *
  * @param href A link to the Web API endpoint providing full details of the album.
  *
  * @param id The Spotify ID for the album.
  *
  * @param images The cover art for the album in various sizes, widest first.
  *                (https://developer.spotify.com/documentation/web-api/reference/object-model/#image-object)
  *
  * @param name The name of the album. In case of an album takedown, the value may be an empty string.
  *
  * @param releaseDate  The date the album was first released, for example 1981.
  *                     Depending on the precision, it might be shown as 1981-12 or 1981-12-15.
  *
  * @param restrictions Part of the response when Track Relinking is applied,
  *                      the original track is not available in the given market,
  *                      and Spotify did not have any tracks to relink it with.
  *                      The track response will still contain metadata for the original track,
  *                      and a restrictions object containing the reason why the track is not available:
  *                      "restrictions" : {"reason" : "market"}
  *
  * @param uri  The Spotify URI for the album.
  *              (https://developer.spotify.com/documentation/web-api/#spotify-uris-and-ids)
  */
@ConfiguredJsonCodec(encodeOnly = true)
case class SimpleAlbum(
  albumGroup: Option[AlbumGroup],
  albumType: AlbumType,
  artists: List[SimpleArtist],
  availableMarkets: List[CountryCodeAlpha2],
  externalUrls: ExternalResourceUrl,
  href: Uri,
  id: SpotifyUserId,
  images: List[SpotifyImage],
  name: String,
  releaseDate: ReleaseDate,
  restrictions: Option[Restrictions],
  uri: SpotifyUri
)

object SimpleAlbum {
  implicit val decoder: Decoder[SimpleAlbum] = (c: HCursor) =>
    for {
      albumGroup <- c.downField("album_group").as[Option[AlbumGroup]]
      albumType <- c.downField("album_type").as[AlbumType]
      artists <- c.downField("artists").as[List[SimpleArtist]]
      availableMarkets <- c.downField("available_markets").as[Option[List[CountryCodeAlpha2]]]
      externalUrls <- c.downField("external_urls").as[ExternalResourceUrl]
      href <- c.downField("href").as[Uri]
      id <- c.downField("id").as[SpotifyUserId]
      images <- c.downField("images").as[List[SpotifyImage]]
      name <- c.downField("name").as[String]
      releaseDate <- c.downField("release_date").as[ReleaseDate]
      restrictions <- c.downField("restriction").as[Option[Restrictions]]
      uri <- c.downField("uri").as[SpotifyUri]
    } yield
      SimpleAlbum(
        albumGroup,
        albumType,
        artists,
        availableMarkets.getOrElse(List.empty),
        externalUrls,
        href,
        id,
        images,
        name,
        releaseDate,
        restrictions,
        uri
    )
}
