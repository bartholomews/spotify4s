package io.bartholomews.spotify4s.playJson

import enumeratum.EnumEntry.Lowercase
import enumeratum.{Enum, EnumEntry, EnumFormats}
import io.bartholomews.spotify4s.core.entities.ReleaseDate
import play.api.libs.json._

import scala.util.{Failure, Success, Try}

private[spotify4s] object ReleaseDatePlayJson {
  sealed trait ReleaseDatePrecision extends EnumEntry with Lowercase {
    def decodeReleaseDatePf: PartialFunction[String, Try[ReleaseDate]]
  }

  case object ReleaseDatePrecision extends Enum[ReleaseDatePrecision] {
    case object Year extends ReleaseDatePrecision {
      override final val decodeReleaseDatePf: PartialFunction[String, Try[ReleaseDate]] = {
        case s"$y" => Try(y.toInt).map(year => ReleaseDate(year, month = None, dayOfMonth = None))
      }
    }

    case object Month extends ReleaseDatePrecision {
      override final val decodeReleaseDatePf: PartialFunction[String, Try[ReleaseDate]] = {
        case s"$y-$mm" =>
          Try(Tuple2(y.toInt, java.time.Month.of(mm.toInt))).map({
            case (year, month) => ReleaseDate(year, month = Some(month), dayOfMonth = None)
          })
      }
    }

    case object Day extends ReleaseDatePrecision {
      override final val decodeReleaseDatePf: PartialFunction[String, Try[ReleaseDate]] = {
        case s"$y-$mm-$dd" =>
          Try(Tuple3(y.toInt, java.time.Month.of(mm.toInt), dd.toInt)).map({
            case (year, month, day) => ReleaseDate(year, month = Some(month), dayOfMonth = Some(day))
          })
      }
    }

    def fromReleaseDate(releaseDate: ReleaseDate): ReleaseDatePrecision =
      if (releaseDate.dayOfMonth.nonEmpty && releaseDate.month.nonEmpty) ReleaseDatePrecision.Day
      else if (releaseDate.month.nonEmpty) ReleaseDatePrecision.Month
      else ReleaseDatePrecision.Year
    override val values: IndexedSeq[ReleaseDatePrecision] = findValues
  }

  val releaseDatePrecisionFormat: Format[ReleaseDatePrecision] =
    EnumFormats.formats(ReleaseDatePrecision)

  def decodeReleaseDate(pf: PartialFunction[String, Try[ReleaseDate]])(rd: String): Reads[ReleaseDate] =
    pf.andThen(
        rdv =>
          rdv match {
            case Failure(exception) => Reads.failed[ReleaseDate](s"Invalid release date: [${exception.getMessage}]")
            case Success(value) => Reads.pure(value)
        }
      )
      .applyOrElse[String, Reads[ReleaseDate]](rd, other => Reads.failed(s"Invalid release date: [$other]"))

  def decodeReleaseDate(
    releaseDatePrecisionReads: Reads[ReleaseDatePrecision],
    releaseDateRaw: Reads[String]
  ): Reads[ReleaseDate] =
    releaseDatePrecisionReads.flatMap(
      rdp => releaseDateRaw.flatMap(rdr => decodeReleaseDate(rdp.decodeReleaseDatePf)(rdr))
    )

  def decodeNullableReleaseDate(
    releaseDatePrecisionReads: Reads[Option[ReleaseDatePrecision]],
    releaseDateRaw: Reads[Option[String]]
  ): Reads[Option[ReleaseDate]] = {
    releaseDatePrecisionReads.flatMap {
      case Some(rdp) =>
        releaseDateRaw.flatMap(
          maybeReleaseDateStr =>
            maybeReleaseDateStr
              .map(str => decodeReleaseDate(rdp.decodeReleaseDatePf)(str).map(Option(_)))
              .getOrElse(Reads.failed[Option[ReleaseDate]]("Missing release_date value"))
        )
      case None => Reads.pure(None)
    }
  }

  val writes: Writes[ReleaseDate] = (o: ReleaseDate) =>
    JsObject(
      Map(
        "release_date_precision" -> JsString(ReleaseDatePrecision.fromReleaseDate(o).entryName),
        "release_date" -> JsString(o.toString)
      )
  )
}
