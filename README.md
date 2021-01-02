[![Actions Status](https://github.com/bartholomews/spotify4s/workflows/build/badge.svg)](https://github.com/bartholomews/spotify4s/actions)
[![codecov](https://codecov.io/gh/bartholomews/spotify4s/branch/master/graph/badge.svg)](https://codecov.io/gh/bartholomews/spotify4s)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](https://github.com/bartholomews/spotify4s/blob/master/LICENSE)
<a href="https://typelevel.org/cats/"><img src="https://typelevel.org/cats/img/cats-badge.svg" height="40px" align="right" alt="Cats friendly" /></a>

# spotify4s
*Spotify* client on top of [sttp](https://sttp.softwaremill.com/en/stable).

This client is using the library [fsclient](https://github.com/bartholomews/fsclient)
which is an OAuth wrapper on top of [sttp](https://sttp.softwaremill.com/en/stable),
so you can use any effect type providing the relevant [backend](https://sttp.softwaremill.com/en/stable/backends/summary.html)

## Endpoints Task list

See [ENDPOINTS.md](https://github.com/bartholomews/spotify4s/blob/master/ENDPOINTS.md)

## Refined types

This client is using [refined](https://github.com/fthomas/refined) in order to enforce type safety on some request parameters.   
It also provides validators out of the box to make its usage a bit easier:
```scala
  import cats.data.NonEmptyList
  import io.bartholomews.spotify4s.core.api.SpotifyApi.SpotifyUris
  import io.bartholomews.spotify4s.core.entities.SpotifyUri

  // type SpotifyUris = Refined[NonEmptyList[SpotifyUri], MaxSize[100]]

  val ex1: Either[String, SpotifyUris] = SpotifyUri.fromList(List.empty)
  assert(ex1 == Left("Predicate failed: need to provide at least one uri."))

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