package io.funfunfine.lacrude.announcement

import derevo.cats.eqv
import derevo.circe.magnolia.customizableDecoder
import derevo.circe.magnolia.customizableEncoder
import derevo.circe.magnolia.{decoder => magnoliaDecoder}
import derevo.circe.magnolia.{encoder => magnoliaEncoder}
import derevo.derive
import derevo.scalacheck.{arbitrary => derevoArbitrary}

import tofu.logging.Loggable
import tofu.logging.derivation.loggable

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

  @derive(magnoliaDecoder, magnoliaEncoder, derevoArbitrary, eqv, schema, loggable)
  final case class PhoneNumber(
      value: NonEmptyString //TODO: regex phone & arbitrary
  )

  implicit val loggablePosStringContravariant: Loggable[NonEmptyString] = Loggable[String].contramap[NonEmptyString](
    pf => pf.value
  )

}

@derive(customizableDecoder, customizableEncoder, derevoArbitrary, eqv, schema, loggable)
final case class Announcement(
    id: Announcement.Id,
    data: Announcement.Data
)

object Announcement {

  implicit val loggablePosFloatContravariant: Loggable[PosFloat] = Loggable[Float].contramap[PosFloat](
    pf => pf.value
  )

  implicit val loggablePosIntContravariant: Loggable[PosInt] = Loggable[Int].contramap[PosInt](
    pf => pf.value
  )

  implicit val loggablePosStringContravariant: Loggable[NonEmptyString] = Loggable[String].contramap[NonEmptyString](
    pf => pf.value
  )

  @derive(customizableDecoder, customizableEncoder, derevoArbitrary, eqv, schema, loggable)
  final case class Data(
      price: PosFloat,
      deal: Deal,
      subject: Announcement.Subject,
      roomsAmount: Option[PosInt],
      address: NonEmptyString,
      seller: Announcement.Seller
  )
  /*
   * create table announcements (
   *   id varchar primary key,
   *   price float not null,
   *   deal_id varchar not null,
   *   rent_id varchar references rents,
   *   sale_id varchar references sales check (rent_id is null or sale_id is null), but not both
   *   rooms_amount int,
   *   address varchar,
   *   seller_name varchar,
   *   seller_number varchar
   *)
   *
   * create table rents (...)
   * create table sales (...)
   *
   *
   * */

  @derive(customizableDecoder, customizableEncoder, derevoArbitrary, eqv, schema, loggable)
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

  @derive(magnoliaDecoder, magnoliaEncoder, derevoArbitrary, eqv, schema, loggable) //TODO: make it newtype
  final case class Id(value: String) extends AnyVal

  object Id {
    insertInstancesHere()
    implicit val codecId: Codec[String, Id, CodecFormat.TextPlain] = Codec.stringCodec(Id(_))

  }

  @derive(customizableDecoder, customizableEncoder, derevoArbitrary, eqv, schema, loggable)
  final case class Seller(name: NonEmptyString, phoneNumber: PhoneNumber)

  @derive(customizableDecoder, customizableEncoder, derevoArbitrary, eqv, schema, loggable)
  sealed trait Subject {
    def area: PosFloat
  }

  object Subject {
    final case class Room(area: PosFloat, roomsAmount: Option[PosInt], kitchenArea: Option[PosFloat]) extends Subject
    final case class Flat(area: PosFloat, livingArea: Option[PosFloat], kitchenArea: Option[PosFloat]) extends Subject
  }
}

@derive(customizableDecoder, customizableEncoder, derevoArbitrary, eqv, schema, loggable)
sealed trait Deal

object Deal {
  implicit val dealConfiguration: Configuration = Announcement.announcementConfiguration

  final case class Sale() extends Deal

  @derive(customizableDecoder, customizableEncoder, derevoArbitrary, eqv, schema, loggable)
  sealed trait Rent extends Deal

  final object Rent {

    final case object Monthly extends Rent
    final case object Daily extends Rent
  }
}
