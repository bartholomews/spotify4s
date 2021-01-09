package io.bartholomews.spotify4s.playJson

import io.bartholomews.spotify4s.core.entities.Loudness
import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json.{JsPath, Reads}

object LoudnessPlayJson {
  val reads: Reads[Loudness] =
    (JsPath \ "loudness_start")
      .read[Double]
      .and((JsPath \ "loudness_max").read[Double])
      .and((JsPath \ "loudness_max_time").read[Double])
      .and((JsPath \ "loudness_end").readNullable[Double])(Loudness.apply _)
}
