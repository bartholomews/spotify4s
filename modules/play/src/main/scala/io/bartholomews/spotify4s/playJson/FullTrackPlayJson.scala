package io.bartholomews.spotify4s.playJson

import io.bartholomews.iso_country.CountryCodeAlpha2
import io.bartholomews.spotify4s.core.entities.{
  ExternalIds,
  ExternalResourceUrl,
  FullTrack,
  LinkedTrack,
  Restrictions,
  SimpleAlbum,
  SimpleArtist,
  SpotifyId,
  SpotifyUri
}
import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json.{JsPath, Reads}
import sttp.model.Uri

private[spotify4s] object FullTrackPlayJson {
  import io.bartholomews.spotify4s.playJson.codecs._
  val reads: Reads[FullTrack] =
    (JsPath \ "album")
      .read[SimpleAlbum]
      .and((JsPath \ "artists").read[List[SimpleArtist]])
      .and((JsPath \ "available_markets").read[List[CountryCodeAlpha2]].orElse(Reads.pure(List.empty)))
      .and((JsPath \ "disc_number").read[Int])
      .and((JsPath \ "duration_ms").read[Int])
      .and((JsPath \ "explicit").read[Boolean])
      .and((JsPath \ "external_ids").read[ExternalIds].map(Option(_)).orElse(Reads.pure(None)))
      .and((JsPath \ "external_urls").read[ExternalResourceUrl].map(Option(_)).orElse(Reads.pure(None)))
      .and((JsPath \ "href").readNullable[Uri])
      .and((JsPath \ "id").readNullable[SpotifyId](spotifyIdDecoder))
      .and((JsPath \ "is_playable").readNullable[Boolean])
      .and((JsPath \ "linked_from").readNullable[LinkedTrack])
      .and((JsPath \ "restrictions").readNullable[Restrictions](restrictionsDecoder))
      .and((JsPath \ "name").read[String])
      .and((JsPath \ "popularity").read[Int])
      .and((JsPath \ "preview_url").readNullable[Uri])
      .and((JsPath \ "track_number").read[Int])
      .and((JsPath \ "uri").read[SpotifyUri](spotifyUriDecoder))
      .and((JsPath \ "is_local").read[Boolean])(FullTrack.apply _)
}
