package io.bartholomews.spotify4s.entities

import io.circe.generic.extras.Configuration

package object requests {
  implicit val defaultConfig: Configuration = io.bartholomews.spotify4s.entities.defaultConfig
}
