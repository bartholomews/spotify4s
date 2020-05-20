package io.bartholomews.spotify4s.entities

import io.bartholomews.iso_country.CountryCodeAlpha2

// https://developer.spotify.com/documentation/general/guides/track-relinking-guide/
sealed trait Market {
  def value: String
}

case object FromToken extends Market {
  final val value = "from_token"
}

case class IsoCountry private (value: String, name: String) extends Market

object IsoCountry {
  def apply(alpha2: CountryCodeAlpha2): IsoCountry = IsoCountry(alpha2.value, alpha2.name)
}
