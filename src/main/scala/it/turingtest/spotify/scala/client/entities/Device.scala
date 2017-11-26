package it.turingtest.spotify.scala.client.entities

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Reads}

/**
  * @see https://developer.spotify.com/web-api/get-a-users-available-devices/
  *
  * @param id The device ID, if defined.
  *
  * @param is_active If this device is the currently active device.
  *
  * @param is_restricted Whether controlling this device is restricted.
  *                      At present if this is "true" then no Web API commands
  *                      will be accepted by this device.
  *
  * @param name The name of the device.
  * @param deviceType Device type, such as "Computer", "Smartphone" or "Speaker".
  * @param volume_percent The current volume in percent, if defined.
  */
case class Device(id: Option[String],
                  is_active: Boolean,
                  is_restricted: Boolean,
                  name: String,
                  deviceType: String,
                  volume_percent: Option[Int])

object Device {
  implicit val deviceReads: Reads[Device] = (
    (JsPath \ "id").readNullable[String] and
      (JsPath \ "is_active").read[Boolean] and
      (JsPath \ "is_restricted").read[Boolean] and
      (JsPath \ "name").read[String] and
      (JsPath \ "type").read[String] and
      (JsPath \ "volume_percent").readNullable[Int]
    )(Device.apply _)
}
