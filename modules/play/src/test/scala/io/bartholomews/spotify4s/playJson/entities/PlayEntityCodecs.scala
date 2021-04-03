package io.bartholomews.spotify4s.playJson.entities

import io.bartholomews.spotify4s.core.entities.JsonCodecs
import play.api.libs.json.{JsValue, Json, Reads, Writes}

import scala.util.Try

trait PlayEntityCodecs {
  private def toEitherString[A](t: Try[A]): Either[String, A] = t.toEither.left.map(_.getMessage)

  implicit def deriveCodecs[Entity](
    implicit reads: Reads[Entity],
    writes: Writes[Entity]
  ): JsonCodecs[Entity, Writes, Reads, JsValue] =
    new JsonCodecs[Entity, Writes, Reads, JsValue] {
      override def encode(entity: Entity): JsValue = Json.toJson(entity)
      override def decode(json: JsValue): Either[String, Entity] = toEitherString(Try(json.as[Entity]))
      override def parse(rawJson: String): Either[String, JsValue] =
        toEitherString(Try(Json.parse(rawJson)))
    }
}
