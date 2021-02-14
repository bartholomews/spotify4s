package io.bartholomews.spotify4s.playJson

import io.bartholomews.spotify4s.core.entities.TimeInterval.{Bar, Beat, Tatum}
import io.bartholomews.spotify4s.core.entities.{Confidence, TimeInterval}
import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json.{Format, JsPath, Json, Reads}

object TimeIntervalPlayJson {
  import io.bartholomews.spotify4s.playJson.codecs.confidenceCodec
  def reads[A <: TimeInterval](apply: (Double, Double, Confidence) => A): Reads[A] =
    (JsPath \ "start")
      .read[Double] // TODO: double check Int or Double ?
      .and((JsPath \ "duration").read[Double])
      .and((JsPath \ "confidence").read[Confidence].orElse(Reads.pure(Confidence(0.0))))(apply)

  val barFormat: Format[Bar] = Format(reads(Bar.apply), Json.writes)
  val beatFormat: Format[Beat] = Format(reads(Beat.apply), Json.writes)
  val tatumFormat: Format[Tatum] = Format(reads(Tatum.apply), Json.writes)
}
