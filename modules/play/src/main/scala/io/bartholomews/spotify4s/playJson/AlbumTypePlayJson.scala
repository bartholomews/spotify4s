package io.bartholomews.spotify4s.playJson

import io.bartholomews.spotify4s.core.entities.AlbumType
import play.api.libs.json.{Format, JsError, JsString, JsSuccess, Reads, Writes}

object AlbumTypePlayJson {
  val reads: Reads[AlbumType] = {
    case JsString(value) =>
      AlbumType.values
        .find(_.entryName == value)
        .map(JsSuccess(_))
        .getOrElse(JsError(s"Invalid AlbumGroup: [$value]"))

    case other => JsError(s"Expected a json string, got [$other]")
  }

  val writes: Writes[AlbumType] = (o: AlbumType) => JsString(o.entryName)
  val format: Format[AlbumType] = Format(reads, writes)
}
