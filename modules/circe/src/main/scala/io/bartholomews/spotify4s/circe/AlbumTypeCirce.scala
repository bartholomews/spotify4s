package io.bartholomews.spotify4s.circe

import io.bartholomews.spotify4s.core.entities.AlbumType
import io.circe.{Decoder, Encoder, Json}

private[spotify4s] object AlbumTypeCirce {
  val encoder: Encoder[AlbumType] = cc => Json.fromString(cc.entryName)
  val decoder: Decoder[AlbumType] = Decoder.decodeString.emap(
    str => AlbumType.values.find(_.entryName == str).toRight(s"Invalid AlbumType: [$str]")
  )
}
