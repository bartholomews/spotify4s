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
import sttp.model.Uri

private[spotify4s] object SimpleAlbumPlayJson {
  import io.bartholomews.spotify4s.playJson.codecs._
  import play.api.libs.json._

  implicit val rdpFormat: Format[ReleaseDatePrecision] = releaseDatePrecisionFormat

  val reads: Reads[SimpleAlbum] =
    (JsPath \ "album_group")
      .readNullable[AlbumGroup]
      .and((JsPath \ "album_type").readNullable[AlbumType](AlbumTypePlayJson.reads))
      .and((JsPath \ "artists").read[List[SimpleArtist]](Reads.list(SimpleArtistPlayJson.reads)))
      .and((JsPath \ "available_markets").read[List[CountryCodeAlpha2]].orElse(Reads.pure(List.empty)))
      .and(
        (JsPath \ "external_urls")
          .read[ExternalResourceUrl](externalResourceUrlCodec)
          .map(Option(_))
          .orElse(Reads.pure(None))
      )
      .and((JsPath \ "href").readNullable[Uri])
      .and((JsPath \ "id").readNullable[SpotifyId](spotifyIdCodec))
      .and((JsPath \ "images").read[List[SpotifyImage]](Reads.list(spotifyImageCodec)))
      .and((JsPath \ "name").read[String])
      .and({
        val rdp = (JsPath \ "release_date_precision").readNullable[ReleaseDatePrecision]
        val rd = (JsPath \ "release_date").readNullable[String]
        ReleaseDatePlayJson.decodeNullableReleaseDate(rdp, rd)
      })
      .and((JsPath \ "restrictions").readNullable[Restrictions](restrictionsCodec))
      .and((JsPath \ "uri").readNullable[SpotifyUri](spotifyUriCodec))(SimpleAlbum.apply _)

  val writes: Writes[SimpleAlbum] = (o: SimpleAlbum) =>
    JsObject(
      Map(
        "album_group" -> Json.toJson(o.albumGroup),
        "album_type" -> Json.toJson(o.albumType),
        "artists" -> Json.toJson(o.artists),
        "available_markets" -> Json.toJson(o.availableMarkets),
        "external_urls" -> Json.toJson(o.externalUrls),
        "href" -> Json.toJson(o.href),
        "id" -> Json.toJson(o.id),
        "images" -> Json.toJson(o.images),
        "name" -> Json.toJson(o.name),
        "release_date_precision" -> Json.toJson(o.releaseDate.map(ReleaseDatePrecision.fromReleaseDate)),
        "release_date" -> Json.toJson(o.releaseDate.map(_.toString)),
        "restrictions" -> Json.toJson(o.restrictions),
        "uri" -> Json.toJson(o.uri)
      )
    )

  val format: Format[SimpleAlbum] = Format(reads, writes)
}
