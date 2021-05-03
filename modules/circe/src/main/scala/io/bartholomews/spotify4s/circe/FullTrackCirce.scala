package io.bartholomews.spotify4s.circe

import io.bartholomews.iso.CountryCodeAlpha2
import io.bartholomews.spotify4s.core.entities.{ExternalIds, ExternalResourceUrl, FullTrack, LinkedTrack, Restrictions, SimpleAlbum, SimpleArtist, SpotifyId, SpotifyUri}
import io.circe.{Decoder, HCursor}
import io.circe.Decoder.{decodeOption, decodeList}
import sttp.model.Uri

private[spotify4s] object FullTrackCirce {
  import codecs._
  import io.bartholomews.fsclient.circe.codecs.sttpUriCodec
  val decoder: Decoder[FullTrack] = (c: HCursor) =>
    for {
      album <- c.downField("album").as[SimpleAlbum](SimpleAlbumCirce.decoder)
      artists <- c.downField("artists").as[List[SimpleArtist]](decodeList(SimpleArtistCirce.decoder))
      availableMarkets <- c.downField("available_markets").as[Option[List[CountryCodeAlpha2]]](decodeOption(decodeList(CountryCodeAlpha2Circe.decoder)))
      discNumber <- c.downField("disc_number").as[Int]
      durationMs <- c.downField("duration_ms").as[Int]
      explicit <- c.downField("explicit").as[Boolean]
      externalIds <- Right(c.downField("external_ids").as[ExternalIds](ExternalIdsCirce.decoder).toOption)
      externalUrls <- Right(c.downField("external_urls").as[ExternalResourceUrl].toOption)
      href <- c.downField("href").as[Option[Uri]]
      id <- c.downField("id").as[Option[SpotifyId]]
      isPlayable <- c.downField("is_playable").as[Option[Boolean]]
      linkedFrom <- c.downField("linked_from").as[Option[LinkedTrack]]
      restrictions <- c.downField("restrictions").as[Option[Restrictions]]
      name <- c.downField("name").as[String]
      popularity <- c.downField("popularity").as[Int]
      previewUrl <- c.downField("preview_url").as[Option[Uri]]
      trackNumber <- c.downField("track_number").as[Int]
      uri <- c.downField("uri").as[SpotifyUri]
      isLocal <- c.downField("is_local").as[Boolean]
    } yield FullTrack(
      album,
      artists,
      availableMarkets.getOrElse(List.empty),
      discNumber,
      durationMs,
      explicit,
      externalIds,
      externalUrls,
      href,
      id,
      isPlayable,
      linkedFrom,
      restrictions,
      name,
      popularity,
      previewUrl,
      trackNumber,
      uri,
      isLocal
    )
}
