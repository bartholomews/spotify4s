package io.bartholomews.spotify4s.entities

import io.circe.generic.extras.ConfiguredJsonCodec

// https://developer.spotify.com/documentation/general/guides/track-relinking-guide/
@ConfiguredJsonCodec
case class Restrictions(reason: String)
