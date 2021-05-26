package io.funfunfine.lacrude

import cats.effect._
import cats.implicits._
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.server.{Server}
import org.http4s.blaze.client.BlazeClientBuilder

import org.http4s.implicits._
import org.http4s.server.middleware.Logger
import scala.concurrent.ExecutionContext.global

object LacrudeServer {

  def resource[F[_]: Async]: Resource[F, Server] =
    for {
      client <-
        BlazeClientBuilder[F](scala.concurrent.ExecutionContext.global).resource
      helloWorldAlg = HelloWorld.impl[F]
      jokeAlg = Jokes.impl[F](client)
      httpApp = (
          LacrudeRoutes.helloWorldRoutes[F](helloWorldAlg) <+>
            LacrudeRoutes.jokeRoutes[F](jokeAlg)
      ).orNotFound
      finalHttpApp = Logger.httpApp(true, true)(httpApp)
      server <- BlazeServerBuilder[F](global)
        .bindHttp(8080)
        .withHttpApp(finalHttpApp)
        .resource
    } yield server

}
