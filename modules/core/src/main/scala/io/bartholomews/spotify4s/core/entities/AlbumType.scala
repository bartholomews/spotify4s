package io.bartholomews.spotify4s.core.entities

import enumeratum.EnumEntry.Snakecase
import enumeratum._

sealed trait AlbumType extends EnumEntry with Snakecase

case object AlbumType extends Enum[AlbumType] {
  case object Album extends AlbumType
  case object Single extends AlbumType
  case object Compilation extends AlbumType

  override val values: IndexedSeq[AlbumType] = findValues
}
