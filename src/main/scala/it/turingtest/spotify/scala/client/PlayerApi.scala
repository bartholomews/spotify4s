package it.turingtest.spotify.scala.client

import javax.inject.Inject

import com.vitorsvieira.iso.ISOCountry.ISOCountry
import it.turingtest.spotify.scala.client.entities._
import it.turingtest.spotify.scala.client.logging.AccessLogging
import it.turingtest.spotify.scala.client.utils.ConversionUtils
import play.api.libs.ws.WSClient

import scala.concurrent.Future

/**
  * @see https://developer.spotify.com/web-api/web-api-connect-endpoint-reference/
  *      https://developer.spotify.com/web-api/working-with-connect/
  */
class PlayerApi @Inject()(ws: WSClient, api: BaseApi) extends AccessLogging {

  private final val PLAYER = s"${api.BASE_URL}/me/player"

  /**
    * Get a User's Available Devices
    *
    * @see https://developer.spotify.com/web-api/get-a-users-available-devices/
    *
    * @return a Seq of Device objects.
    */
  def devices: Future[Seq[Device]] = api.getWithOAuth[Seq[Device]]("devices", s"$PLAYER/devices")

  /**
    * Get Information About The User’s Current Playback
    *
    * @see https://developer.spotify.com/web-api/get-information-about-the-users-current-playback/
    *
    * @param market Optional. An ISO 3166-1 alpha-2 country code.
    *               Provide this parameter if you want to apply Track Relinking.
    *
    * @return the last known device context, if available.
    */
  def lastPlayback(market: Option[ISOCountry] = None): Future[Option[PlayingContext]] = {
    val query = ConversionUtils.seq(("market", market))
    api.getOptWithOAuth[PlayingContext](PLAYER, query:_*)
  }



}
