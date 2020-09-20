package io.bartholomews.spotify4s

import cats.data.NonEmptyList
import eu.timepit.refined.api.Validate
import eu.timepit.refined.api.Validate.Plain
import eu.timepit.refined.boolean.And
import eu.timepit.refined.numeric.{Greater, GreaterEqual, Interval, Less, LessEqual}
import eu.timepit.refined.predicates.all.{Not, Size}
import eu.timepit.refined.predicates.collection.MaxSize
import io.bartholomews.spotify4s.entities.SpotifyUri
import shapeless.Nat._0
import shapeless.Witness

package object validators {
  private val greaterEqualZeroP: GreaterEqual[_0] = Not(Less(shapeless.nat._0))
  private def lessEqualP[N](implicit w: Witness.Aux[N]): LessEqual[N] = Not(Greater(w.value))
  private def maxSizeP[N](implicit w: Witness.Aux[N]): Interval.Closed[_0, N] = And(greaterEqualZeroP, lessEqualP)

  implicit def validateSpotifyUris: Plain[NonEmptyList[SpotifyUri], MaxSize[100]] = {
    Validate
      .fromPredicate(
        (d: NonEmptyList[SpotifyUri]) => d.length <= 100,
        (_: NonEmptyList[SpotifyUri]) => "a maximum of 100 uris can be set in one request",
        Size[Interval.Closed[_0, Witness.`100`.T]](maxSizeP)
      )
  }
}
