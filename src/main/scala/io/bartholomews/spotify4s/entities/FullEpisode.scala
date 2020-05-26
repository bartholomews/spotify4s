package io.bartholomews.spotify4s.entities

import io.circe.generic.extras.ConfiguredJsonCodec
import org.http4s.Uri

// https://developer.spotify.com/documentation/web-api/reference/object-model/#episode-object-full

/*
audio_preview_url 	string 	A URL to a 30 second preview (MP3 format) of the episode. null if not available.
description 	string 	A description of the episode.
duration_ms 	integer 	The episode length in milliseconds.
explicit 	boolean 	Whether or not the episode has explicit content (true = yes it does; false = no it does not OR unknown).
external_urls 	an external URL object 	External URLs for this episode.
href 	string 	A link to the Web API endpoint providing full details of the episode.
id 	string 	The Spotify ID for the episode.
images 	array of image objects 	The cover art for the episode in various sizes, widest first.
is_externally_hosted 	boolean 	True if the episode is hosted outside of Spotify’s CDN.
is_playable 	boolean 	True if the episode is playable in the given market. Otherwise false.
languages 	array of strings 	A list of the languages used in the episode, identified by their ISO 639 code.
name 	string 	The name of the episode.
release_date 	string 	The date the episode was first released, for example "1981-12-15". Depending on the precision, it might be shown as "1981" or "1981-12".
release_date_precision 	string 	The precision with which release_date value is known: "year", "month", or "day".
resume_point 	a resume point object 	The user’s most recent position in the episode. Set if the supplied access token is a user token and has the scope user-read-playback-position.
show 	a simplified show object 	The show on which the episode belongs.
type 	string 	The object type: "episode".
uri 	string 	The Spotify ID for the episode.
 */

@ConfiguredJsonCodec
case class FullEpisode(
  audioPreviewUrl: Uri,
  description: String,
  durationMs: Int,
  explicit: Boolean,
  externalUrls: ExternalResourceUrl,
  href: Uri,
  id: SpotifyId,
  images: List[SpotifyImage],
  isExternallyHosted: Boolean,
  isPlayable: Boolean,
  languages: List[String],
  name: String,
  releaseDate: ReleaseDate,
  //  resumePoint:
  //  show:
  uri: SpotifyUri
)
