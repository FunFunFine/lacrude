package io.funfunfine.lacrude

import scala.concurrent.ExecutionContext.global

import io.funfunfine.lacrude.announcement.api.AnnouncementRoutes

import cats.effect.ConcurrentEffect
import cats.effect.ContextShift
import cats.effect.Resource
import cats.effect.Timer

import org.http4s.implicits._
import org.http4s.server.Server
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware.Logger

object LacrudeServer {

  def resource[F[_]: ConcurrentEffect: ContextShift](implicit T: Timer[F]): Resource[F, Server[F]] = {

    val httpApp = Logger.httpApp(logHeaders = true, logBody = true)(AnnouncementRoutes.routes[F].orNotFound)

    BlazeServerBuilder[F](global)
      .bindHttp(8080, "0.0.0.0")
      .withHttpApp(httpApp)
      .resource
  }
}
