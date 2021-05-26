package io.funfunfine.lacrude

import cats.effect.ExitCode
import cats.effect.IO
import cats.effect.IOApp

object Main extends IOApp {

  def run(args: List[String]) =
    LacrudeServer.stream[IO].compile.drain.as(ExitCode.Success)
}
