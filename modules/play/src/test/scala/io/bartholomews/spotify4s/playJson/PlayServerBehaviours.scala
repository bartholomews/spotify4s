package io.bartholomews.spotify4s.playJson

import io.bartholomews.scalatestudo.WireWordSpec
import io.bartholomews.spotify4s.playJson.SpotifyPlayJsonApi
import io.bartholomews.spotify4s.core.ServerBehaviours
import io.bartholomews.spotify4s.core.entities.SpotifyError
import play.api.libs.json.{Format, JsError, Reads}

import scala.reflect.ClassTag

trait PlayServerBehaviours extends ServerBehaviours[Reads, JsError] with SpotifyPlayJsonApi {

  self: WireWordSpec =>

  override implicit val ct: ClassTag[JsError] = ClassTag[JsError](JsError.getClass)

  import play.api.libs.json._

  final def playParser[B](input: String)(implicit rds: Reads[B]): Either[JsError, B] =
    Json.parse(input).validate[B] match {
      case JsSuccess(value, _) => Right(value)
      case error: JsError => Left(error)
    }

  final override def parseSpotifyError: String => Either[JsError, SpotifyError] =
    input => playParser[SpotifyError](input)
}
