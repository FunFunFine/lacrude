package io.funfunfine.lacrude.announcement.api

import derevo.derive
import cats.Functor
import io.funfunfine.lacrude.users.User
import tofu.generate.GenUUID
import tofu.logging.derivation.loggable
import tofu.syntax.monadic._

@derive(loggable)
final case class RequestContext(traceId: String, userId: User.Id)

object RequestContext {

  def make[F[_]: GenUUID: Functor](userId: User.Id): F[RequestContext] =
    GenUUID.randomString[F].map(RequestContext.apply(_, userId))
}
