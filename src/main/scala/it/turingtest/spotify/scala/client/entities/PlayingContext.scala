package it.turingtest.spotify.scala.client.entities

import scala.util.Try

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, JsonValidationError, Reads}


case class PlayingContext(device: Device,
                          repeat_state: RepeatState,
                          is_shuffle_on: Boolean,
                          context: Option[Context],
                          timestamp: Long,
                          progress_ms: Option[String], // TODO Int
                          is_playing: Boolean,
                          item: Option[Track])

object PlayingContext {
  implicit val playingContextReads: Reads[PlayingContext] = (
    (JsPath \ "device").read[Device] and
      // @see https://stackoverflow.com/a/29948007
      (JsPath \ "repeat_state").read[String].collect(JsonValidationError(""))(
        Function.unlift(t => Try(RepeatState.valueOf(t)).toOption)) and
      (JsPath \ "shuffle_state").read[Boolean] and
      (JsPath \ "context").readNullable[Context] and
      (JsPath \ "timestamp").read[Long] and
      (JsPath \ "progress_ms").readNullable[String] and
      (JsPath \ "is_playing").read[Boolean] and
      (JsPath \ "item").readNullable[Track]
    )(PlayingContext.apply _)
}

sealed trait RepeatState

object RepeatState {
  def valueOf(value: String): RepeatState = value.toLowerCase match {
    case "off" => REPEAT_OFF
    case "track" => REPEAT_TRACK
    case "context" => REPEAT_CONTEXT
    case other => throw new IllegalArgumentException(s"$other: invalid context type.")
  }
}

case object REPEAT_OFF extends RepeatState
case object REPEAT_TRACK extends RepeatState
case object REPEAT_CONTEXT extends RepeatState