package io.bartholomews.spotify4s.playJson

import io.bartholomews.spotify4s.core.entities.{ApiError, AuthError, SpotifyError}
import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json.{Format, JsError, JsNumber, JsObject, JsPath, JsString, Reads, Writes}

private[spotify4s] object SpotifyErrorPlayJson {
  val apiErrorWrites: Writes[ApiError] = (o: ApiError) =>
    JsObject(
      List(Tuple2("error", JsObject.apply(Map("status" -> JsNumber(o.status), "message" -> JsString(o.message)))))
    )

  val apiErrorReads: Reads[ApiError] = {
    case JsObject(objMap) =>
      if (objMap.size != 1) JsError(s"Expected an object with size one, got [$objMap]")
      objMap
        .get("error")
        .map(
          _.validate[ApiError](
            (JsPath \ "status")
              .read[Int]
              .and((JsPath \ "message").read[String])(ApiError.apply _)
          )
        )
        .getOrElse(JsError(s"Expected a `spotify` entry, got [$objMap]"))
    case other => JsError(s"Expected a json object, got [$other]")
  }

  val authErrorWrites: Writes[AuthError] = (o: AuthError) =>
    JsObject(Map("error" -> JsString(o.error), "error_description" -> JsString(o.message)))

  val authErrorReads: Reads[AuthError] =
    (JsPath \ "error")
      .read[String]
      .and((JsPath \ "error_description").read[String])(AuthError.apply _)

  val authErrorFormat: Format[AuthError] = Format(authErrorReads, authErrorWrites)
  val apiErrorFormat: Format[ApiError] = Format(apiErrorReads, apiErrorWrites)

  val spotifyErrorReads: Reads[SpotifyError] = {
    val r1 = apiErrorReads.widen[SpotifyError]
    val r2 = authErrorReads.widen[SpotifyError]
    r1.orElse(r2)
  }

  val spotifyErrorWrites: Writes[SpotifyError] = {
    case apiError: ApiError => apiErrorWrites.writes(apiError)
    case authError: AuthError => authErrorWrites.writes(authError)
  }

  val spotifyErrorFormat: Format[SpotifyError] = Format(spotifyErrorReads, spotifyErrorWrites)
}
