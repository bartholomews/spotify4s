package it.turingtest.spotify.scala.client

import javax.inject.Inject

import it.turingtest.spotify.scala.client.entities.{User, UserPrivate}
import it.turingtest.spotify.scala.client.logging.AccessLogging
import play.api.libs.ws.WSClient

import scala.concurrent.Future

/**
  * @see https://developer.spotify.com/web-api/user-profile-endpoints/
  */
class ProfilesApi @Inject()(ws: WSClient, api: BaseApi) extends AccessLogging {

  private final val ME = s"${api.BASE_URL}/me"

  /**
    * @see https://developer.spotify.com/web-api/get-current-users-profile/
    *
    * [OAUTH]
    * Reading the user's email address requires the user-read-email scope;
    * reading country and product subscription level requires the user-read-private scope.
    * Reading the user's birthdate requires the user-read-birthdate scope.
    *
    * @return Get detailed profile information about the current user
    *         (including the current user’s username).
    */
  def me: Future[UserPrivate] = api.getWithOAuth[UserPrivate](ME)

  /**
    * @see https://developer.spotify.com/web-api/get-users-profile/
    *
    * @param user_id The user's Spotify user ID
    *
    * @return On success, the HTTP status code in the response header is 200 OK
    *         and the response body contains a user object in JSON format.
    *         On error, the header status code is an error code
    *         and the response body contains an error object (@see `ErrorDetails`)
    *         If a user with that user_id doesn’t exist, the status code is 404 NOT FOUND.
    */
  def user(user_id: String): Future[User] = api.get[User](s"${api.BASE_URL}/users/$user_id")

}
