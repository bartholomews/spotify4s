package it.turingtest.spotify.scala.client.entities

/**
  * @see https://developer.spotify.com/web-api/using-scopes/
  */
trait Scope {
  val value: String
}

/**
  * If no scope is specified, access is permitted only to publicly available information:
  * that is, only information normally visible to normal logged-in users of the
  * Spotify desktop, web, and mobile clients (e.g. public playlists).
  */
case object NO_SCOPE extends Scope {
  override val value = ""
}

/**
  * Read access to user's private playlists.
  */
case object PLAYLIST_READ_PRIVATE extends Scope {
  override val value = "playlist-read-private"
}

/**
  * Include collaborative playlists when requesting a user's playlists.
  */
case object PLAYLIST_READ_COLLABORATIVE extends Scope {
  override val value = "playlist-read-collaborative"
}

/**
  * Write access to a user's public playlists.
  */
case object PLAYLIST_MODIFY_PUBLIC extends Scope {
  override val value = "playlist-modify-public"
}

/**
  * Write access to a user's private playlists.
  */
case object PLAYLIST_MODIFY_PRIVATE extends Scope {
  override val value = "playlist-modify-private"
}

/**
  * Control playback of a Spotify track. This scope is currently only available to Spotify native SDKs
  * (for example, the iOS SDK and the Android SDK). The user must have a Spotify Premium account.
  */
case object STREAMING extends Scope {
  override val value = "streaming"
}

/**
  * Write/delete access to the list of artists and other users that the user follows.
  */
case object USER_FOLLOW_MODIFY extends Scope {
  override val value = "user-follow-modify"
}

/**
  * Read access to the list of artists and other users that the user follows.
  */
case object USER_FOLLOW_READ extends Scope {
  override val value = "user-follow-read"
}

/**
  * Read access to a user's "Your Music" library.
  */
case object USER_LIBRARY_READ extends Scope {
  override val value = "user-library-read"
}

/**
  * Write/delete access to a user's "Your Music" library.
  */
case object USER_LIBRARY_MODIFY extends Scope {
  override val value = "user-library-modify"
}

/**
  * Read access to user’s subscription details (type of user account).
  */
case object USER_READ_PRIVATE extends Scope {
  override val value = "user-read-private"
}

/**
  * Read access to the user's birthdate.
  */
case object USER_READ_BIRTHDATE extends Scope {
  override val value = "user-read-birthdate"
}

/**
  * Read access to user’s email address.
  */
case object USER_READ_EMAIL extends Scope {
  override val value = "user-read-email"
}

/**
  * Read access to a user's top artists and tracks
  */
case object USER_TOP_READ extends Scope {
  override val value = "user-top-read"
}
