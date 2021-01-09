package io.bartholomews.spotify4s.playJson

import io.bartholomews.spotify4s.core.entities.{AudioKey, Confidence, PitchClass}
import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json.{JsPath, Json, Reads}

object AudioKeyPlayJson {
  import io.bartholomews.spotify4s.playJson.codecs.confidenceDecoder
  val reads: Reads[AudioKey] =
    (JsPath \ "key")
      .readNullable[PitchClass](Json.valueReads[PitchClass])
      .and((JsPath \ "key_confidence").read[Confidence])(AudioKey.apply _)
}
