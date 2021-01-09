package io.bartholomews.spotify4s.circe.api

import io.bartholomews.spotify4s.circe.{CirceServerBehaviours, SpotifyCirceApi}
import io.bartholomews.spotify4s.core.api.TracksApiSpec
import io.circe
import io.circe.Decoder

class BrowseApiCirceSpec extends TracksApiSpec[Decoder, circe.Error] with CirceServerBehaviours with SpotifyCirceApi
