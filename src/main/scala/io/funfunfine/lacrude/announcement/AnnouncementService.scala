package io.funfunfine.lacrude.announcement

import derevo.derive

import cats.Monad

import tofu.higherKind.derived.representableK
import tofu.logging.LoggingCompanion
import tofu.logging.Logs
import tofu.logging.derivation.loggingMid
import tofu.syntax.logging._

@derive(loggingMid, representableK)
trait AnnouncementService[F[_]] {
  def create(data: Announcement.Data): F[Announcement]
  def getAll: F[List[Announcement]]
  def delete(id: Announcement.Id): F[Unit]
  def update(id: Announcement.Id, patch: Announcement.Patch): F[Unit]
}

object AnnouncementService extends LoggingCompanion[AnnouncementService] {

  def make[F[_]: Monad: AnnouncementService.Log: Logs.Universal]: AnnouncementService[F] = new AnnouncementService[F] {

    override def create(
        data: Announcement.Data
    ): F[Announcement] = ???

    override def getAll: F[List[Announcement]] = ???
    override def delete(id: Announcement.Id): F[Unit] = info"Yay deleting!"
    override def update(id: Announcement.Id, patch: Announcement.Patch): F[Unit] = ???
  }.attachLogs
}
