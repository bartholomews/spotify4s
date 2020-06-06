package io.bartholomews.spotify4s.entities

import enumeratum.EnumEntry.Snakecase
import enumeratum._

sealed trait AlbumGroup extends EnumEntry with Snakecase

case object AlbumGroup extends Enum[AlbumGroup] with CirceEnum[AlbumGroup] {
  case object Album extends AlbumGroup
  case object Single extends AlbumGroup
  case object Compilation extends AlbumGroup
  case object AppearsOn extends AlbumGroup

  override val values: IndexedSeq[AlbumGroup] = findValues
}
