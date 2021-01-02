package io.bartholomews.spotify4s.circe

import io.bartholomews.iso_country.CountryCodeAlpha2
import io.bartholomews.spotify4s.core.entities.{
  AlbumGroup,
  AlbumType,
  ExternalResourceUrl,
  ReleaseDate,
  Restrictions,
  SimpleAlbum,
  SimpleArtist,
  SpotifyId,
  SpotifyImage,
  SpotifyUri
}
import io.circe.{Decoder, HCursor}
import sttp.model.Uri

object CirceSimpleAlbum {
  implicit val decoder: Decoder[SimpleAlbum] = (c: HCursor) =>
    for {
      albumGroup <- c.downField("album_group").as[Option[AlbumGroup]]
      albumType <- c.downField("album_type").as[Option[AlbumType]]
      artists <- c.downField("artists").as[List[SimpleArtist]]
      availableMarkets <- c.downField("available_markets").as[Option[List[CountryCodeAlpha2]]]
      externalUrls <- Right(c.downField("external_urls").as[ExternalResourceUrl].toOption)
      href <- c.downField("href").as[Option[Uri]]
      id <- c.downField("id").as[Option[SpotifyId]]
      images <- c.downField("images").as[List[SpotifyImage]]
      name <- c.downField("name").as[String]
      releaseDate <- c.downField("release_date").as[Option[ReleaseDate]]
      restrictions <- c.downField("restriction").as[Option[Restrictions]]
      uri <- c.downField("uri").as[Option[SpotifyUri]]
    } yield SimpleAlbum(
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
