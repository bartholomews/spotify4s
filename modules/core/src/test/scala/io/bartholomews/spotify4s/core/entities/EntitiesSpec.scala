package io.bartholomews.spotify4s.core.entities

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

abstract class EntitiesSpec[Entity, Encode[_], Decode[_], Json]() extends AnyWordSpec with Matchers {
  def aNiceCodec(entity: Entity, rawJson: String)(
    implicit codecs: JsonCodecs[Entity, Encode, Decode, Json]
  ): Unit = {
    val json = codecs.parse(rawJson) match {
      case Left(error) => fail(error)
      case Right(value) => value
    }

    "encode" in { codecs.encode(entity) shouldBe json }
    "decode" in { codecs.decode(json) shouldBe Right(entity) }
  }
}

trait JsonCodecs[Entity, Encoder[_], Decoder[_], Json] {
  def parse(rawJson: String): Either[String, Json]
  def encode(entity: Entity): Json
  def decode(json: Json): Either[String, Entity]
}