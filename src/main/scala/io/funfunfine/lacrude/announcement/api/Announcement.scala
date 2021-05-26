package io.funfunfine.lacrude.announcement.api

import io.estatico.newtype.macros.newtype
import eu.timepit.refined._
import eu.timepit.refined.string._
import eu.timepit.refined.auto._
import eu.timepit.refined.types.string.NonEmptyString
import eu.timepit.refined.types.numeric.{PosFloat, PosInt}
import eu.timepit.refined.numeric._
import eu.timepit.refined.api.{RefType, Refined}
import eu.timepit.refined.boolean._с
import eu.timepit.refined.char._
import eu.timepit.refined.collection._
import eu.timepit.refined.generic._
import eu.timepit.refined.string._

object types {}

case class Announcement(
    id: Announcement.Id,
    price: BigDecimal,
    deal: Deal,
    kind: Announcement.Subject,
    roomsAmount: Option[PosInt],
    address: NonEmptyString,
    seller: Announcement.Seller
)

object Announcement {

  @newtype case class Id(value: NonEmptyString)

  final case class Seller(name: NonEmptyString, phoneNumber: NonEmptyString)

  sealed trait Subject {
    def area: PosFloat
  }

  object Subject {
    final case class Room(area: PosFloat, roomsAmount: Option[PosInt], kitchenArea: Option[PosFloat]) extends Subject
    final case class Flat(area: PosFloat, livingArea: Option[PosFloat], kitchenArea: Option[PosFloat]) extends Subject
  }
}

sealed trait Deal

object Deal {

  final case class Rent(period: Rent.Period) extends Deal

  final object Rent {
    sealed trait Period

    final object Period {
      final case object Monthly extends Period
      final case object Daily extends Period
    }
  }
  final case class Sale() extends Deal
}
