package io.funfunfine.lacrude.announcement.repository

import io.funfunfine.lacrude.announcement.Announcement

trait AnnouncementRepository[F[_]] {
  def get(id: Announcement.Id): F[Option[Announcement]]

}
