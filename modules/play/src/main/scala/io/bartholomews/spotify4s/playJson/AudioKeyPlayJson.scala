package io.bartholomews.spotify4s.playJson

import io.bartholomews.spotify4s.core.entities.{AudioKey, Confidence, PitchClass}
import play.api.libs.functional.syntax.{toFunctionalBuilderOps, unlift}
import play.api.libs.json.{Format, JsPath, Json, Reads, Writes}

object AudioKeyPlayJson {
  import io.bartholomews.spotify4s.playJson.codecs.confidenceCodec
  val reads: Reads[AudioKey] =
    (JsPath \ "key")
      .readNullable[PitchClass](Json.valueReads[PitchClass])
      .and((JsPath \ "key_confidence").read[Confidence])(AudioKey.apply _)

  val writes: Writes[AudioKey] =
    (JsPath \ "key")
      .writeNullable[PitchClass](Json.valueWrites[PitchClass])
      .and((JsPath \ "key_confidence").write[Confidence])(unlift(AudioKey.unapply))

  val codec: Format[AudioKey] = Format(reads, writes)
}
