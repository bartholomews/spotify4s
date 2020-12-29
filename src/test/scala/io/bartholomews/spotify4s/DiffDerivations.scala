package io.bartholomews.spotify4s

import com.softwaremill.diffx.scalatest.DiffMatcher
import com.softwaremill.diffx.{Derived, Diff, DiffResult, DiffResultObject, Identical}
import io.bartholomews.fsclient.core.oauth.{AccessTokenSigner, NonRefreshableTokenSigner}
import sttp.model.Uri

trait DiffDerivations extends DiffMatcher {
  implicit val accessTokenSignerDiff: Diff[AccessTokenSigner] =
    Derived[Diff[AccessTokenSigner]].ignore[AccessTokenSigner, Long](_.generatedAt)

  implicit val nonRefreshableTokenSignerDiff: Diff[NonRefreshableTokenSigner] =
    Derived[Diff[NonRefreshableTokenSigner]].ignore[NonRefreshableTokenSigner, Long](_.generatedAt)

  /**
    * implicit val diffCaseClassWithPrivateConstructor: Diff[CaseClassWithPrivateConstructor] =
    *    (left: CaseClassWithPrivateConstructor, right: CaseClassWithPrivateConstructor, _) => {
    *      fromObject(
    *        left,
    *        DiffResultObject(
    *            name = "CaseClassWithPrivateConstructor",
    *            fields = Map(
    *                "fieldA" -> Diff[FieldA].apply(left.fieldA, right.fieldA),
    *                "fieldB" -> `Diff[Option[FieldB]]`.apply(left.fieldB, right.fieldB)
    *              )
    *       )
    *    )
    * }
    *
    * @param value the `T` value to test
    * @param diffResultObject the DiffResultObject implementation for the specific type T
    * @tparam T the type of the object to write the custom diff
    * @return a custom `DiffResult` for `T`
    */
  def fromObject[T](value: T, diffResultObject: DiffResultObject): DiffResult =
    diffResultObject.fields.values
      .collectFirst({ case result if !result.isIdentical => diffResultObject })
      .getOrElse(Identical(value))

  implicit val diffUri: Derived[Diff[Uri]] = Derived(Diff[String].contramap[Uri](_.toString()))
}

object DiffDerivations extends DiffDerivations
