package io.bartholomews.spotify4s.playJson

import io.bartholomews.spotify4s.core.entities.Loudness
import play.api.libs.functional.syntax.{toFunctionalBuilderOps, unlift}
import play.api.libs.json.{Format, JsPath, Reads, Writes}

object LoudnessPlayJson {
  val reads: Reads[Loudness] =
    (JsPath \ "loudness_start")
      .read[Double]
      .and((JsPath \ "loudness_max").read[Double])
      .and((JsPath \ "loudness_max_time").read[Double])
      .and((JsPath \ "loudness_end").readNullable[Double])(Loudness.apply _)

  val writes: Writes[Loudness] =
    (JsPath \ "loudness_start")
      .write[Double]
      .and((JsPath \ "loudness_max").write[Double])
      .and((JsPath \ "loudness_max_time").write[Double])
      .and((JsPath \ "loudness_end").writeNullable[Double])(unlift(Loudness.unapply))

  val codec: Format[Loudness] = Format(reads, writes)
}
