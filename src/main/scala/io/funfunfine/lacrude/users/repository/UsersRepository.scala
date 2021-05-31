package io.funfunfine.lacrude.users.repository

import io.funfunfine.lacrude.users.User

import cats.effect.BracketThrow

import doobie._
import doobie.implicits._
import doobie.postgres.implicits._

trait UsersRepository[F[_]] {
  def add(passwordHash: String): F[User.Id]

  def get(id: User.Id): F[Option[String]]
}

object UsersRepository {

  def make[F[_]: BracketThrow](transactor: Transactor[F]): UsersRepository[F] = new UsersRepository[F] {
    override def add(passwordHash: String): F[User.Id] = insert(passwordHash).transact(transactor)

    override def get(
        id: User.Id
    ): F[Option[String]] = selectHash(id).transact(transactor)
  }

  private def insert(hash: String) =
    sql"insert into users values ($hash)".update.withUniqueGeneratedKeys[User.Id]("id")

  private def selectHash(id: User.Id) =
    sql"select hash from users where id = $id".query[String].option
}
