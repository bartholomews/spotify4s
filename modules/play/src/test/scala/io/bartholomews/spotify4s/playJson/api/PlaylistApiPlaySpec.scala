package io.bartholomews.spotify4s.playJson.api

import io.bartholomews.spotify4s.core.api.PlaylistApiSpec
import io.bartholomews.spotify4s.core.api.PlaylistApiSpec.{PartialPlaylist, PartialTrack}
import io.bartholomews.spotify4s.playJson.{PlayEntityCodecs, PlayServerBehaviours}
import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json._
import sttp.model.Uri

class PlaylistApiPlaySpec
    extends PlaylistApiSpec[Writes, Reads, JsError, JsValue]
    with PlayServerBehaviours
    with PlayEntityCodecs {
  implicit val partialTrackReads: Reads[PartialTrack] =
    (JsPath \ "track" \ "name")
      .read[String]
      .and((JsPath \ "added_by" \ "id").read[String])(PartialTrack.apply _)

  override implicit val partialPlaylistDecoder: Reads[PartialPlaylist] =
    (JsPath \ "description")
      .read[String]
      .and((JsPath \ "href").read[Uri])
      .and((JsPath \ "tracks" \ "items").read[List[PartialTrack]])(PartialPlaylist.apply _)
}
