package io.bartholomews.spotify4s.core.entities

import io.bartholomews.iso.{CountryCodeAlpha2, LanguageCode}

final case class Locale(language: LanguageCode, country: CountryCodeAlpha2) {
  val value: String = s"${language.value}_${country.value}"
}
