package io.bartholomews.spotify4s.playJson.api

import io.bartholomews.spotify4s.core.api.AlbumsApiSpec
import io.bartholomews.spotify4s.core.entities.{FullAlbum, FullAlbumsResponse, SimpleTrack}
import io.bartholomews.spotify4s.playJson.{PlayEntityCodecs, PlayServerBehaviours}
import play.api.libs.json.{JsError, JsValue, Reads, Writes}

class AlbumsApiPlaySpec
    extends AlbumsApiSpec[Writes, Reads, JsError, JsValue]
    with PlayServerBehaviours
    with PlayEntityCodecs
