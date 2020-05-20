[![Build Status](https://travis-ci.org/bartholomews/spotify4s.svg?branch=master)](https://travis-ci.org/bartholomews/spotify4s)
[![codecov](https://codecov.io/gh/bartholomews/spotify4s/branch/master/graph/badge.svg)](https://codecov.io/gh/bartholomews/spotify4s)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](https://github.com/bartholomews/spotify4s/blob/master/LICENSE)

# spotify-scala-client
*Spotify Web API Scala Client*
 
### Add to your *build.sbt*

```
resolvers += "Sonatype OSS Snapshots".at("https://oss.sonatype.org/content/repositories/snapshots")
"io.bartholomews" %% "spotify4s" % "0.0.1-SNAPSHOT",
```

### Endpoints Task list

See [ENDPOINTS.md](https://github.com/bartholomews/spotify4s/blob/master/ENDPOINTS.md)
 
### Logging

In your *conf/logback.xml*:

```
  <logger name="spotify4s" level="INFO"/>
```

**INFO** will log all the requests.  
**DEBUG** will log all the responses.
  
---
