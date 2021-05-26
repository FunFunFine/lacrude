package io.funfunfine.lacrude

import cats.implicits._

import io.circe.Decoder
import io.circe.Encoder
import io.circe.generic.semiauto._

import cats.effect.Sync

import org.http4s.Method._
import org.http4s.circe.CirceEntityCodec._
import org.http4s.client.Client
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.implicits._

trait Jokes[F[_]] {
  def get: F[Jokes.Joke]
}

object Jokes {
  def apply[F[_]](implicit ev: Jokes[F]): Jokes[F] = ev

  final case class Joke(joke: String) extends AnyVal

  object Joke {
    implicit val jokeDecoder: Decoder[Joke] = deriveDecoder[Joke]

    implicit val jokeEncoder: Encoder[Joke] = deriveEncoder[Joke]

  }

  final case class JokeError(e: Throwable) extends RuntimeException

  def impl[F[_]: Sync](C: Client[F]): Jokes[F] = new Jokes[F] {
    val dsl = new Http4sClientDsl[F] {}
    import dsl._

    def get: F[Jokes.Joke] =
      C.expect[Joke](GET(uri"https://icanhazdadjoke.com/"))
        .adaptError {
          case t => JokeError(t)
        } // Prevent Client Json Decoding Failure Leaking
  }
}
