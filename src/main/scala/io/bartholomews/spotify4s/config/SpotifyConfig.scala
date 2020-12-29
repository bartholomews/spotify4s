package io.bartholomews.spotify4s.config

import pureconfig.ConfigSource
import sttp.model.Uri

object SpotifyConfig {
  import pureconfig.generic.auto._
  import io.bartholomews.fsclient.core.config.sttpUriReader

  private[spotify4s] val spotify =
    ConfigSource.default.at("spotify").loadOrThrow[SpotifyReference]

  case class BaseUri(value: Uri) extends AnyVal
  case class ApiUri(value: Uri) extends AnyVal
  case class AccountsUri(value: Uri) extends AnyVal

  private[spotify4s] case class SpotifyReference(baseUri: BaseUri, apiUri: ApiUri, accountsUri: AccountsUri)
}
