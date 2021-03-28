package io.bartholomews.spotify4s.playJson

import io.bartholomews.iso_country.CountryCodeAlpha2
import io.bartholomews.spotify4s.core.entities._
import io.bartholomews.spotify4s.playJson.ReleaseDatePlayJson.{releaseDatePrecisionFormat, ReleaseDatePrecision}
import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json.{Format, JsPath, Reads}
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
}
