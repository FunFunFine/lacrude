package io.funfunfine.lacrude.announcement.api

import cats.MonadThrow
import cats.implicits._

import tofu.WithRun
import tofu.generate.GenUUID
import tofu.syntax.context._
import tofu.syntax.feither._
import tofu.syntax.foption._

import io.funfunfine.lacrude.announcement.Announcement
import io.funfunfine.lacrude.announcement.AnnouncementService

import cats.effect.Concurrent
import cats.effect.ContextShift
import cats.effect.Timer

import sttp.tapir._
import sttp.tapir.json.circe._
import sttp.tapir.server.http4s.Http4sServerInterpreter

import org.http4s.HttpRoutes

object AnnouncementEndpoints {

  def definitions: List[Endpoint[_, _, _, _]] =
    createAnnouncement :: readAnnouncements :: readAnnouncement :: updateAnnouncement :: deleteAnnouncement :: Nil
  val baseEndpoint: Endpoint[Unit, String, Unit, Any] = endpoint.in("announcements").errorOut(stringBody)

  val createAnnouncement: Endpoint[Announcement.Data, String, Announcement.Id, Any] =
    baseEndpoint.post
      .in(jsonBody[Announcement.Data])
      .out(plainBody[Announcement.Id].description("Айди добавленного объявления"))
      .description("Добавить объявление")

  val readAnnouncement: Endpoint[Announcement.Id, String, Announcement, Any] = baseEndpoint.post
    .in(path[Announcement.Id]("announcement_id"))
    .out(jsonBody[Announcement])
    .description("Прочитать объявление по его идентификатору")

  val updateAnnouncement: Endpoint[(Announcement.Id, Announcement.Patch), String, Unit, Any] = baseEndpoint.put
    .in(path[Announcement.Id]("announcement_id"))
    .in(jsonBody[Announcement.Patch])
    .description("Обновить данные в объявлении")

  val readAnnouncements: Endpoint[Unit, String, List[Announcement], Any] = //TODO should be streaming
    baseEndpoint.get.out(jsonBody[List[Announcement]])

  val deleteAnnouncement: Endpoint[Announcement.Id, String, Unit, Any] = baseEndpoint.delete
    .in(path[Announcement.Id]("announcement_id").description("Идентификатор удаляемого объявления"))
    .description("Удалить объявление")

}

object AnnouncementRoutes {

  def routes[F[_]: Timer: ContextShift: GenUUID: Concurrent, G[_]: WithRun[*[_], F, RequestContext]: MonadThrow](
      announcementService: AnnouncementService[G]
  ): HttpRoutes[F] = {

    def contextEndpoint[I, O](f: I => G[O]): I => F[O] =
      input =>
        for {
          trace  <- RequestContext.make[F]
          output <- runContext[G](f(input))(trace)
        } yield output

    def handleErrors[I, O](f: I => F[O]): I => F[Either[String, O]] =
      input => f(input).attempt.leftMapIn(_.getMessage)

    Http4sServerInterpreter.toRoutes(AnnouncementEndpoints.createAnnouncement)(
      handleErrors(contextEndpoint(announcementService.create(_).map(_.id)))
    ) <+>
      Http4sServerInterpreter.toRoutes(AnnouncementEndpoints.readAnnouncement)(
        handleErrors(
          contextEndpoint[Announcement.Id, Announcement](
            id =>
              announcementService.getAll
                .map(_.find(_.id == id))
                .orThrow(new Throwable("No such announcement"))
          )
        )
      ) <+>
      Http4sServerInterpreter.toRoutes(AnnouncementEndpoints.readAnnouncements)(
        handleErrors(
          contextEndpoint(
            _ => announcementService.getAll
          )
        )
      ) <+>
      Http4sServerInterpreter.toRoutes(AnnouncementEndpoints.updateAnnouncement)(
        handleErrors(contextEndpoint {
          case (id, patch) => announcementService.update(id, patch)
        })
      ) <+>
      Http4sServerInterpreter.toRoutes(AnnouncementEndpoints.deleteAnnouncement)(
        handleErrors(contextEndpoint(announcementService.delete))
      )
  }
}
