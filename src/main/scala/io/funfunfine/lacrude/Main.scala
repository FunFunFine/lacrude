package io.funfunfine.lacrude

import cats.effect.ExitCode
import cats.effect.IO
import cats.effect.IOApp
import tofu.syntax.console._

object Main extends IOApp {

  def run(args: List[String]) =
    LacrudeServer
      .resource[IO]
      .flatMap(
        _.use(
          _ => IO.never
        )
      )
      .handleErrorWith(
        error => putErrLn[IO](s"Error on startup: $error")
      )
      .as(ExitCode.Success)
}
