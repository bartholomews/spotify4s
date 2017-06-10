[![Build Status](https://travis-ci.org/bartholomews/spotify-scala-client.svg?branch=master)](https://travis-ci.org/bartholomews/spotify-scala-client)
[![codecov](https://codecov.io/gh/bartholomews/spotify-scala-client/branch/master/graph/badge.svg)](https://codecov.io/gh/bartholomews/spotify-scala-client)

# spotify-scala-client
*Asynchronous Spotify Web API Scala Client/Wrapper*.
 
**NOTE -** *This project is a work in progress at an early stage 
and will be updated soon, in the meantime if you have any question/suggestion please get in touch, thanks.*
 
#### Usage

#### - Setup in***Application.conf***

In your `application.conf` add your application client id, secret and redirect uri.  
If you don't have them yet, get these[here](https://developer.spotify.com/my-applications/#!/).
```
CLIENT_ID = "my-client-id"  
CLIENT_SECRET = "my-client-secret"  
REDIRECT_URI = "http://localhost:9000/my-callback-endpoint"
```
(remember to ignore the *conf* file from version control if your application is public)

#### - Inject the wrapper's endpoints that you need

In your Controller:

```
import play.api.mvc._  

import com.google.inject.Inject
import it.turingtest.spotify.scala.client.entities._
import it.turingtest.spotify.scala.client.{it.turingtest.spotify.scala.client.BaseApi, it.turingtest.spotify.scala.client.PlaylistsApi, it.turingtest.spotify.scala.client.ProfilesApi, it.turingtest.spotify.scala.client.TracksApi}
 
import scala.concurrent.Future  
import scala.concurrent.ExecutionContext.Implicits.global

 
class MyController @Inject() (api: it.turingtest.spotify.scala.client.BaseApi,
                              playlistsApi: it.turingtest.spotify.scala.client.PlaylistsApi,
                              profilesApi: it.turingtest.spotify.scala.client.ProfilesApi,
                              tracksApi: it.turingtest.spotify.scala.client.TracksApi) extends Controller {

 
  // profilesApi.me returns a Future[UserPrivate]
  // in general, all the entities are mapping 
  // the official Spotify api, so most of the docs are similar.
  def hello = profilesApi.me map {
        me => Ok(views.html.myview(s"Hello, ${me.id}"))
  }
 
  
}

```

#### - Logging

In your *conf/logback.xml*:

```
  <logger name="spotify-scala-client" level="DEBUG"/>

```

**DEBUG** will log all the json responses.  
**WARN** is currently doing nothing.  
**INFO** is currently doing nothing.

#### 2. ***Authorisation***


This wrapper will automatically use either the client credentials flow (if the request doesn't 
need user's permissions) or Authorisation Code Grant (which requires
 a code that is returned to your "redirect-uri" after the user granted permissions).   

For the latter, you can call `it.turingtest.spotify.scala.client.AuthApi`'s `authoriseURL` which returns the Spotify URL
where your user can grant permissions. For instance in your Controller:
```
  /**
    * Redirect a user to authenticate with Spotify and grant permissions to the application.
    * @return a Redirect Action (play.api.mvc.Action type is a wrapper around the type `Request[A] => Result`,
    */
  def auth = Action {
    // authApi is an injected instance of it.turingtest.spotify.scala.client.it.turingtest.spotify.scala.client.AuthApi
    Redirect(authApi.authoriseURL(state = Some("state"), scopes = List(PLAYLIST_READ_PRIVATE), showDialog = true))
  }
```

And in your view:

```
<form action = "@routes.MyController.auth" method = "get">
<button class="btn btn-success btn-lg" id="login-button">Login with Spotify</button>
</form>
```

The page will be redirected to the redirect url which you have set
for your app as first step. The request will return the authorisation code
which the wrapper needs in order to do oAuth calls. You can feed this code calling  a setter, or 
if you want to return an Action and render a view, you can call it.turingtest.spotify.scala.client.BaseApi's callback
method. For instance:

```
  def callback: Action[AnyContent] = Action.async {
    request =>
      request.getQueryString("code") match {
       // callback uses a function which takes a Token (unused in this case, 
       // so left with underscore) and return a Future[Result], in this case hello().
       // If the user rejected the permissions, or some other error occurred,
       // the request should contain an "error" querystring instead of a "code".
        case Some(code) => api.callback(code) { _ => hello() }
        case None => request.getQueryString("error") match {
          case Some("access_denied") => Future(BadRequest("You need to authorize permissions in order to use the App."))
          case Some(error) => Future(BadRequest(error))
          case _ => Future(BadRequest("Something went wrong."))
        }
      }
  }

```

If you don't feed the wrapper with an authorisation code, either via the `callback` or with a setter (TODO), you will get an error such as "Authorisation code not provided" if 
you make an Oauth request. After you have given the auth code, this client will
automatically retrieve and refresh tokens, you don't have to worry about them.
Still, you can access the Token which is used on each oAuth request if you call the it.turingtest.spotify.scala.client.BaseApi
methods. For instance, callback above takes a Token and returns a 
Future[Result]. Other endpoints such as it.turingtest.spotify.scala.client.ProfilesApi and it.turingtest.spotify.scala.client.TracksApi use it.turingtest.spotify.scala.client.BaseApi underneath to make requests, so you can stay at a  higher-level and keep it simple.

 All the endpoints should be documented so if you decide not to use the Auth Credential Flow, make
 sure that you check the wrapper's function you use do not require it.


Read more about [Spotify Web API Authorisation](https://developer.spotify.com/web-api/authorization-guide/).

The parameters of `authoriseURL` are:
+ **state**: an optional String of your choice, default is empty. As the official Spotify documentation says, 
*The state can be useful for correlating requests and responses. 
Because your redirect_uri can be guessed, using a state value can increase your assurance 
that an incoming connection is the result of an authentication request. 
If you generate a random string or encode the hash of some client state (e.g., a cookie) 
in this state variable, you can validate the response to additionally ensure that the 
request and response originated in the same browser. This provides protection against 
attacks such as cross-site request forgery.*

+ **scopes**: a List of `Scope` case objects, such as PLAYLIST-MODIFY-PUBLIC or USER-READ-EMAIL.
  These are basically the permissions that you need from the users of your app. Read more about scopes [here](https://developer.spotify.com/web-api/using-scopes/)
    
+ **show_dialog** Whether or not to force the user to approve the app again if they’ve already done so.
It defaults to true.

You can call `authoriseURL` without parameters 
if you want; but it will be a request with empty state, no scopes and show_dialog set to true.
(If no scope is specified, access is permitted only to publicly available information, and you
might get a response error... [TODO])

***