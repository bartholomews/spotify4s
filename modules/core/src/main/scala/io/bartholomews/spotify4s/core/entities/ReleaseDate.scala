package io.bartholomews.spotify4s.core.entities

import java.time.{LocalDate, Month}

case class ReleaseDate(year: Int, month: Option[Month], dayOfMonth: Option[Int]) {
  lazy val localDate: Option[LocalDate] = for {
    mm <- month
    dd <- dayOfMonth
  } yield LocalDate.of(year, mm, dd)

  override def toString: String = {
    val mm = month.map(m => s"-$m").getOrElse("")
    val dd = dayOfMonth.map(d => s"-$d").getOrElse("")
    s"$year$mm$dd"
  }
}
