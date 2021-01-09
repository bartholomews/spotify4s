package io.bartholomews.spotify4s.playJson

import io.bartholomews.spotify4s.core.entities.Modality
import play.api.libs.json.{Format, JsError, JsNumber, JsSuccess, Reads, Writes}

private[spotify4s] object ModalityPlayJson {
  val reads: Reads[Modality] = {
    case JsNumber(intValue) =>
      Modality.values
        .find(_.value == intValue)
        .map(JsSuccess(_))
        .getOrElse(JsError(s"[$intValue] is not a valid Modality value"))

    case other => JsError(s"Expected a json number, got [$other]")
  }

  val writes: Writes[Modality] =
    (o: Modality) => JsNumber(o.value)

  val format: Format[Modality] = Format(reads, writes)
}
