package io.bartholomews.spotify4s.playJson

import io.bartholomews.spotify4s.core.entities.AlbumGroup
import play.api.libs.json.{Format, JsError, JsString, JsSuccess, Reads, Writes}

object AlbumGroupPlayJson {
  val reads: Reads[AlbumGroup] = {
    case JsString(value) =>
      AlbumGroup.values
        .find(_.entryName == value)
        .map(JsSuccess(_))
        .getOrElse(JsError(s"Invalid AlbumGroup: [$value]"))

    case other => JsError(s"Expected a json string, got [$other]")
  }

  val writes: Writes[AlbumGroup] = (o: AlbumGroup) => JsString(o.entryName)
  val format: Format[AlbumGroup] = Format(reads, writes)
}
