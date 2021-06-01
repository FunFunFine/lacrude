package io.funfunfine.lacrude.announcement.api

import cats.Monad
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
import io.funfunfine.lacrude.users.User
import io.funfunfine.lacrude.users.User
import io.funfunfine.lacrude.users.UsersService
import sttp.tapir._
import sttp.tapir.json.circe._
import sttp.tapir.server.http4s.Http4sServerInterpreter
import org.http4s.HttpRoutes
import sttp.tapir.model.UsernamePassword
import sttp.tapir.server.PartialServerEndpoint
import tofu.Delay

import java.util.UUID
import scala.util.Try

class AnnouncementEndpoints[F[_]: Timer: ContextShift: GenUUID: Concurrent, G[_]: WithRun[
  *[_],
  F,
  RequestContext
]: MonadThrow](announcementService: AnnouncementService[G], usersService: UsersService[F]) {

  def contextEndpoint[I, O](f: I => G[O])(userId: I => User.Id): I => F[O] = {
    input =>
      for {
        trace  <- RequestContext.make[F](userId(input))
        output <- runContext[G](f(input))(trace)
      } yield output
  }

  def handleErrors[I, O](f: I => F[O]): I => F[Either[String, O]] =
    input => f(input).attempt.leftMapIn(_.getMessage)

  private def authUser =
    (up: UsernamePassword) =>
      for {
        uuid <- Delay[F].delay[Option[UUID]](Try(UUID.fromString(up.username)).toOption)
        id <- (uuid, up.password).tupled.flatTraverse {
                case (id, pwd) =>
                  val userId = User.Id(id)
                  usersService.isRegistered(pwd, userId).map(if (_) userId.some else none)
              }
      } yield Either.fromOption(id, "Non authorized")

  def definitions: List[Endpoint[_, _, _, _]] =
    createAnnouncement :: readAnnouncements :: readAnnouncement :: updateAnnouncement :: deleteAnnouncement :: Nil

  private def baseEndpoint: PartialServerEndpoint[UsernamePassword, User.Id, Unit, String, Unit, Any, F] =
    endpoint
      .in("announcements")
      .errorOut(stringBody)
      .in(auth.basic[UsernamePassword]())
      .serverLogicForCurrent[User.Id, F](authUser)

  val createAnnouncement =
    baseEndpoint.post
      .in(jsonBody[Announcement.Data])
      .out(plainBody[Announcement.Id].description("Айди добавленного объявления"))
      .description("Добавить объявление")
      .serverLogic(handleErrors(contextEndpoint({
        case (userId, id) => announcementService.create(userId, id).map(_.id)
      })(_._1)))

  val readAnnouncement = baseEndpoint.post
    .in(path[Announcement.Id]("announcement_id"))
    .out(jsonBody[Announcement])
    .description("Прочитать объявление по его идентификатору")
    .serverLogic {
      handleErrors(
        contextEndpoint {
          case (userId, id) =>
            announcementService
              .getAll(userId)
              .map(_.find(_.id == id))
              .orThrow(new Throwable("No such announcement"))
        }(_._1)
      )
    }

  val updateAnnouncement =
    baseEndpoint.put
      .in(path[Announcement.Id]("announcement_id"))
      .in(jsonBody[Announcement.Patch])
      .description("Обновить данные в объявлении")
      .serverLogic(handleErrors(contextEndpoint {
        case (userId, id, patch) => announcementService.update(userId, id, patch)
      }(_._1)))

  val readAnnouncements = //TODO should be streaming
    baseEndpoint.get
      .out(jsonBody[List[Announcement]])
      .serverLogic(
        handleErrors(
          contextEndpoint {
            case (userId, _) => announcementService.getAll(userId)
          }(_._1)
        )
      )

  val deleteAnnouncement = baseEndpoint.delete
    .in(path[Announcement.Id]("announcement_id").description("Идентификатор удаляемого объявления"))
    .description("Удалить объявление")
    .serverLogic(handleErrors(contextEndpoint {
      case (userId, id) => announcementService.delete(userId, id)
    }(_._1)))

  def routes: HttpRoutes[F] = ???
}
