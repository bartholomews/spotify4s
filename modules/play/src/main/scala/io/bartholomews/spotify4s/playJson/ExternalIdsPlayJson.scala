package io.bartholomews.spotify4s.playJson

import io.bartholomews.spotify4s.core.entities.ExternalIds
import io.bartholomews.spotify4s.core.entities.ExternalIds._
import play.api.libs.json._

object ExternalIdsPlayJson {
  val reads: Reads[ExternalIds] = {
    case JsObject(objMap) =>
      if (objMap.size != 1) JsError(s"Expected an object with size one, got [$objMap]")
      objMap.toList match {
        case ("isrc", JsString(value)) :: Nil => JsSuccess(ISRC(value))
        case ("ean", JsString(value)) :: Nil => JsSuccess(EAN(value))
        case ("upc", JsString(value)) :: Nil => JsSuccess(UPC(value))
        case _ => JsError(s"Invalid `ExternalIds` object: [$objMap]")
      }

    case other => JsError(s"Expected a json object, got [$other]")
  }

  val writes: Writes[ExternalIds] = (o: ExternalIds) => {
    val key = o match {
      case _: ISRC => "isrc"
      case _: EAN => "ean"
      case _: UPC => "upc"
    }
    JsObject(Map(key -> JsString(o.value)))
  }

  val format: Format[ExternalIds] = Format(reads, writes)
}
