package io.bartholomews.spotify4s.core.entities

abstract class RestrictionsEntitiesSpec[Encode[_], Decode[_], Json]
    extends EntitiesSpec[Restrictions, Encode, Decode, Json] {

  implicit def codecs: JsonCodecs[Restrictions, Encode, Decode, Json]

  "Restrictions" should {
    behave like aNiceCodec(
      entity = Restrictions("why"),
      rawJson = """{ "reason": "why" }"""
    )
  }
}
