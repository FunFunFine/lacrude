package io.funfunfine.lacrude

import scala.concurrent.ExecutionContext.global
import cats.MonadThrow
import cats.syntax.semigroupk._
import tofu.Delay
import tofu.WithRun
import tofu.logging.Logs
import tofu.syntax.monadic._
import io.funfunfine.lacrude.announcement.AnnouncementService
import io.funfunfine.lacrude.announcement.api.AnnouncementEndpoints
import io.funfunfine.lacrude.announcement.api.RequestContext
import cats.effect.ConcurrentEffect
import cats.effect.ContextShift
import cats.effect.Resource
import cats.effect.Sync
import cats.effect.Timer
import io.funfunfine.lacrude.users.UsersService
import io.funfunfine.lacrude.users.repository.UsersRepository
import sttp.tapir.docs.openapi.OpenAPIDocsInterpreter
import sttp.tapir.openapi.circe.yaml._
import sttp.tapir.swagger.http4s.SwaggerHttp4s
import org.http4s.HttpRoutes
import org.http4s.implicits._
import org.http4s.server.Server
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware.Logger
import sttp.tapir.Endpoint

object LacrudeServer {

  def resource[
      F[_]: ConcurrentEffect: ContextShift: Timer,
      G[_]: WithRun[*[_], F, RequestContext]: MonadThrow: Delay
  ]: F[Resource[F, Server[F]]] = {

    def swaggerRoute(endpoints: List[Endpoint[_, _, _, _]]): F[HttpRoutes[F]] =
      for {
        openApi <-
          Sync[F].delay(
            OpenAPIDocsInterpreter
              .toOpenAPI(endpoints, "Lacrude: объявления", "0.0.1")
              .toYaml
          )
        swagger <-
          Sync[F].delay(
            new SwaggerHttp4s(openApi)
          )
      } yield swagger.routes[F]

    implicit val logs = Logs.contextual[G, RequestContext]
    val usersService = UsersService.make[F](UsersRepository.make[F](transactor = ???))
    val announcementService = AnnouncementService.make[G]

    val announcementEndpoints = new AnnouncementEndpoints[F, G](announcementService, usersService)
    for {
      swagger <- swaggerRoute(announcementEndpoints.definitions)
      httpApp =
        Logger.httpApp(logHeaders = true, logBody = true)(
          (announcementEndpoints.routes <+> swagger).orNotFound
        )
    } yield BlazeServerBuilder[F](global)
      .bindHttp(8080, "0.0.0.0")
      .withHttpApp(httpApp)
      .resource
  }
}
