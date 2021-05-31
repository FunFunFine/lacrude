package io.funfunfine.lacrude.users

import cats.Monad

import tofu.logging.LoggingCompanion
import tofu.syntax.monadic._

import io.funfunfine.lacrude.users.repository.UsersRepository

import com.dedipresta.crypto.hash.sha256.Sha256

trait UsersService[F[_]] {
  def register(password: String): F[User.Id]
  def isRegistered(password: String, id: User.Id): F[Boolean]
}

object UsersService extends LoggingCompanion[UsersService] {

  final val salt = "SALT YEAH SAFETY WOW SUCH Д Е Ф Е Н С Е"

  def hashPassword(password: String): String = Sha256.hash(password + salt).mkString

  def make[F[_]: Monad](repository: UsersRepository[F]): UsersService[F] = new UsersService[F] {

    override def register(password: String): F[User.Id] = {
      val verySafeHash = hashPassword(password)
      repository.add(verySafeHash)
    }

    override def isRegistered(
        password: String,
        id: User.Id
    ): F[Boolean] = for {
      passwordHash <- repository.get(id)
      receivedHash = hashPassword(password)
    } yield passwordHash.contains(receivedHash)
  }
}
