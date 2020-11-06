package io.bartholomews.spotify4s.entities

import io.circe.{Decoder, HCursor}

case class SnapshotId(value: String) extends AnyVal

object SnapshotId {
  implicit val decoder: Decoder[SnapshotId] = (c: HCursor) =>
    c.downField("snapshot_id").as[String].map(SnapshotId.apply)
}
