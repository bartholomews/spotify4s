package io.bartholomews.spotify4s.core.entities

abstract class RestrictionsSpec[Encode[_], Decode[_], Json] extends EntitySpec[Restrictions, Encode, Decode, Json] {

  implicit def restrictionsCodecs: JsonCodecs[Restrictions, Encode, Decode, Json]

  "Restrictions" should {
    behave like aNiceCodec(
      entity = Restrictions("why"),
      rawJson = """{ "reason": "why" }"""
    )
  }
}
