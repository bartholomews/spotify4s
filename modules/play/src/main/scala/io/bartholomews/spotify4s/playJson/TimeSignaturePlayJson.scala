package io.bartholomews.spotify4s.playJson

import io.bartholomews.spotify4s.core.entities.{Confidence, TimeSignature}
import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json.{JsPath, Reads}

object TimeSignaturePlayJson {
  import io.bartholomews.spotify4s.playJson.codecs.confidenceDecoder
  val reads: Reads[TimeSignature] =
    (JsPath \ "time_signature")
      .read[Double] // TODO: double check Int or Double ?
      .and((JsPath \ "time_signature_confidence").read[Confidence])(TimeSignature.apply _)
}
