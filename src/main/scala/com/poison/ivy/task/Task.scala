package com.poison.ivy.task

import com.poison.ivy.docs.{Timeable, Describable}

import scala.concurrent.Future

/**
  * The simplest form of action that poison-ivy can take.  Some tasks will also call other tasks and chain tasks together.
  *
  * @tparam INCOMING the input for the task
  * @tparam OUTGOING the output of the task
  */
trait Task[INCOMING, OUTGOING] extends Describable with Timeable {
  private def debugStr(error:Boolean) = println(s" ${
    if (error) Console.RED
    else Console.GREEN
  }$name: ${
    if (error) Console.RED
    else Console.WHITE
  }$description ${
    if (timing > 1000) Console.RED
    else if (timing > 250) Console.YELLOW
    else Console.MAGENTA
  }($timingAsDuration)")

  final def apply(input: INCOMING):Future[OUTGOING] = {
    var failed = false
    beginTiming
    try run(input) catch {
      case e:Exception =>
        failed = true
        stopTiming
        debugStr(failed)
        throw e
    } finally {
      if (!failed) {
        stopTiming
        debugStr(failed)
      }
    }
  }
  protected def run(input: INCOMING):Future[OUTGOING]
}
