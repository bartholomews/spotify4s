[![Build Status](https://travis-ci.org/bartholomews/spotify4s.svg?branch=master)](https://travis-ci.org/bartholomews/spotify4s)
[![codecov](https://codecov.io/gh/bartholomews/spotify4s/branch/master/graph/badge.svg)](https://codecov.io/gh/bartholomews/spotify4s)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](https://github.com/bartholomews/spotify4s/blob/master/LICENSE)

# spotify4s
Early stage *Spotify* client with the *Typelevel* stack.  

The client is using the library [fsclient](https://github.com/bartholomews/fsclient)
which is a wrapper on http4s/fs2 with circe and OAuth handling.

## Endpoints Task list

See [ENDPOINTS.md](https://github.com/bartholomews/spotify4s/blob/master/ENDPOINTS.md)

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