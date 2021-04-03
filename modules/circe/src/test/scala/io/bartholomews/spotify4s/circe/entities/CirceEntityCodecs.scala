package io.bartholomews.spotify4s.circe.entities

import io.bartholomews.spotify4s.core.entities.JsonCodecs

trait CirceEntityCodecs {
  import io.circe._
  import io.circe.syntax.EncoderOps

  implicit def deriveCodecs[Entity](
    implicit decoder: Decoder[Entity],
    encoder: Encoder[Entity]
  ): JsonCodecs[Entity, Encoder, Decoder, Json] =
    new JsonCodecs[Entity, Encoder, Decoder, Json] {
      override def encode(entity: Entity): Json = entity.asJson
      override def decode(json: Json): Either[String, Entity] = json.as[Entity].left.map(_.message)
      override def parse(rawJson: String): Either[String, Json] =
        io.circe.parser.parse(rawJson).left.map(_.message)
    }
}
