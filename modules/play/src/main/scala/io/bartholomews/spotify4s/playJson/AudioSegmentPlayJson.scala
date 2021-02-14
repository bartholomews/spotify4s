package io.bartholomews.spotify4s.playJson

import io.bartholomews.spotify4s.core.entities.{AudioSegment, Confidence, Loudness}
import play.api.libs.functional.syntax.{toFunctionalBuilderOps, unlift}
import play.api.libs.json.{Format, JsPath, Reads, Writes}

//noinspection DuplicatedCode
object AudioSegmentPlayJson {
  import io.bartholomews.spotify4s.playJson.codecs.confidenceCodec
  val reads: Reads[AudioSegment] =
    (JsPath \ "start")
      .read[Double]
      .orElse(Reads.pure(0.0))
      .and((JsPath \ "duration").read[Double])
      .and((JsPath \ "confidence").read[Confidence])
      .and(JsPath.read[Loudness](LoudnessPlayJson.reads))
      .and((JsPath \ "pitches").read[List[Double]])
      .and((JsPath \ "timbre").read[List[Double]])(AudioSegment.apply _)

  val writes: Writes[AudioSegment] =
    (JsPath \ "start")
      .write[Double]
      .and((JsPath \ "duration").write[Double])
      .and((JsPath \ "confidence").write[Confidence])
      .and(JsPath.write[Loudness](LoudnessPlayJson.writes))
      .and((JsPath \ "pitches").write[List[Double]])
      .and((JsPath \ "timbre").write[List[Double]])(unlift(AudioSegment.unapply))

  val format: Format[AudioSegment] = Format(reads, writes)
}
