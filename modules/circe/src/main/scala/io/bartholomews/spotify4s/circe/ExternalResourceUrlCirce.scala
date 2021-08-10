package io.bartholomews.spotify4s.circe

import io.bartholomews.spotify4s.core.entities.ExternalResourceUrl
import io.bartholomews.spotify4s.core.entities.ExternalResourceUrl.SpotifyResourceUrl
import io.circe.Decoder.Result
import io.circe._
import io.circe.syntax.EncoderOps
import sttp.model.Uri

private[spotify4s] object ExternalResourceUrlCirce {
  import io.bartholomews.fsclient.circe.codecs.sttpUriCodec

  private def decodeExternalResourceUrlJsonObject(json: Json, c: HCursor): Result[ExternalResourceUrl] = {
    json.asObject match {
      case Some(obj) if obj.size == 0 => Right(ExternalResourceUrl.Empty)
      case Some(obj) if obj.size == 1 =>
        obj.toIterable.head match {
          case ("spotify", value) => value.as[Uri].map(SpotifyResourceUrl.apply)
          case (unknown, _) =>
            Left(DecodingFailure(s"SpotifyResourceUrl; unexpected resource url type: [$unknown]", c.history))
        }

      case _ =>
        Left(DecodingFailure("ExternalResourceUrl; expected singleton object", c.history))
    }
  }

  // https://stackoverflow.com/a/57708249
  val decoder: Decoder[ExternalResourceUrl] =
    Decoder.instance(c => decodeExternalResourceUrlJsonObject(c.value, c))

  val encoder: Encoder[ExternalResourceUrl] = {
    case ExternalResourceUrl.Empty => Json.obj()
    case s: SpotifyResourceUrl => Json.obj(("spotify", s.uri.asJson))
  }

  val codec: Codec[ExternalResourceUrl] = Codec.from(decoder, encoder)
}
