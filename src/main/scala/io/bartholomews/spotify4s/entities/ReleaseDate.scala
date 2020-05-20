package io.bartholomews.spotify4s.entities

import java.time.{LocalDate, Month}

import cats.implicits._
import io.circe._

import scala.util.matching.Regex

case class ReleaseDate(year: Int, month: Option[Month], dayOfMonth: Option[Int]) {
  lazy val localDate: Option[LocalDate] = for {
    mm <- month
    dd <- dayOfMonth
  } yield LocalDate.of(year, mm, dd)
}

object ReleaseDate {
  private sealed trait ReleaseDatePrecision {
    def regex: Regex
  }

  type ReleaseDateTuple3 = (Int, Option[Month], Option[Int])

  implicit val decoder: Decoder[ReleaseDate] = {
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

  implicit val encoder: Encoder[ReleaseDate] = (entity: ReleaseDate) => {
    val month = entity.month.map(mm => s"-${mm.getValue}").getOrElse("")
    val day = entity.dayOfMonth.map(d => s"-$d").getOrElse("")
    Json.fromString(s"${entity.year}$month$day")
  }

  implicit val codec: Codec[ReleaseDate] = Codec.from(decoder, encoder)
}
