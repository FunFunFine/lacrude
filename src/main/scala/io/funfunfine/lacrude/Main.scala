package io.funfunfine.lacrude

import zio.ExitCode
import zio.RIO
import zio.Task
import zio.interop.catz._
import zio.interop.catz.implicits._

import tofu.zioInstances.implicits._

import io.funfunfine.lacrude.announcement.api.RequestContext

object Main extends CatsApp {

  def run(args: List[String]) =
    LacrudeServer
      .resource[Task, RIO[RequestContext, *]]
      .flatMap(
        _.use(
          _ => Task.never
        )
      )
      .catchAllCause(
        error => console.putStrLn(s"Error on startup: $error")
      )
      .as(ExitCode.success)
}
