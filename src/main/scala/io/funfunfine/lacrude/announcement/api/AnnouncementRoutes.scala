package io.funfunfine.lacrude.announcement.api

import derevo.derive

import cats.Functor

import tofu.generate.GenUUID
import tofu.logging.derivation.loggable
import tofu.syntax.monadic._

@derive(loggable)
final case class RequestContext(traceId: String)

object RequestContext {
  def make[F[_]: GenUUID: Functor]: F[RequestContext] = GenUUID.randomString[F].map(RequestContext.apply _)
}
