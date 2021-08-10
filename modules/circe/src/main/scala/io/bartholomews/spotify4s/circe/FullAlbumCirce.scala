package io.bartholomews.spotify4s.circe

import io.bartholomews.iso.CountryCodeAlpha2
import io.bartholomews.spotify4s.core.entities.FullAlbum
import io.circe.generic.extras.semiauto.deriveConfiguredDecoder
import io.circe.syntax.EncoderOps
import io.circe.{Codec, Decoder, Encoder, Json}

object FullAlbumCirce {
  import codecs._

  val decoder: Decoder[FullAlbum] = {
    implicit val availableMarketsCodec: Decoder[List[CountryCodeAlpha2]] = optionalCountryCodeList
    deriveConfiguredDecoder
  }

  val encoder: Encoder[FullAlbum] = (a: FullAlbum) =>
    Json.obj(
      ("album_type", a.albumType.asJson),
      ("artists", a.artists.asJson),
      ("available_markets", a.availableMarkets.asJson),
      ("copyrights", a.copyrights.asJson),
      ("external_ids", a.externalIds.asJson),
      ("external_urls", a.externalUrls.asJson),
      ("genres", a.genres.asJson),
      ("href", a.href.asJson),
      ("id", a.id.asJson),
      ("images", a.images.asJson),
      ("label", a.label.asJson),
      ("name", a.name.asJson),
      ("popularity", a.popularity.asJson),
      ("release_date", Json.fromString(a.releaseDate.toString)),
      (
        "release_date_precision",
        Json.fromString(
          if (a.releaseDate.dayOfMonth.isDefined) "day"
          else if (a.releaseDate.month.isDefined) "month"
          else "year"
        )
      ),
      ("restrictions", a.restrictions.asJson),
      ("tracks", a.tracks.asJson),
      ("uri", a.uri.asJson)
    )

  val codec: Codec[FullAlbum] = Codec.from(decoder, encoder)
}
