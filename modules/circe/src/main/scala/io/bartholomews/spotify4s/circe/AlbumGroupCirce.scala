package io.bartholomews.spotify4s.circe

import io.bartholomews.spotify4s.core.entities.AlbumGroup
import io.circe.{Decoder, Encoder, Json}

private[spotify4s] object AlbumGroupCirce {
  val encoder: Encoder[AlbumGroup] = cc => Json.fromString(cc.entryName)
  val decoder: Decoder[AlbumGroup] = Decoder.decodeString.emap(
    str => AlbumGroup.values.find(_.entryName == str).toRight(s"Invalid AlbumGroup: [$str]")
  )
}
