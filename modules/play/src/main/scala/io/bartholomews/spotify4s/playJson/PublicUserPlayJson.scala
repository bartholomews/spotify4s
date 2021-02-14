package io.bartholomews.spotify4s.playJson

import io.bartholomews.spotify4s.core.entities.{
  ExternalResourceUrl,
  Followers,
  PublicUser,
  SpotifyImage,
  SpotifyUri,
  SpotifyUserId
}
import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json.{Format, JsPath, Json, Reads, Writes}
import sttp.model.Uri

private[spotify4s] object PublicUserPlayJson {
  import io.bartholomews.spotify4s.playJson.codecs._
  val reads: Reads[PublicUser] =
    (JsPath \ "display_name")
      .readNullable[String]
      .and((JsPath \ "external_urls").read[ExternalResourceUrl](ExternalResourceUrlPlayJson.reads))
      .and((JsPath \ "followers").readNullable[Followers])
      .and((JsPath \ "href").read[Uri])
      .and((JsPath \ "id").read[SpotifyUserId])
      .and((JsPath \ "images").readNullable[List[SpotifyImage]].map(_.getOrElse(List.empty)))
      .and((JsPath \ "uri").read[SpotifyUri])(PublicUser.apply _)

  val writes: Writes[PublicUser] = Json.writes

  val format: Format[PublicUser] = Format(reads, writes)
}
