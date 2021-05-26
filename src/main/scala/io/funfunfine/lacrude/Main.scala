package io.funfunfine.lacrude

import cats.effect.{ExitCode, IO, IOApp}

object Main extends IOApp {
  def run(args: List[String]) =
    LacrudeServer.resource[IO].use(_ => IO.never).as(ExitCode.Success)
}
