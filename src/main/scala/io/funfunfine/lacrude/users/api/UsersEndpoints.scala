package io.funfunfine.lacrude.users.api

import io.funfunfine.lacrude.users.User

import sttp.tapir._

object UsersEndpoints {
  def definitions: List[Endpoint[_, _, _, _]] = Nil
  val baseEndpoint: Endpoint[Unit, String, Unit, Any] = endpoint.in("users").errorOut(stringBody)

  val register: Endpoint[String, String, User.Id, Any] =
    baseEndpoint.post
      .in(stringBody.description("Пароль"))
      .out(plainBody[User.Id].description("Айди зарегистрированного пользователя."))
      .description("Регистрация, просто отправь пароль")
      .summary("Простейшая \"секурная\" регистрация")

}
