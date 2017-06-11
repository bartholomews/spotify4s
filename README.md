[![Build Status](https://travis-ci.org/bartholomews/spotify-scala-client.svg?branch=master)](https://travis-ci.org/bartholomews/spotify-scala-client)
[![codecov](https://codecov.io/gh/bartholomews/spotify-scala-client/branch/master/graph/badge.svg)](https://codecov.io/gh/bartholomews/spotify-scala-client)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](https://github.com/bartholomews/spotify-scala-client/blob/master/LICENSE)

# spotify-scala-client
*Asynchronous Spotify Web API Scala Client/Wrapper*.
 
#### Endpoints Task list
 
- [ ] Albums
    - [ ] Get an Album
    - [ ] Get Several Albums
    - [ ] Get an Album's Tracks
- [ ] Artist
    - [ ] Get an Artist
    - [ ] Get Several Artists
    - [ ] Get an Artist's Albums
    - [ ] Get an Artist's Top Tracks
    - [ ] Get an Artist's Related Artists
- [ ] Browse endpoint
    - [x] Get a List of Featured Playlists
    - [x] Get a List of New Releases
    - [ ] Get a List of Categories
    - [ ] Get a Category
    - [ ] Get a Category's Playlists
    - [ ] Get Recommendations Based on Seeds
- [ ] Follow
    - [ ] Get User's Followed Artists
    - [ ] Follow Artists or Users
    - [ ] Unfollow Artists or Users
    - [ ] Check if Current User Follows Artists or Users
    - [ ] Follow a Playlist
    - [ ] Unfollow a Playlist
    - [ ] Check if Users Follow a Playlist

**(MORE TODO)**
 
#### Usage

#### - Setup in *application.conf*

In your `application.conf` add your application client id, secret and redirect uri.  
If you don't have them yet, get these [here](https://developer.spotify.com/my-applications/#!/).
```
CLIENT_ID = "my-client-id"  
CLIENT_SECRET = "my-client-secret"  
REDIRECT_URI = "http://localhost:9000/my-callback-endpoint"
```
(remember to ignore the *conf* file from version control if your application is public)

#### - Inject the endpoints that you need

In your Controller (e.g. with *Play Framework*):

```
import play.api.mvc._  

import com.google.inject.Inject
import it.turingtest.spotify.scala.client.entities._
import it.turingtest.spotify.scala.client.{BaseApi, AuthApi, PlaylistsApi, ProfilesApi, TracksApi}
 
import scala.concurrent.Future  
import scala.concurrent.ExecutionContext.Implicits.global

 
class MyController @Inject() (api: BaseApi,
                              auth: AuthApi,
                              playlistsApi: PlaylistsApi,
                              profilesApi: ProfilesApi,
                              tracksApi: TracksApi) extends Controller {

   def hello = {
    val user: Future[UserPrivate] = profilesApi.me 
    user map { me => Ok(views.html.myview(s"Hello, ${me.id}")) }
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

For the latter, you can call `AuthApi`'s `authoriseURL` which returns the Spotify URL
where your user can grant permissions. For instance in your Controller:
```
  def auth = Action {
    Redirect(authApi.authoriseURL(state = Some("state"), scopes = List(PLAYLIST_READ_PRIVATE), showDialog = true))
  }
```

The page will be redirected to `redirect url` (which you have set
for your app as first step).  
The request will return the authorisation code
which the wrapper needs in order to do *oAuth* calls.  
Give this code to `BaseApi` with a setter (**TODO**)  
otherwise you can call `callback(code)` and perform an `Action` straight away.
For instance, here callback will set the authorisation code and call the `hello()` action:

```
  def callback: Action[AnyContent] = Action.async {
    request =>
      request.getQueryString("code") match {
       // If the user rejected the permissions, or some other error occurred,
       // request should contain an "error" querystring instead of a "code".
        case Some(code) => api.callback(code) { _ => hello() }
        case None => request.getQueryString("error") match {
          case Some("access_denied") => Future(BadRequest("You need to authorize permissions in order to use the App."))
          case Some(error) => Future(BadRequest(error))
          case _ => Future(BadRequest("Something went wrong."))
        }
      }
  }

```

If you don't set the authorisation code you will get an error such as "Authorisation code not provided" 
if you make an *oAuth* request. Once this is set, the client will automatically take care 
of refreshing future tokens.  

Some endpoints don't need *oAuth*, so you can still call them if you don't set it.
Just read the docs of the endpoint you need to make sure it doesn't require authorisation.
For non-*oAuth* requests, the client will use the *client credentials* flow in order to
benefit of higher rates.

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