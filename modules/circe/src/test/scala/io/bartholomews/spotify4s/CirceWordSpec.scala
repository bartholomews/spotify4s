package io.bartholomews.spotify4s

import io.bartholomews.scalatestudo.WireWordSpec

trait CirceWordSpec extends WireWordSpec with DiffDerivations {
  override final val testResourcesFileRoot = "modules/circe/src/test/resources"
}
