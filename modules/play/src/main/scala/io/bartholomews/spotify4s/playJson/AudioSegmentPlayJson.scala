package io.bartholomews.spotify4s.playJson

import io.bartholomews.spotify4s.core.entities.{AudioSegment, Confidence, Loudness}
import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json.{JsPath, Reads}

object AudioSegmentPlayJson {
  import io.bartholomews.spotify4s.playJson.codecs.confidenceDecoder
  //noinspection DuplicatedCode
  val reads: Reads[AudioSegment] =
    (JsPath \ "start")
      .read[Double]
      .orElse(Reads.pure(0.0))
      .and((JsPath \ "duration").read[Double])
      .and((JsPath \ "confidence").read[Confidence])
      .and(JsPath.read[Loudness](LoudnessPlayJson.reads))
      .and((JsPath \ "pitches").read[List[Double]])
      .and((JsPath \ "timbre").read[List[Double]])(AudioSegment.apply _)
}
