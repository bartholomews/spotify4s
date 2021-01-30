package io.bartholomews.spotify4s.playJson

import io.bartholomews.iso_country.CountryCodeAlpha2
import io.bartholomews.spotify4s.core.entities.{
  AlbumGroup,
  AlbumType,
  ExternalResourceUrl,
  Restrictions,
  SimpleAlbum,
  SimpleArtist,
  SpotifyId,
  SpotifyImage,
  SpotifyUri
}
import io.bartholomews.spotify4s.playJson.ReleaseDatePlayJson.{releaseDatePrecisionFormat, ReleaseDatePrecision}
import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json.{JsPath, Reads}
import sttp.model.Uri

private[spotify4s] object SimpleAlbumPlayJson {
  import io.bartholomews.spotify4s.playJson.codecs._
  import play.api.libs.json.Reads.list
  val reads: Reads[SimpleAlbum] =
    (JsPath \ "album_group")
      .readNullable[AlbumGroup]
      .and((JsPath \ "album_type").readNullable[AlbumType](AlbumTypePlayJson.reads))
      .and((JsPath \ "artists").read[List[SimpleArtist]](list(SimpleArtistPlayJson.reads)))
      .and((JsPath \ "available_markets").read[List[CountryCodeAlpha2]].orElse(Reads.pure(List.empty)))
      .and(
        (JsPath \ "external_urls")
          .read[ExternalResourceUrl](externalResourceUrlDecoder)
          .map(Option(_))
          .orElse(Reads.pure(None))
      )
      .and((JsPath \ "href").readNullable[Uri](uriDecoder))
      .and((JsPath \ "id").readNullable[SpotifyId](spotifyIdDecoder))
      .and((JsPath \ "images").read[List[SpotifyImage]](list(spotifyImageDecoder)))
      .and((JsPath \ "name").read[String])
      .and({
        val rdp = (JsPath \ "release_date_precision").readNullable[ReleaseDatePrecision](releaseDatePrecisionFormat)
        val rd = (JsPath \ "release_date").readNullable[String]
        ReleaseDatePlayJson.decodeNullableReleaseDate(rdp, rd)
      })
      .and((JsPath \ "restrictions").readNullable[Restrictions](restrictionsDecoder))
      .and((JsPath \ "uri").readNullable[SpotifyUri](spotifyUriDecoder))(SimpleAlbum.apply _)
}
