package io.bartholomews.spotify4s.playJson

import io.bartholomews.scalatestudo.entities.JsonCodecs
import play.api.libs.json.{JsValue, Json, Reads, Writes}

import scala.util.Try

trait PlayEntityCodecs {
  private def toEitherString[A](t: Try[A]): Either[String, A] = t.toEither.left.map(_.getMessage)

  implicit def entityCodecs[Entity](
    implicit writes: Writes[Entity],
    reads: Reads[Entity]
  ): JsonCodecs[Entity, Writes, Reads, JsValue] =
    new JsonCodecs[Entity, Writes, Reads, JsValue] {
      override implicit def entityEncoder: Writes[Entity] = writes
      override implicit def entityDecoder: Reads[Entity] = reads
      override def encode(entity: Entity): JsValue = Json.toJson(entity)(writes)
      override def decode(json: JsValue): Either[String, Entity] = toEitherString(Try(json.as[Entity](reads)))
      override def parse(rawJson: String): Either[String, JsValue] =
        toEitherString(Try(Json.parse(rawJson)))
    }
}
