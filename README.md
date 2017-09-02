[![Build Status](https://travis-ci.org/bartholomews/spotify-scala-client.svg?branch=master)](https://travis-ci.org/bartholomews/spotify-scala-client)
[![codecov](https://codecov.io/gh/bartholomews/spotify-scala-client/branch/master/graph/badge.svg)](https://codecov.io/gh/bartholomews/spotify-scala-client)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](https://github.com/bartholomews/spotify-scala-client/blob/master/LICENSE)
[ ![Download](https://api.bintray.com/packages/bartholomews/maven/spotify-scala-client/images/download.svg) ](https://bintray.com/bartholomews/maven/spotify-scala-client/_latestVersion)

# spotify-scala-client
*Asynchronous Spotify Web API Scala Client/Wrapper*
 
### Add to your *build.sbt*

```
"it.turingtest" %% "spotify-scala-client" % "0.0.1",
resolvers += Resolver.jcenterRepo // you might have this already
```

### Endpoints Task list
 
- [ ] **Albums**
    - [ ] Get an Album
    - [ ] Get Several Albums
    - [ ] Get an Album's Tracks
- [ ] **Artist**
    - [ ] Get an Artist
    - [ ] Get Several Artists
    - [ ] Get an Artist's Albums
    - [ ] Get an Artist's Top Tracks
    - [ ] Get an Artist's Related Artists
- [x] [**Browse**](https://github.com/bartholomews/spotify-scala-client/blob/master/src/main/scala/it/turingtest/spotify/scala/client/BrowseApi.scala)
    - [x] Get a List of Featured Playlists
    - [x] Get a List of New Releases
    - [x] Get a List of Categories
    - [x] Get a Category
    - [x] Get a Category's Playlists
    - [x] Get Recommendations Based on Seeds
- [ ] **Follow**
    - [ ] Get User's Followed Artists
    - [ ] Follow Artists or Users
    - [ ] Unfollow Artists or Users
    - [ ] Check if Current User Follows Artists or Users
    - [ ] Follow a Playlist
    - [ ] Unfollow a Playlist
    - [ ] Check if Users Follow a Playlist
- [x] [**Tracks**](https://github.com/bartholomews/spotify-scala-client/blob/master/src/main/scala/it/turingtest/spotify/scala/client/TracksApi.scala)
    - [x] Get audio analysis for a track
    - [x] Get audio features for a track
    - [x] Get audio features for several tracks
    - [x] Get a track
    - [x] Get several tracks 

**(MORE TODO)**

### Usage

#### - Setup in *application.conf*

In your `application.conf` add your application client id, secret and redirect uri.  
If you don't have them yet, get these [here](https://developer.spotify.com/my-applications/#!/).
```
CLIENT_ID = "my-client-id"  
CLIENT_SECRET = "my-client-secret"  
REDIRECT_URI = "http://localhost:9000/my-callback-endpoint"
```

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
                              profilesApi: ProfilesApi,
                              tracksApi: TracksApi) extends Controller {

   def hello() = {
    val user: Future[UserPrivate] = profilesApi.me 
    user map { me => Ok(views.html.myview(s"Hello, ${me.id}")) }
  }
 
  
}

```

### **oAuth**

For requests which don't need user permissions, this client will use the *client credentials flow*.
Otherwise you need to set up the *Authorization Code Grant*: 

```
import it.turingtest.spotify.scala.client.AuthApi

def auth = Action {
    Redirect(authApi.authoriseURL(state = Some("state"), scopes = List(PLAYLIST_READ_PRIVATE), showDialog = true))
}
```

In this example, the controller action redirects the user to the Spotify Authorization page
where he needs to grant the permissions listed in the `scopes` parameter (e.g. here to read private
playlists).

The parameters of `authoriseURL` are:
+ **state**: an optional String of your choice, default is empty. You can generate a random string
or encode the hash of some client state (e.g. a cookie) and validate the response to make sure 
it is genuine and prevent attacks such as CSRF.
+ **scopes**: a List of `Scope` case objects, such as `PLAYLIST_MODIFY_PUBLIC` or `USER_READ_EMAIL`.
  These are basically the permissions that you need from the users of your app. Read more about scopes [here](https://developer.spotify.com/web-api/using-scopes/).
+ **show_dialog** Whether or not to force the user to approve the app again if they’ve already done so.
It defaults to true here.

You can call `authoriseURL` without parameters 
if you want; but it will be a request with empty state, no scopes and show_dialog set to true.
(If no scope is specified, access is permitted only to publicly available information, and you
might get a response error.

After the user has granted or rejected permissions, he is redirected to the url value defined in 
your `application.conf` under `REDIRECT_URL`. The request's should contain a 'code' querystring,
which you need to give to the wrapper in order to set up oAuth.

For instance, if you want to perform an action straight away after the user has granted
permissions:

In your `conf/application.conf`:
```
REDIRECT_URI = "http://localhost:9000/callbackEndpoint"
```

In your `conf/routes`:

```
GET /callbackEndpoint   controllers.MyController.callbackAction
```

In your controller:

```

import play.api.mvc._  

import com.google.inject.Inject
import it.turingtest.spotify.scala.client.entities._
import it.turingtest.spotify.scala.client.{AuthApi, ProfilesApi}
 
import scala.concurrent.Future  
import scala.concurrent.ExecutionContext.Implicits.global

 
class MyController @Inject() (authApi: AuthApi,
                              profilesApi: ProfilesApi) extends Controller {

   def hello() = {
    val user: Future[UserPrivate] = profilesApi.me 
    user map { me => Ok(views.html.myview(s"Hello, ${me.id}")) }
  }
 
  def callbackAction: Action[AnyContent] = Action.async {
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
  
}

```

After you call `authApi.callback(code)`, you have set *oAuth* and can make user-specific requests.
If you don't set the authorisation code you will get an error such as "Authorisation code not provided" 
if you make an *oAuth* request. Once this is set, the client will automatically take care 
of refreshing future tokens.  

Some endpoints don't need *oAuth*, so you can still call them if you don't set it.
Just read the docs of the endpoint you need to make sure it doesn't require authorisation.
For non-*oAuth* requests, the client will use the *client credentials* flow in order to
benefit of higher rates.

Read more about [Spotify Web API Authorisation](https://developer.spotify.com/web-api/authorization-guide/).

### Logging

In your *conf/logback.xml*:

```
  <logger name="spotify-scala-client" level="DEBUG"/>

```

**DEBUG** will log all the json responses.  
**WARN** is currently doing nothing.  
**INFO** is currently doing nothing.

### (*TO BE CONTINUED*)

***
