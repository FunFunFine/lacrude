package io.funfunfine.lacrude

import cats.effect.ExitCode
import cats.effect.IO
import cats.effect.IOApp

object Main extends IOApp {

  def run(args: List[String]) =
    LacrudeServer
      .resource[IO]
      .use(
        _ => IO.never
      )
      .as(ExitCode.Success)
}
