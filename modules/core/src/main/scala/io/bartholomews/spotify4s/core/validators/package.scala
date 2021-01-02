package io.bartholomews.spotify4s

import eu.timepit.refined.boolean.And
import eu.timepit.refined.numeric.{Greater, GreaterEqual, Interval, Less, LessEqual}
import eu.timepit.refined.predicates.all.Not
import shapeless.Nat._0
import shapeless.Witness

package object validators {
  private val greaterEqualZeroP: GreaterEqual[_0] = Not(Less(shapeless.nat._0))
  private[spotify4s] def lessEqualP[N](implicit w: Witness.Aux[N]): LessEqual[N] = Not(Greater(w.value))
  private[spotify4s] def maxSizeP[N](implicit w: Witness.Aux[N]): Interval.Closed[_0, N] =
    And(greaterEqualZeroP, lessEqualP)
}
