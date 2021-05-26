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

import sttp.tapir.Codec
import sttp.tapir.CodecFormat
import sttp.tapir.codec.refined._
import sttp.tapir.derevo.schema

import eu.timepit.refined.cats._
import eu.timepit.refined.scalacheck.all._
import eu.timepit.refined.types.numeric.PosFloat
import eu.timepit.refined.types.numeric.PosInt
import eu.timepit.refined.types.string.NonEmptyString

object types {

  @derive(magnoliaDecoder, magnoliaEncoder, derevoArbitrary, eqv, schema)
  final case class PhoneNumber(
      value: NonEmptyString //TODO: regex phone & arbitrary
  )
}

@derive(customizableDecoder, customizableEncoder, derevoArbitrary, eqv, schema)
final case class Announcement(
    id: Announcement.Id,
    data: Announcement.Data
)

object Announcement {

  @derive(customizableDecoder, customizableEncoder, derevoArbitrary, eqv, schema)
  final case class Data(
      price: PosFloat,
      deal: Deal,
      kind: Announcement.Subject,
      roomsAmount: Option[PosInt],
      address: NonEmptyString,
      seller: Announcement.Seller
  )

  @derive(customizableDecoder, customizableEncoder, derevoArbitrary, eqv, schema)
  final case class Patch(
      price: Option[PosFloat],
      deal: Option[Deal],
      kind: Option[Announcement.Subject],
      roomsAmount: Option[PosInt],
      address: Option[NonEmptyString],
      seller: Option[Announcement.Seller]
  )

  implicit val announcementConfiguration: Configuration =
    Configuration.default.withDiscriminator("type").withSnakeCaseMemberNames.withSnakeCaseConstructorNames

  @derive(magnoliaDecoder, magnoliaEncoder, derevoArbitrary, eqv, schema) //TODO: make it newtype
  final case class Id(value: String) extends AnyVal

  object Id {
    insertInstancesHere()
    implicit val codecId: Codec[String, Id, CodecFormat.TextPlain] = Codec.stringCodec(Id(_))

  }

  @derive(customizableDecoder, customizableEncoder, derevoArbitrary, eqv, schema)
  final case class Seller(name: NonEmptyString, phoneNumber: PhoneNumber)

  @derive(customizableDecoder, customizableEncoder, derevoArbitrary, eqv, schema)
  sealed trait Subject {
    def area: PosFloat
  }

  object Subject {
    final case class Room(area: PosFloat, roomsAmount: Option[PosInt], kitchenArea: Option[PosFloat]) extends Subject
    final case class Flat(area: PosFloat, livingArea: Option[PosFloat], kitchenArea: Option[PosFloat]) extends Subject
  }
}

@derive(customizableDecoder, customizableEncoder, derevoArbitrary, eqv, schema)
sealed trait Deal

object Deal {
  implicit val dealConfiguration: Configuration = Announcement.announcementConfiguration

  final case class Sale() extends Deal

  @derive(customizableDecoder, customizableEncoder, derevoArbitrary, eqv, schema)
  final case class Rent(period: Rent.Period) extends Deal

  final object Rent {

    @derive(customizableDecoder, customizableEncoder, derevoArbitrary, eqv, schema)
    sealed trait Period

    final object Period {
      final case object Monthly extends Period
      final case object Daily extends Period
    }
  }
}
