package io.bartholomews.spotify4s.core.entities

import enumeratum.EnumEntry.Snakecase
import enumeratum._

sealed trait SubscriptionLevel extends EnumEntry with Snakecase

case object SubscriptionLevel extends Enum[SubscriptionLevel] {
  case object Premium extends SubscriptionLevel
  case object Free extends SubscriptionLevel
  case class Unknown(value: Option[String]) extends SubscriptionLevel

  override val values: IndexedSeq[SubscriptionLevel] = findValues
}
