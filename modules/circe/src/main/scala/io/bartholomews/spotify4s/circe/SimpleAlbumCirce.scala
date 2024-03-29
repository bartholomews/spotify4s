package io.bartholomews.spotify4s.circe

import io.bartholomews.iso.CountryCodeAlpha2
import io.bartholomews.spotify4s.core.entities._
import io.circe._
import io.circe.generic.extras.semiauto.deriveConfiguredDecoder
import io.circe.syntax.EncoderOps

private[spotify4s] object SimpleAlbumCirce {
  import codecs._
  import io.bartholomews.fsclient.circe.codecs.sttpUriCodec
  val decoder: Decoder[SimpleAlbum] = {
    implicit val availableMarketsCodec: Decoder[List[CountryCodeAlpha2]] = optionalCountryCodeList
    deriveConfiguredDecoder
  }

  val encoder: Encoder[SimpleAlbum] = (a: SimpleAlbum) =>
    Json.obj(
      ("album_group", a.albumGroup.asJson),
      ("album_type", a.albumType.asJson),
      ("artists", a.artists.asJson),
      ("available_markets", a.availableMarkets.asJson),
      ("external_urls", a.externalUrls.asJson),
      ("href", a.href.asJson),
      ("id", a.id.asJson),
      ("images", a.images.asJson),
      ("name", a.name.asJson),
      ("release_date", a.releaseDate.map(_.toString).asJson),
      (
        "release_date_precision",
        a.releaseDate
          .map(rd => {
            if (rd.dayOfMonth.isDefined) "day"
            else if (rd.month.isDefined) "month"
            else "year"
          })
          .asJson
      ),
      ("restrictions", a.restrictions.asJson),
      ("uri", a.uri.asJson)
    )

  val codec: Codec[SimpleAlbum] = Codec.from(decoder, encoder)
}
