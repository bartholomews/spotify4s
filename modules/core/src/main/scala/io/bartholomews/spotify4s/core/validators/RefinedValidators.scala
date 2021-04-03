package io.bartholomews.spotify4s.core.validators

import cats.data.NonEmptyList
import eu.timepit.refined.boolean.{And, Not}
import eu.timepit.refined.numeric.{Greater, GreaterEqual, Interval, Less, LessEqual}
import shapeless.Nat._0
import shapeless.Witness

private[spotify4s] object RefinedValidators {
  private val greaterEqualZeroP: GreaterEqual[_0] = Not(Less(shapeless.nat._0))
  def lessEqualP[N](implicit w: Witness.Aux[N]): LessEqual[N] = Not(Greater(w.value))
  def maxSizeP[N](implicit w: Witness.Aux[N]): Interval.Closed[_0, N] =
    And(greaterEqualZeroP, lessEqualP)

  abstract class NelMaxSizeValidators[A, R](maxSize: Int) {
    def fromNel(xs: NonEmptyList[A]): Either[String, R]

    def fromList(xs: List[A]): Either[String, R] = {
      import cats.syntax.either._
      Either
        .fromOption(NonEmptyList.fromList(xs), ifNone = "Predicate failed: need to provide at least one element.")
        .map(fromNel)
        .joinRight
    }

    /**
      * Group a NEL in batches of max size (100)
      * (so each element can be mapped to each separate request).
      * @param xs the NEL to be grouped
      * @return a nel of grouped elements
      */
    def grouped(xs: NonEmptyList[A]): NonEmptyList[R] = {
      NonEmptyList.fromListUnsafe(
        xs.toList
          .grouped(maxSize)
          .toList
          .map(
            xs => fromList(xs).fold(error => throw new Exception(s"Unexpected refinement error: [$error]"), identity)
          )
      )
    }
  }
}
