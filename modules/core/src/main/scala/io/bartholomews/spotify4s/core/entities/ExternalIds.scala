package io.bartholomews.spotify4s.core.entities

// https://developer.spotify.com/documentation/web-api/reference-beta/#object-externalidobject
sealed trait ExternalIds {
  def value: String
}

object ExternalIds {
  // https://en.wikipedia.org/wiki/International_Standard_Recording_Code
  case class ISRC(value: String) extends ExternalIds
  // https://en.wikipedia.org/wiki/International_Article_Number
  case class EAN(value: String) extends ExternalIds
  // https://en.wikipedia.org/wiki/Universal_Product_Code
  case class UPC(value: String) extends ExternalIds
}
