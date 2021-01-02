package io.bartholomews.spotify4s.circe

import java.time.Month

import io.bartholomews.iso_country.CountryCodeAlpha2
import io.bartholomews.spotify4s.core.entities.SubscriptionLevel.{Free, Premium, Unknown}
import io.bartholomews.spotify4s.core.entities.{
  EAN,
  ExternalIds,
  ExternalResourceUrl,
  FullTrack,
  ISRC,
  LinkedTrack,
  Page,
  ReleaseDate,
  Restrictions,
  SimpleAlbum,
  SimpleArtist,
  SpotifyId,
  SpotifyResourceUrl,
  SpotifyUri,
  SubscriptionLevel,
  UPC
}
import io.circe.Decoder.{decodeOption, Result}
import io.circe.generic.extras.semiauto.deriveConfiguredEncoder
import io.circe.{Codec, Decoder, DecodingFailure, Encoder, HCursor, Json, JsonObject}
import sttp.model.Uri

import scala.util.matching.Regex

object CirceExternalIds {
  // https://stackoverflow.com/a/57708249
  val decoder: Decoder[ExternalIds] =
    Decoder
      .instance { c =>
        c.value.asObject match {
          case Some(obj) if obj.size == 1 =>
            obj.toIterable.head match {
              case ("isrc", value) => value.as[String].map(ISRC.apply)
              case ("ean", value) => value.as[String].map(EAN.apply)
              case ("upc", value) => value.as[String].map(UPC.apply)
              case (unknown, _) =>
                Left(DecodingFailure(s"ExternalId; unexpected resource standard code: [$unknown]", c.history))
            }

          case _ =>
            Left(DecodingFailure("ExternalId; expected singleton object", c.history))
        }
      }

  val encoder: Encoder[ExternalIds] = {
    case ISRC(value) => Json.obj(("isrc", Json.fromString(value)))
    case EAN(value) => Json.obj(("ean", Json.fromString(value)))
    case UPC(value) => Json.obj(("upc", Json.fromString(value)))
  }
}

object CirceExternalResourceUrl {
  private def decodeExternalResourceUrlJsonObject(json: Json, c: HCursor): Result[ExternalResourceUrl] = {
    json.asObject match {
      case Some(obj) if obj.size == 1 =>
        obj.toIterable.head match {
          case ("spotify", value) => value.as[Uri].map(SpotifyResourceUrl.apply)
          case (unknown, _) =>
            Left(DecodingFailure(s"SpotifyResourceUrl; unexpected resource url type: [$unknown]", c.history))
        }

      case _ =>
        Left(DecodingFailure("ExternalResourceUrl; expected singleton object", c.history))
    }
  }

  // https://stackoverflow.com/a/57708249
  val decoder: Decoder[ExternalResourceUrl] =
    Decoder.instance(c => decodeExternalResourceUrlJsonObject(c.value, c))

  val encoder: Encoder[ExternalResourceUrl] = {
    case s: SpotifyResourceUrl => Json.obj(("spotify", Json.fromString(s.value)))
  }

  val codec: Codec[ExternalResourceUrl] = Codec.from(decoder, encoder)
}

object CirceFullTrack {
  val decoder: Decoder[FullTrack] = (c: HCursor) =>
    for {
      album <- c.downField("album").as[SimpleAlbum](CirceSimpleAlbum.decoder)
      artists <- c.downField("artists").as[List[SimpleArtist]]
      availableMarkets <- c.downField("available_markets").as[Option[List[CountryCodeAlpha2]]]
      discNumber <- c.downField("disc_number").as[Int]
      durationMs <- c.downField("duration_ms").as[Int]
      explicit <- c.downField("explicit").as[Boolean]
      externalIds <- Right(c.downField("external_ids").as[ExternalIds].toOption)
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

object CircePage {
  def encoder[A](implicit encode: Encoder[A]): Encoder[Page[A]] = deriveConfiguredEncoder
  def decoder[A](implicit decode: Decoder[A]): Decoder[Page[A]] =
    (c: HCursor) =>
      for {
        href <- c.downField("href").as[Uri]
        items <- c.downField("items").as[Option[List[A]]]
        limit <- c.downField("limit").as[Option[Int]]
        next <- c.downField("next").as[Option[String]]
        offset <- c.downField("offset").as[Option[Int]]
        previous <- c.downField("previous").as[Option[String]]
        total <- c.downField("total").as[Int]
      } yield Page(href, items.getOrElse(List.empty), limit, next, offset, previous, total)
}

object CirceReleaseDate {
  private sealed trait ReleaseDatePrecision {
    def regex: Regex
  }

  type ReleaseDateTuple3 = (Int, Option[Month], Option[Int])

  import cats.implicits._

  val decoder: Decoder[ReleaseDate] = {
    Decoder
      .instance(cursor => {
        def decodeReleaseDate(pf: PartialFunction[Option[String], ReleaseDate]): Decoder.Result[ReleaseDate] =
          Either
            .catchNonFatal(pf(cursor.value.asString))
            .leftMap(_ => DecodingFailure(s"release_date; unexpected value: ${cursor.value}", cursor.history))

        cursor.up
          .downField("release_date_precision")
          .as[String]
          .flatMap {
            case "year" =>
              decodeReleaseDate({
                case Some(y) => ReleaseDate(y.toInt, month = None, dayOfMonth = None)
              })

            case "month" =>
              decodeReleaseDate({
                case Some(s"$y-$mm") => ReleaseDate(y.toInt, month = Some(Month.of(mm.toInt)), dayOfMonth = None)
              })

            case "day" =>
              decodeReleaseDate({
                case Some(s"$y-$mm-$dd") =>
                  ReleaseDate(y.toInt, month = Some(Month.of(mm.toInt)), dayOfMonth = Some(dd.toInt))
              })

            case unknown =>
              DecodingFailure(s"release_date_precision; unexpected precision: [$unknown]", cursor.history)
                .asLeft[ReleaseDate]
          }
      })
  }

  val encoder: Encoder[ReleaseDate] = (entity: ReleaseDate) => {
    val month = entity.month.map(mm => s"-${mm.getValue}").getOrElse("")
    val day = entity.dayOfMonth.map(d => s"-$d").getOrElse("")
    Json.fromString(s"${entity.year}$month$day")
  }

  val codec: Codec[ReleaseDate] = Codec.from(decoder, encoder)
}

case object CirceSubscriptionLevel {
  val decoder: Decoder[SubscriptionLevel] = (c: HCursor) =>
    c.value.asString match {
      case Some("premium") => Right(SubscriptionLevel.Premium)
      case Some("free") | Some("open") => Right(SubscriptionLevel.Free)
      case unknown => Right(SubscriptionLevel.Unknown(unknown))
    }

  val encoder: Encoder[SubscriptionLevel] = Encoder.instance {
    case _ @Premium => Json.fromString("premium")
    case _ @Free => Json.fromString("free")
    case _ @Unknown(value) => value.fold(Json.Null)(Json.fromString)
  }

  val codec: Codec[SubscriptionLevel] = Codec.from(decoder, encoder)
}
