package io.bartholomews.spotify4s.circe

import io.bartholomews.scalatestudo.entities.JsonCodecs

trait CirceEntityCodecs {
  import io.circe._
  import io.circe.syntax.EncoderOps

  implicit def entityCodecs[Entity](
    implicit encoder: Encoder[Entity],
    decoder: Decoder[Entity]
  ): JsonCodecs[Entity, Encoder, Decoder, Json] =
    new JsonCodecs[Entity, Encoder, Decoder, Json] {
      override implicit def entityEncoder: Encoder[Entity] = encoder
      override implicit def entityDecoder: Decoder[Entity] = decoder
      override def encode(entity: Entity): Json = entity.asJson(encoder)
      override def decode(json: Json): Either[String, Entity] = json.as[Entity](decoder).left.map(_.message)
      override def parse(rawJson: String): Either[String, Json] =
        io.circe.parser.parse(rawJson).left.map(_.message)
    }
}
