package io.bartholomews.spotify4s.playJson

import io.bartholomews.spotify4s.core.entities.TimeInterval.{Bar, Beat, Tatum}
import io.bartholomews.spotify4s.core.entities.{Confidence, TimeInterval}
import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json.{JsPath, Reads}

object TimeIntervalPlayJson {
  import io.bartholomews.spotify4s.playJson.codecs.confidenceDecoder
  def reads[A <: TimeInterval](apply: (Double, Double, Confidence) => A): Reads[A] =
    (JsPath \ "start")
      .read[Double] // TODO: double check Int or Double ?
      .and((JsPath \ "duration").read[Double])
      .and((JsPath \ "confidence").read[Confidence].orElse(Reads.pure(Confidence(0.0))))(apply)

  val barDecoder: Reads[Bar] = reads(Bar.apply)
  val beatDecoder: Reads[Beat] = reads(Beat.apply)
  val tatumDecoder: Reads[Tatum] = reads(Tatum.apply)
}
