package io.bartholomews.spotify4s.playJson

import io.bartholomews.iso.CountryCodeAlpha2
import io.bartholomews.spotify4s.core.entities._
import io.bartholomews.spotify4s.playJson.ReleaseDatePlayJson.{ReleaseDatePrecision, releaseDatePrecisionFormat}
import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json.{Format, JsObject, JsPath, JsString, Json, Reads, Writes}
import sttp.model.Uri

object FullAlbumPlayJson {
  import codecs._
  implicit val rdpFormat: Format[ReleaseDatePrecision] = releaseDatePrecisionFormat

  implicit val reads: Reads[FullAlbum] = (JsPath \ "album_type")
    .read[AlbumType]
    .and((JsPath \ "artists").read[List[SimpleArtist]](Reads.list(SimpleArtistPlayJson.reads)))
    .and((JsPath \ "available_markets").read[List[CountryCodeAlpha2]].orElse(Reads.pure(List.empty)))
    .and((JsPath \ "copyrights").read[List[Copyright]])
    .and((JsPath \ "external_ids").read[ExternalIds])
    .and((JsPath \ "external_urls").read[ExternalResourceUrl](externalResourceUrlCodec))
    .and((JsPath \ "genres").read[List[String]])
    .and((JsPath \ "href").read[Uri])
    .and((JsPath \ "id").read[SpotifyId](spotifyIdCodec))
    .and((JsPath \ "images").read[List[SpotifyImage]](Reads.list(spotifyImageCodec)))
    .and((JsPath \ "label").read[String])
    .and((JsPath \ "name").read[String])
    .and((JsPath \ "popularity").read[Int])
    .and({
      val rdp = (JsPath \ "release_date_precision").read[ReleaseDatePrecision]
      val rd = (JsPath \ "release_date").read[String]
      ReleaseDatePlayJson.decodeReleaseDate(rdp, rd)
    })
    .and((JsPath \ "restrictions").readNullable[Restrictions](restrictionsCodec))
    .and((JsPath \ "tracks").read[Page[SimpleTrack]])
    .and((JsPath \ "uri").read[SpotifyUri](spotifyUriCodec))(FullAlbum.apply _)

  val writes: Writes[FullAlbum] = (o: FullAlbum) =>
    JsObject(
      Map(
        "album_type" -> Json.toJson(o.albumType),
        "artists" -> Json.toJson(o.artists),
        "available_markets" -> Json.toJson(o.availableMarkets),
        "copyrights" -> Json.toJson(o.copyrights),
        "external_ids" -> Json.toJson(o.externalIds),
        "external_urls" -> Json.toJson(o.externalUrls),
        "genres" -> Json.toJson(o.genres),
        "href" -> Json.toJson(o.href),
        "id" -> Json.toJson(o.id),
        "images" -> Json.toJson(o.images),
        "label" -> Json.toJson(o.label),
        "name" -> Json.toJson(o.name),
        "popularity" -> Json.toJson(o.popularity),
        "release_date_precision" -> Json.toJson(ReleaseDatePrecision.fromReleaseDate(o.releaseDate)),
        "release_date" -> JsString(o.releaseDate.toString),
        "restrictions" -> Json.toJson(o.restrictions),
        "tracks" -> Json.toJson(o.tracks),
        "uri" -> Json.toJson(o.uri)
      )
    )

  val format: Format[FullAlbum] = Format(reads, writes)
}
