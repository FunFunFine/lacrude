package io.funfunfine.lacrude.announcement.api

import cats.implicits._

import io.funfunfine.lacrude.announcement.Announcement

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
  val baseEndpoint: Endpoint[Unit, Unit, Unit, Any] = endpoint.in("announcements")

  val createAnnouncement: Endpoint[Announcement.Data, Unit, Announcement.Id, Any] =
    baseEndpoint.post
      .in(jsonBody[Announcement.Data])
      .out(plainBody[Announcement.Id].description("Айди добавленного объявления"))
      .description("Добавить объявление")

  val readAnnouncement: Endpoint[Announcement.Id, Unit, Announcement, Any] = baseEndpoint.post
    .in(path[Announcement.Id]("announcement_id"))
    .out(jsonBody[Announcement])
    .description("Удалить объявление")

  val updateAnnouncement: Endpoint[(Announcement.Id, Announcement.Patch), Unit, Unit, Any] = baseEndpoint.put
    .in(path[Announcement.Id]("announcement_id"))
    .in(jsonBody[Announcement.Patch])
    .description("Удалить объявление")

  val readAnnouncements: Endpoint[Unit, Unit, List[Announcement], Any] = //should be streaming
    baseEndpoint.get.out(jsonBody[List[Announcement]])

  val deleteAnnouncement: Endpoint[Announcement.Id, Unit, Unit, Any] = baseEndpoint.delete
    .in(path[Announcement.Id]("announcement_id").description("Идентификатор удаляемого объявления"))
    .description("Удалить объявление")

}

object AnnouncementRoutes {
  def stub[F[_], A]: Any => F[Either[Unit, A]] = ???

  def routes[F[_]: Timer: ContextShift: Concurrent]: HttpRoutes[F] =
    Http4sServerInterpreter.toRoutes(AnnouncementEndpoints.createAnnouncement)(stub[F, Announcement.Id]) <+>
      Http4sServerInterpreter.toRoutes(AnnouncementEndpoints.readAnnouncement)(stub[F, Announcement]) <+>
      Http4sServerInterpreter.toRoutes(AnnouncementEndpoints.readAnnouncements)(stub[F, List[Announcement]]) <+>
      Http4sServerInterpreter.toRoutes(AnnouncementEndpoints.updateAnnouncement)(stub[F, Unit]) <+>
      Http4sServerInterpreter.toRoutes(AnnouncementEndpoints.deleteAnnouncement)(stub[F, Unit])
}
