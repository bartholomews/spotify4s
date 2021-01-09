package io.bartholomews.spotify4s.circe

import io.bartholomews.spotify4s.core.entities.ReleaseDate
import io.circe.{Decoder, DecodingFailure}

private[spotify4s] object ReleaseDateCirce {
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
                case Some(s"$y-$mm") =>
                  ReleaseDate(y.toInt, month = Some(java.time.Month.of(mm.toInt)), dayOfMonth = None)
              })

            case "day" =>
              decodeReleaseDate({
                case Some(s"$y-$mm-$dd") =>
                  ReleaseDate(y.toInt, month = Some(java.time.Month.of(mm.toInt)), dayOfMonth = Some(dd.toInt))
              })

            case unknown =>
              DecodingFailure(s"release_date_precision; unexpected precision: [$unknown]", cursor.history)
                .asLeft[ReleaseDate]
          }
      })
  }

  //  val encoder: Encoder[ReleaseDate] = (entity: ReleaseDate) => {
  //    val month = entity.month.map(mm => s"-${mm.getValue}").getOrElse("")
  //    val day = entity.dayOfMonth.map(d => s"-$d").getOrElse("")
  //    Json.fromString(s"${entity.year}$month$day")
  //  }
  //
  //  val codec: Codec[ReleaseDate] = Codec.from(decoder, encoder)
}
