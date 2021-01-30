package io.bartholomews.spotify4s.circe.api

import io.bartholomews.scalatestudo.WireWordSpec
import io.bartholomews.spotify4s.circe.CirceServerBehaviours
import io.bartholomews.spotify4s.core.api.PlaylistApiSpec
import io.bartholomews.spotify4s.core.api.PlaylistApiSpec.{PartialPlaylist, PartialTrack}
import io.circe
import io.circe.{Decoder, HCursor}
import sttp.model.Uri

class PlaylistsApiCirceSpec
    extends PlaylistApiSpec[circe.Encoder, circe.Decoder, circe.Error]
    with WireWordSpec
    with CirceServerBehaviours {
  implicit val partialPlaylistDecoder: Decoder[PartialPlaylist] = (c: HCursor) =>
    for {
      description <- c.downField("description").as[String]
      href <- c.downField("href").as[Uri](Decoder.decodeString.emap(Uri.parse))
      tracks <- c.downField("tracks").downField("items").as[List[PartialTrack]]
    } yield PartialPlaylist(description, href, tracks)

  implicit val partialTrackDecoder: Decoder[PartialTrack] = (c: HCursor) =>
    for {
      trackName <- c.downField("track").downField("name").as[String]
      addedBy <- c.downField("added_by").downField("id").as[String]
    } yield PartialTrack(trackName, addedBy)
}
