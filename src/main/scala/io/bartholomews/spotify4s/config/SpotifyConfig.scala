package io.bartholomews.spotify4s.config

import io.bartholomews.fsclient.config.{ApiUri, BaseUri}
import org.http4s.Uri
import pureconfig.ConfigSource
import pureconfig.error.ConfigReaderException

object SpotifyConfig {
  import cats.implicits._
  import pureconfig.generic.auto._

  private[spotify4s] val spotify: SpotifyReference = (for {
    conf <- ConfigSource.default
             .at("spotify")
             .load[Spotify]
             .leftMap(failures => ConfigReaderException(failures))

    api <- Uri.fromString(s"${conf.scheme}://${conf.api}")
    accounts <- Uri.fromString(s"${conf.scheme}://${conf.accounts}")
    base <- Uri.fromString(s"${conf.scheme}://${conf.domain}")
  } yield SpotifyReference(BaseUri(base), ApiUri(api), AccountsUri(accounts)))
    .valueOr(throw _)

  case class AccountsUri(value: Uri) extends AnyVal

  private case class Spotify(scheme: String, api: String, domain: String, accounts: String)

  private[spotify4s] case class SpotifyReference(baseUri: BaseUri, apiUri: ApiUri, accountsUri: AccountsUri)
}
