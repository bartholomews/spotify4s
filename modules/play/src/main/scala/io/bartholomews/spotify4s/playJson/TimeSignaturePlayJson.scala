package io.bartholomews.spotify4s.playJson

import io.bartholomews.spotify4s.core.entities.{Confidence, TimeSignature}
import play.api.libs.functional.syntax.{toFunctionalBuilderOps, unlift}
import play.api.libs.json.{Format, JsPath, Reads, Writes}

object TimeSignaturePlayJson {
  import io.bartholomews.spotify4s.playJson.codecs.confidenceCodec
  val reads: Reads[TimeSignature] =
    (JsPath \ "time_signature")
      .read[Double] // TODO: double check Int or Double ?
      .and((JsPath \ "time_signature_confidence").read[Confidence])(TimeSignature.apply _)

  val writes: Writes[TimeSignature] =
    (JsPath \ "time_signature")
      .write[Double] // TODO: double check Int or Double ?
      .and((JsPath \ "time_signature_confidence").write[Confidence])(unlift(TimeSignature.unapply))

  val codec: Format[TimeSignature] = Format(reads, writes)
}
