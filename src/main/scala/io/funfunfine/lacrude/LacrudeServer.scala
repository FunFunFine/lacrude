package io.funfunfine.lacrude

import scala.concurrent.ExecutionContext.global

import cats.implicits._

import io.funfunfine.lacrude.announcement.api.LacrudeRoutes

import cats.effect.ConcurrentEffect
import cats.effect.Timer

import fs2.Stream

import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware.Logger

object LacrudeServer {

  def stream[F[_]: ConcurrentEffect](implicit T: Timer[F]): Stream[F, Nothing] = {
    for {
      client <- BlazeClientBuilder[F](global).stream
      helloWorldAlg = HelloWorld.impl[F]
      jokeAlg = Jokes.impl[F](client)

      // Combine Service Routes into an HttpApp.
      // Can also be done via a Router if you
      // want to extract a segments not checked
      // in the underlying routes.
      httpApp = (
                  LacrudeRoutes.helloWorldRoutes[F](helloWorldAlg) <+>
                    LacrudeRoutes.jokeRoutes[F](jokeAlg)
                ).orNotFound

      // With Middlewares in place
      finalHttpApp = Logger.httpApp(true, true)(httpApp)

      exitCode <- BlazeServerBuilder[F](global)
                    .bindHttp(8080, "0.0.0.0")
                    .withHttpApp(finalHttpApp)
                    .serve
    } yield exitCode
  }.drain
}
