package io.funfunfine.lacrude.announcement.api

import cats.implicits._
import io.funfunfine.lacrude.HelloWorld
import io.funfunfine.lacrude.Jokes
import cats.effect.Sync
import io.funfunfine.lacrude.announcement.Announcement
import org.http4s.HttpRoutes
import org.http4s.circe.CirceEntityCodec._
import org.http4s.dsl.Http4sDsl
import sttp.tapir._
import sttp.tapir.json.circe._

object AnnouncementEndpoints {
  def endpoints: List[Endpoint[_, _, _, _]] =  createAnnouncement :: readAnnouncements ::readAnnouncement :: updateAnnouncement :: deleteAnnouncement :: Nil
  val baseEndpoint = endpoint.in("announcements")


  val createAnnouncement =
    baseEndpoint.post
      .in(jsonBody[Announcement])
      .out(stringBody.description("Айди добавленного объявления"))
      .description("Добавить объявление")

  val readAnnouncement = baseEndpoint.post
    .in(path[Announcement.Id]("announcement_id"))
    .out(jsonBody[Announcement])
    .description("Удалить объявление")

  val updateAnnouncement = baseEndpoint.post
    .in(path[Announcement.Id]("announcement_id"))
    .in(jsonBody[Announcement])
    .description("Удалить объявление")

  val readAnnouncements = //should be streaming
    baseEndpoint.get.out(jsonBody[List[Announcement]])

  val deleteAnnouncement = baseEndpoint.post
    .in(path[Announcement.Id]("announcement_id").description("Идентификатор удаляемого объявления"))
    .in(jsonBody[Announcement])
    .out(stringBody)
    .description("Удалить объявление")



}

object LacrudeRoutes {

  def jokeRoutes[F[_]: Sync](J: Jokes[F]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F] {}
    import dsl._
    HttpRoutes.of[F] {
      case GET -> Root / "joke" =>
        for {
          joke <- J.get
          resp <- Ok(joke)
        } yield resp
    }
  }

  def helloWorldRoutes[F[_]: Sync](H: HelloWorld[F]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F] {}
    import dsl._
    HttpRoutes.of[F] {
      case GET -> Root / "hello" / name =>
        for {
          greeting <- H.hello(HelloWorld.Name(name))
          resp     <- Ok(greeting)
        } yield resp
    }
  }
}
