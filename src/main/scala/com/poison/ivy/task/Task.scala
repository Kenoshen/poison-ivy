package com.poison.ivy.task

import com.poison.ivy.docs.{Timeable, Describable}

import scala.concurrent.Future

/**
  * The simplest form of action that poison-ivy can take.  Some tasks will also call other tasks and chain tasks together.
  * @tparam INCOMING the input for the task
  * @tparam OUTGOING the output of the task
  */
trait Task[INCOMING, OUTGOING] extends Describable with Timeable {
  final def apply(input: INCOMING, debug: Boolean = false):Future[OUTGOING] = {
    beginTiming
    try run(input) finally {
      stopTiming
      if (debug) {
        println(s"$name: $description ($timingAsDuration)")
      }
    }
  }
  protected def run(input: INCOMING):Future[OUTGOING]
}
