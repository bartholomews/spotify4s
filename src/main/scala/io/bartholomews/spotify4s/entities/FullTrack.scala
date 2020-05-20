package io.bartholomews.spotify4s.entities

import io.bartholomews.fsclient.codecs.FsJsonResponsePipe
import io.bartholomews.iso_country.CountryCodeAlpha2
import io.circe.generic.extras.ConfiguredJsonCodec
import io.circe.{Decoder, HCursor}
import org.http4s.Uri

// https://developer.spotify.com/documentation/web-api/reference/object-model/#track-object-full
@ConfiguredJsonCodec
case class FullTrack(
  album: SimpleAlbum,
  artists: List[SimpleArtist],
  availableMarkets: List[CountryCodeAlpha2],
  discNumber: Int,
  durationMs: Int,
  explicit: Boolean,
  externalIds: ExternalIds,
  externalUrls: ExternalResourceUrl,
  href: String,
  id: SpotifyUserId,
  isPlayable: Option[Boolean],
  linkedFrom: Option[LinkedTrack],
  restrictions: Option[Restrictions],
  name: String,
  popularity: Int,
  previewUrl: Option[Uri],
  trackNumber: Int,
  uri: SpotifyUri,
  isLocal: Boolean
)

object FullTrack extends FsJsonResponsePipe[FullTrack] {
  implicit val decoder: Decoder[FullTrack] = (c: HCursor) =>
    for {
      album <- c.downField("album").as[SimpleAlbum](SimpleAlbum.decoder)
      artists <- c.downField("artists").as[List[SimpleArtist]]
      availableMarkets <- c.downField("available_markets").as[Option[List[CountryCodeAlpha2]]]
      discNumber <- c.downField("disc_number").as[Int]
      durationMs <- c.downField("duration_ms").as[Int]
      explicit <- c.downField("explicit").as[Boolean]
      externalIds <- c.downField("external_ids").as[ExternalIds]
      externalUrls <- c.downField("external_urls").as[ExternalResourceUrl]
      href <- c.downField("href").as[String]
      id <- c.downField("id").as[SpotifyUserId]
      isPlayable <- c.downField("is_playable").as[Option[Boolean]]
      linkedFrom <- c.downField("linked_from").as[Option[LinkedTrack]]
      restrictions <- c.downField("restrictions").as[Option[Restrictions]]
      name <- c.downField("name").as[String]
      popularity <- c.downField("popularity").as[Int]
      previewUrl <- c.downField("preview_url").as[Option[Uri]]
      trackNumber <- c.downField("track_number").as[Int]
      uri <- c.downField("uri").as[SpotifyUri]
      isLocal <- c.downField("is_local").as[Boolean]
    } yield
      FullTrack(
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
