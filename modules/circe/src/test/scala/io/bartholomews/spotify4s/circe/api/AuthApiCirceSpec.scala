//package io.bartholomews.spotify4s.circe.api
//
//import io.bartholomews.fsclient.core.oauth.v2.OAuthV2.ResponseHandler
//import io.bartholomews.fsclient.core.oauth.{AccessTokenSigner, NonRefreshableTokenSigner}
//import io.bartholomews.spotify4s.circe.CirceServerBehaviours
//import io.bartholomews.spotify4s.core.api.AuthApiSpec
//import io.circe
//
//class AuthApiCirceSpec extends AuthApiSpec[io.circe.Error] with CirceServerBehaviours {
//  import io.bartholomews.spotify4s.circe._
//
//  override implicit def accessTokenSignerResponseHandler: ResponseHandler[circe.Error, AccessTokenSigner] =
//    responseHandler[AccessTokenSigner]
//
//  override implicit def nonRefreshableTokenSignerResponseHandler
//    : ResponseHandler[circe.Error, NonRefreshableTokenSigner] =
//    responseHandler[NonRefreshableTokenSigner]
//}
