package io.bartholomews.spotify4s.playJson.api

import io.bartholomews.scalatestudo.WireWordSpec
import io.bartholomews.spotify4s.core.api.UsersApiSpec
import io.bartholomews.spotify4s.core.entities.{PrivateUser, SimplePlaylist}
import io.bartholomews.spotify4s.playJson.{PlayEntityCodecs, PlayServerBehaviours}
import play.api.libs.json.{JsError, JsValue, Reads, Writes}

class UsersApiPlaySpec
    extends UsersApiSpec[Writes, Reads, JsError, JsValue]
    with WireWordSpec
    with PlayServerBehaviours
    with PlayEntityCodecs {
  override implicit def privateUserDecoder: Reads[PrivateUser] = privateUserCodec
  override implicit def simplePlaylistDecoder: Reads[SimplePlaylist] = simplePlaylistCodec
}
