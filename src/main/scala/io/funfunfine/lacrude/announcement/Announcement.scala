package io.funfunfine.lacrude.announcement

import derevo.cats.eqv
import derevo.circe.magnolia.customizableDecoder
import derevo.circe.magnolia.customizableEncoder
import derevo.circe.magnolia.{decoder => magnoliaDecoder}
import derevo.circe.magnolia.{encoder => magnoliaEncoder}
import derevo.derive
import derevo.scalacheck.{arbitrary => derevoArbitrary}

import io.funfunfine.lacrude.announcement.types.PhoneNumber

import io.circe.magnolia.configured.Configuration
import io.circe.refined._

import eu.timepit.refined.cats._
import eu.timepit.refined.scalacheck.all._
import eu.timepit.refined.types.numeric.PosFloat
import eu.timepit.refined.types.numeric.PosInt
import eu.timepit.refined.types.string.NonEmptyString

object types {

  @derive(magnoliaDecoder, magnoliaEncoder, derevoArbitrary, eqv)
  final case class PhoneNumber(
      value: NonEmptyString //TODO: regex phone & arbitrary
  )
}

@derive(customizableDecoder, customizableEncoder, derevoArbitrary, eqv)
final case class Announcement(
    id: Announcement.Id,
    price: PosFloat,
    deal: Deal,
    kind: Announcement.Subject,
    roomsAmount: Option[PosInt],
    address: NonEmptyString,
    seller: Announcement.Seller
)

object Announcement {

  implicit val announcementConfiguration: Configuration =
    Configuration.default.withDiscriminator("type").withSnakeCaseMemberNames.withSnakeCaseConstructorNames

  @derive(magnoliaDecoder, magnoliaEncoder, derevoArbitrary, eqv) //TODO: make it newtype
  final case class Id(value: Long) extends AnyVal

  @derive(customizableDecoder, customizableEncoder, derevoArbitrary, eqv)
  final case class Seller(name: NonEmptyString, phoneNumber: PhoneNumber)

  @derive(customizableDecoder, customizableEncoder, derevoArbitrary, eqv)
  sealed trait Subject {
    def area: PosFloat
  }

  object Subject {
    final case class Room(area: PosFloat, roomsAmount: Option[PosInt], kitchenArea: Option[PosFloat]) extends Subject
    final case class Flat(area: PosFloat, livingArea: Option[PosFloat], kitchenArea: Option[PosFloat]) extends Subject
  }
}

@derive(customizableDecoder, customizableEncoder, derevoArbitrary, eqv)
sealed trait Deal

object Deal {
  implicit val dealConfiguration: Configuration = Announcement.announcementConfiguration

  final case class Sale() extends Deal

  @derive(customizableDecoder, customizableEncoder, derevoArbitrary, eqv)
  final case class Rent(period: Rent.Period) extends Deal

  final object Rent {

    @derive(customizableDecoder, customizableEncoder, derevoArbitrary, eqv)
    sealed trait Period

    final object Period {
      final case object Monthly extends Period
      final case object Daily extends Period
    }
  }
}
