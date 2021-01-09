package io.bartholomews.spotify4s.playJson

import io.bartholomews.spotify4s.core.entities.{ExternalResourceUrl, SpotifyResourceUrl}
import play.api.libs.json.{Format, JsError, JsObject, JsString, Reads, Writes}
import sttp.model.Uri

private[spotify4s] object ExternalResourceUrlPlayJson {
  import io.bartholomews.spotify4s.playJson.codecs.uriDecoder

  val reads: Reads[ExternalResourceUrl] = {
    case JsObject(objMap) =>
      if (objMap.size != 1) JsError(s"Expected an object with size one, got [$objMap]")
      else
        objMap
          .get("spotify")
          .map(_.validate[Uri].map(SpotifyResourceUrl.apply))
          .getOrElse(JsError(s"Expected a `spotify` entry, got [$objMap]"))

    case other => JsError(s"Expected a json object, got [$other]")
  }

  val writes: Writes[ExternalResourceUrl] = {
    case SpotifyResourceUrl(uri) => JsObject(Map("spotify" -> JsString(uri.toString)))
  }

  val format: Format[ExternalResourceUrl] = Format(reads, writes)
}
