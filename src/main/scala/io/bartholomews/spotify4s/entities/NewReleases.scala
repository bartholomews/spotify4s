package io.bartholomews.spotify4s.entities

import io.bartholomews.fsclient.codecs.FsJsonResponsePipe
import io.circe.generic.extras.ConfiguredJsonCodec

@ConfiguredJsonCodec
case class NewReleases(albums: Page[SimpleAlbum])

object NewReleases extends FsJsonResponsePipe[NewReleases]
