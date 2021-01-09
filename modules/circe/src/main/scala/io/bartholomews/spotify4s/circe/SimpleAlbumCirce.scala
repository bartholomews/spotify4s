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

private[spotify4s] object SimpleAlbumCirce {
  import codecs._
  import io.bartholomews.fsclient.circe.sttpUriCodec
  import io.circe.Decoder.{decodeList, decodeOption}
  val decoder: Decoder[SimpleAlbum] = (c: HCursor) =>
    for {
      albumGroup <- c.downField("album_group").as[Option[AlbumGroup]](decodeOption(AlbumGroupCirce.decoder))
      albumType <- c.downField("album_type").as[Option[AlbumType]](decodeOption(AlbumTypeCirce.decoder))
      artists <- c.downField("artists").as[List[SimpleArtist]](decodeList(SimpleArtistCirce.decoder))
      availableMarkets <- c.downField("available_markets")
                           .as[Option[List[CountryCodeAlpha2]]](
                             decodeOption(decodeList(CountryCodeAlpha2Circe.decoder))
                           )
      externalUrls <- Right(c.downField("external_urls").as[ExternalResourceUrl].toOption)
      href <- c.downField("href").as[Option[Uri]]
      id <- c.downField("id").as[Option[SpotifyId]]
      images <- c.downField("images").as[List[SpotifyImage]]
      name <- c.downField("name").as[String]
      releaseDate <- c.downField("release_date").as[Option[ReleaseDate]](decodeOption(ReleaseDateCirce.decoder))
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
