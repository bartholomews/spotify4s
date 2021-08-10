package io.bartholomews.spotify4s.playJson

import io.bartholomews.iso.CountryCodeAlpha2
import io.bartholomews.spotify4s.core.entities._
import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json.{Format, JsPath, Json, Reads}
import sttp.model.Uri

object SimpleTrackPlayJson {
  import codecs._
  val reads: Reads[SimpleTrack] = (JsPath \ "artists")
    .read[List[SimpleArtist]](Reads.list(SimpleArtistPlayJson.reads))
    .and((JsPath \ "available_markets").read[List[CountryCodeAlpha2]].orElse(Reads.pure(List.empty)))
    .and((JsPath \ "disc_number").read[Int])
    .and((JsPath \ "duration_ms").read[Int])
    .and((JsPath \ "explicit").read[Boolean])
    .and((JsPath \ "external_urls").readNullable[ExternalResourceUrl])
    .and((JsPath \ "href").readNullable[Uri])
    .and((JsPath \ "id").readNullable[SpotifyId])
    .and((JsPath \ "is_playable").readNullable[Boolean])
    .and((JsPath \ "linked_from").readNullable[LinkedTrack])
    .and((JsPath \ "restrictions").readNullable[Restrictions])
    .and((JsPath \ "name").read[String])
    .and((JsPath \ "preview_url").readNullable[Uri])
    .and((JsPath \ "track_number").read[Int])
    .and((JsPath \ "uri").read[SpotifyUri])
    .and((JsPath \ "is_local").read[Boolean])(SimpleTrack.apply _)

  val format: Format[SimpleTrack] = Format[SimpleTrack](reads, Json.writes[SimpleTrack])
}
