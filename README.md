[![Actions Status](https://github.com/bartholomews/spotify4s/workflows/build/badge.svg)](https://github.com/bartholomews/spotify4s/actions)
[![Coverage Status](https://coveralls.io/repos/github/bartholomews/spotify4s/badge.svg)](https://coveralls.io/github/bartholomews/spotify4s)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](https://github.com/bartholomews/spotify4s/blob/master/LICENSE)
<a href="https://typelevel.org/cats/"><img src="https://typelevel.org/cats/img/cats-badge.svg" height="40px" align="right" alt="Cats friendly" /></a>

# spotify4s
Type-safe, functional *Spotify* client on top of [sttp](https://sttp.softwaremill.com/en/stable).

This client is using the library [fsclient](https://github.com/bartholomews/fsclient)
which is an OAuth wrapper on top of [sttp](https://sttp.softwaremill.com/en/stable),
so you can use any effect type providing the relevant [backend](https://sttp.softwaremill.com/en/stable/backends/summary.html)

Pick one module:

```
// circe codecs
libraryDependencies += "io.bartholomews" %% "spotify4s-circe" % "0.0.0+1-841d0138-SNAPSHOT"
// play-json codecs
libraryDependencies += "io.bartholomews" %% "spotify4s-play" % "0.0.0+1-841d0138-SNAPSHOT"
// no codecs (you need to provide your own)
libraryDependencies += "io.bartholomews" %% "spotify4s-core" % "0.0.0+1-841d0138-SNAPSHOT"

resolvers +=
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
```

## Setup

### Register your app and obtain a *Client ID* and *Client Secret*

Follow the instructions here: https://developer.spotify.com/documentation/general/guides/app-settings

### Choose your sttp backend and create a *Spotify Client*

```scala
import sttp.client3.{HttpURLConnectionBackend, Identity, SttpBackend}

// Choose your effect / sttp backend
type F[X] = Identity[X]
val backend: SttpBackend[F, Any] = HttpURLConnectionBackend()
```

You can load your app credentials from `application.conf`:

```
user-agent {
    app-name = "your-app-name" // required
    app-version = "0.0.1-SNAPSHOT" // optional
    app-url = "https://github.com/your/app-name" // optional
}

spotify {
    client-id: ${YOUR_SPOTIFY_CLIENT_ID} // required
    client-secret: ${YOUR_SPOTIFY_CLIENT_SECRET} // required
}
```

```scala
import sttp.client3.Identity
import io.bartholomews.spotify4s.core.SpotifyAuthClient
import pureconfig.error.ConfigReaderFailures

val safeClient: Either[ConfigReaderFailures, SpotifyAuthClient[Identity]] = SpotifyAuthClient.fromConfig(backend)
val unsafeClient: SpotifyAuthClient[Identity] = SpotifyAuthClient.unsafeFromConfig(backend)
```

Or you can create a client manually:

```scala
import io.bartholomews.fsclient.core.FsClient
import io.bartholomews.fsclient.core.config.UserAgent
import io.bartholomews.fsclient.core.oauth.v2.{ClientId, ClientPassword, ClientSecret}
import io.bartholomews.spotify4s.core.SpotifyAuthClient

private val userAgent =
  UserAgent(appName = "your-app-name", appVersion = None, appUrl = None)

private val clientPassword = ClientPassword(
    clientId = ClientId(System.getenv("YOUR_SPOTIFY_CLIENT_ID")),
    clientSecret = ClientSecret(System.getenv("YOUR_SPOTIFY_CLIENT_SECRET"))
)

val client = new SpotifyAuthClient(userAgent, clientPassword, backend)
```

## Usage

The various [apis](https://developer.spotify.com/documentation/web-api/reference) are accessible as objects
within the client, e.g.:
```scala
// browse api
client.browse.getNewReleases(country = None)
// albums api
client.albums.getAlbum(id = SpotifyId("1weenld61qoidwYuZ1GESA"), country = None)
```

## Endpoints Task list

See [ENDPOINTS.md](https://github.com/bartholomews/spotify4s/blob/master/ENDPOINTS.md)

### [Client Credentials Flow](https://developer.spotify.com/documentation/general/guides/authorization-guide/#client-credentials-flow)

The Client Credentials flow is used in server-to-server authentication.
For this flow you can use a `SpotifySimpleClient` (instead of the full `SpotifyAuthClient`).
This client exposes only a subset of the endpoints, since it doesn't have authorization
to access user information. It manages the access token request/refresh internally.

```scala
import io.bartholomews.fsclient.core.http.SttpResponses.SttpResponse
import io.bartholomews.spotify4s.core.entities.{Page, SimpleTrack, SpotifyId}
import io.bartholomews.spotify4s.core.SpotifySimpleClient
import io.circe

// import the response handler and token response decoder
// (here using the circe module, you can also use the play framework or provide your own if using core module)
import io.bartholomews.spotify4s.circe.codecs._

// Create a `SpotifySimpleClient` (with the same options as the full `SpotifyAuthClient` as defined above)
val client = SpotifySimpleClient.unsafeFromConfig(backend)

val tracks: F[SttpResponse[circe.Error, Page[SimpleTrack]]] = client.albums.getAlbumTracks(
  id = SpotifyId("1weenld61qoidwYuZ1GESA"),
  country = None
)
```

### [Authorization Code Flow](https://developer.spotify.com/documentation/general/guides/authorization-guide/#authorization-code-flow)

This flow is suitable for long-running applications in which the user grants permission only once.  
It provides an **access token** that can be *refreshed*.  

```scala
import io.bartholomews.fsclient.core.http.SttpResponses.SttpResponse
import io.bartholomews.fsclient.core.oauth.{AccessTokenSigner, SignerV2}
import io.bartholomews.fsclient.core.oauth.v2.OAuthV2.{RedirectUri, RefreshToken}
import io.bartholomews.spotify4s.core.api.AuthApi.SpotifyUserAuthorizationRequest
import io.bartholomews.spotify4s.core.entities.{PrivateUser, SpotifyScope}
import io.circe
import sttp.client3.UriContext
import sttp.model.Uri

val request = SpotifyUserAuthorizationRequest(
import io.bartholomews.fsclient.core.http.SttpResponses.SttpResponse
import io.bartholomews.fsclient.core.oauth.AccessTokenSigner
import io.bartholomews.fsclient.core.oauth.v2.OAuthV2.{RedirectUri, RefreshToken}
import io.bartholomews.spotify4s.core.api.AuthApi.SpotifyUserAuthorizationRequest
import io.bartholomews.spotify4s.core.entities.{PrivateUser, SpotifyScope}
import io.circe
import sttp.client3.UriContext
import sttp.model.Uri

val request = SpotifyUserAuthorizationRequest(
  /*
    The URI to redirect to after the user grants or denies permission.
    This URI needs to have been entered in the Redirect URI whitelist
    that you specified when you registered your application.
    The value of redirect_uri here must exactly match one of the values
    you entered when you registered your application,
    including upper or lowercase, terminating slashes, and such.
   */
  redirectUri = RedirectUri(uri"https://bartholomews.io/callback"),
  /*
    A list of scopes. See https://developer.spotify.com/documentation/general/guides/authorization-guide/#list-of-scopes
    If no scopes are specified, authorization will be granted only to access publicly available information:
    that is, only information normally visible in the Spotify desktop, web, and mobile players.
   */
  scopes = List(SpotifyScope.PLAYLIST_READ_PRIVATE),
  /*
    Optional, but strongly recommended.
    This provides protection against attacks such as cross-site request forgery.
    See RFC-6749 (tools.ietf.org/html/rfc6749#section-4.1)
   */
  state = None
)

// Send the user to `authorizeUrl`
val authorizeUrl: Uri = client.auth.authorizeUrl(request)

// After they approve/deny your app, they will be sent to `uriAfterRedirect`, which should look something like:
val redirectionUriResponse: Uri = uri"http://localhost:9000/callback?code=AQApD1DlOFSQ27NXtPeZTmTbWDe9j6HyqxJrOy"

// import the response handler and token response decoder
// (here using the circe module, you can also use the play framework or provide your own if using core module)
import io.bartholomews.spotify4s.circe.codecs._

val accessTokenResponse: F[SttpResponse[circe.Error, AccessTokenSigner]] =
  client.auth.AuthorizationCode.acquire(request, redirectionUriResponse)

accessTokenResponse.body.map(
  (token: AccessTokenSigner) =>
    /*
    You can store both the accessTokenSigner.accessToken and accessTokenSigner.refreshToken.
    A refresh token is returned only with the first `accessTokenSigner` response,
    you need to keep using that to refresh, and subsequent token responses
    do not provide a refresh token.
     */
    if (token.isExpired())
      client.auth.AuthorizationCode.refresh(
        token.refreshToken.getOrElse(
          RefreshToken(
            "Only the first `accessTokenSigner` has a refresh token, I hope you still have that"
          )
        )
      )
    else {
      // The access token allows you to make requests to the Spotify Web API on behalf of a user:
      val me: F[SttpResponse[circe.Error, PrivateUser]] = client.users.me(token)
      println {
        me.body.map(_.displayName)
      }
    }
)
```

## Refined types

This client is using [refined](https://github.com/fthomas/refined) in order to enforce type safety on some request parameters.   
It also provides validators out of the box to make its usage a bit easier:
```scala
  import cats.data.NonEmptyList
  import io.bartholomews.spotify4s.core.api.SpotifyApi.SpotifyUris
  import io.bartholomews.spotify4s.core.entities.SpotifyUri

  // type SpotifyUris = Refined[NonEmptyList[SpotifyUri], MaxSize[100]]

  val ex1: Either[String, SpotifyUris] = SpotifyUri.fromList(List.empty)
  assert(ex1 == Left("Predicate failed: need to provide at least one element."))

  val tooManyUris: NonEmptyList[SpotifyUri] = NonEmptyList.fromListUnsafe(
    (1 to 101).map(_ => SpotifyUri("just one extra uri...")).toList
  )

  val ex2: Either[String, SpotifyUris] = SpotifyUri.fromNel(tooManyUris)
  assert(ex2 == Left("Predicate failed: a maximum of 100 uris can be set in one request."))
```

## Contributing

Any request / issue / help / PR is most welcome.

### CI/CD Pipeline

This project is using [sbt-ci-release](https://github.com/olafurpg/sbt-ci-release) plugin:
 - Every push to master will trigger a snapshot release.  
 - In order to trigger a regular release you need to push a tag:
 
    ```bash
    ./scripts/release.sh v1.0.0
    ```
 
 - If for some reason you need to replace an older version (e.g. the release stage failed):
 
    ```bash
    TAG=v1.0.0
    git push --delete origin ${TAG} && git tag --delete ${TAG} \
    && ./scripts/release.sh ${TAG}
    ```
