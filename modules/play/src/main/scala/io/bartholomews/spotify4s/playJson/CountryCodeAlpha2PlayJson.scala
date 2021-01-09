package io.bartholomews.spotify4s.playJson

import io.bartholomews.iso_country.CountryCodeAlpha2
import play.api.libs.json.{Format, JsError, JsString, JsSuccess, Reads, Writes}

private[spotify4s] object CountryCodeAlpha2PlayJson {
  val reads: Reads[CountryCodeAlpha2] = {
    case JsString(value) =>
      CountryCodeAlpha2.values
        .find(_.value == value)
        .map(JsSuccess(_))
        .getOrElse(JsError(s"Invalid CountryCodeAlpha2: [$value]"))

    case other => JsError(s"Expected a json string, got [$other]")
  }

  val writes: Writes[CountryCodeAlpha2] =
    (o: CountryCodeAlpha2) => JsString(o.value)

  val format: Format[CountryCodeAlpha2] = Format(reads, writes)
}
