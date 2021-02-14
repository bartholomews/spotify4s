package io.bartholomews.spotify4s.playJson.api

import io.bartholomews.spotify4s.core.api.BrowseApiSpec
import io.bartholomews.spotify4s.core.entities.NewReleases
import io.bartholomews.spotify4s.playJson.PlayServerBehaviours
import play.api.libs.json.{JsError, Reads, Writes}

class BrowseApiPlaySpec extends BrowseApiSpec[Writes, Reads, JsError] with PlayServerBehaviours {
  override implicit def newReleasesDecoder: Reads[NewReleases] = newReleasesCodec
}
