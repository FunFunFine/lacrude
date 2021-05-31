package io.funfunfine.lacrude.users

import java.util.UUID

import derevo.circe.magnolia.decoder
import derevo.circe.magnolia.encoder
import derevo.derive

import tofu.logging.derivation.loggable

import sttp.tapir.Codec
import sttp.tapir.CodecFormat
import sttp.tapir.derevo.schema

@derive(loggable, encoder, decoder, schema)
final case class User(id: User.Id)

object User {

  @derive(loggable, decoder, encoder, schema)
  final case class Id(value: UUID)

  object Id {
    implicit val codecId: Codec[String, Id, CodecFormat.TextPlain] = Codec.uuid.map(Id(_))(_.value)
  }
}
