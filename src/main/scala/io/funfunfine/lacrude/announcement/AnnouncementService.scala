package io.funfunfine.lacrude.announcement

import derevo.derive
import cats.Monad
import io.funfunfine.lacrude.users.User
import tofu.higherKind.derived.representableK
import tofu.logging.LoggingCompanion
import tofu.logging.Logs
import tofu.logging.derivation.loggingMid
import tofu.syntax.logging._

@derive(loggingMid, representableK)
trait AnnouncementService[F[_]] {
  def create(user: User.Id, data: Announcement.Data): F[Announcement]
  def getAll(user: User.Id): F[List[Announcement]]
  def delete(user: User.Id, id: Announcement.Id): F[Unit]
  def update(user: User.Id, id: Announcement.Id, patch: Announcement.Patch): F[Unit]
}

object AnnouncementService extends LoggingCompanion[AnnouncementService] {

  def make[F[_]: Monad: Logs.Universal: AnnouncementService.Log]: AnnouncementService[F] = new AnnouncementService[F] {

    override def create(user: User.Id, data: Announcement.Data): F[Announcement] = ???
    def update(user: User.Id, id: Announcement.Id, patch: Announcement.Patch): F[Unit] = ???

    override def getAll(user: User.Id): F[List[Announcement]] = ???
    override def delete(user: User.Id, id: Announcement.Id): F[Unit] = info"Yay deleting!"
  }.attachLogs
}
