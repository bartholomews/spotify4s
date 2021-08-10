package io.bartholomews.spotify4s.playJson

import io.bartholomews.spotify4s.core.entities.ExternalResourceUrl
import io.bartholomews.spotify4s.core.entities.ExternalResourceUrl.SpotifyResourceUrl
import play.api.libs.json._
import sttp.model.Uri

private[spotify4s] object ExternalResourceUrlPlayJson {
  import io.bartholomews.spotify4s.playJson.codecs.sttpUriDecoder

  val reads: Reads[ExternalResourceUrl] = {
    case JsObject(objMap) if objMap.isEmpty => JsSuccess(ExternalResourceUrl.Empty)
    case JsObject(objMap) if objMap.size == 1 =>
      objMap
        .get("spotify")
        .map(_.validate[Uri].map(SpotifyResourceUrl.apply))
        .getOrElse(JsError(s"Expected a `spotify` entry, got [$objMap]"))

    case other => JsError(s"Expected a singleton json object, got [$other]")
  }

  val writes: Writes[ExternalResourceUrl] = {
    case ExternalResourceUrl.Empty => JsObject(Seq.empty)
    case SpotifyResourceUrl(uri) => JsObject(Map("spotify" -> JsString(uri.toString)))
  }

  val format: Format[ExternalResourceUrl] = Format(reads, writes)
}
