package io.bartholomews.spotify4s.circe

import io.bartholomews.scalatestudo.WireWordSpec
import io.bartholomews.spotify4s.core.ServerBehaviours
import io.bartholomews.spotify4s.core.entities.SpotifyError
import io.circe
import io.circe.{Decoder, Encoder}
import sttp.client3.BodySerializer

import scala.reflect.ClassTag

trait CirceServerBehaviours extends ServerBehaviours[circe.Encoder, circe.Decoder, circe.Error] with SpotifyCirceApi {
  self: WireWordSpec =>

  import io.bartholomews.spotify4s.circe.codecs._

  override implicit val ct: ClassTag[circe.Error] = ClassTag[circe.Error](circe.Error.getClass)

  implicit def bodySerializer[T](implicit encoder: Encoder[T]): BodySerializer[T] =
    circeBodySerializer

  final def circeParser[B](input: String)(implicit decoder: Decoder[B]): Either[circe.Error, B] =
    io.circe.parser.parse(input).flatMap(_.as[B](decoder))

  final override def parseSpotifyError: String => Either[circe.Error, SpotifyError] =
    input => circeParser[SpotifyError](input)
}
