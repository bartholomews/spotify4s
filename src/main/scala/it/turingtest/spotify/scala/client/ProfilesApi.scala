package it.turingtest.spotify.scala.client

import javax.inject.Inject

import it.turingtest.spotify.scala.client.entities.UserPrivate
import it.turingtest.spotify.scala.client.logging.AccessLogging
import play.api.libs.ws.WSClient

import scala.concurrent.Future

/**
  * @see https://developer.spotify.com/web-api/user-profile-endpoints/
  */
class ProfilesApi @Inject()(configuration: play.api.Configuration, ws: WSClient,
                            api: BaseApi) extends AccessLogging {

  private final val ME = s"${api.BASE_URL}/me"

  def me: Future[UserPrivate] = api.getWithOAuth[UserPrivate](ME)

}
