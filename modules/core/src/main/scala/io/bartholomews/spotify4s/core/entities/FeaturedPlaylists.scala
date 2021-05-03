package io.bartholomews.spotify4s.core.entities

final case class FeaturedPlaylists(message: String, playlists: Page[SimplePlaylist])
