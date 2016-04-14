package com.poision.ivy.task

import java.util.concurrent.TimeUnit

import com.poison.ivy.task.Task
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}


class TestingTheTimerTask extends Task[Int, Unit] {
  override def description: String = "Just for testing the timer functions"

  override protected def run(input: Int): Future[Unit] = {
    println(s"Sleep for $input milliseconds")
    Thread.sleep(input)
    println("Done sleeping")
    Future.successful()
  }
}


@RunWith(classOf[JUnitRunner])
class TimerTaskTest extends FunSuite {
  test("check that the timer in tasks works as expected") {
    val timeStart = System.currentTimeMillis()
    val task = new TestingTheTimerTask()
    Await.result(task(1000), Duration(5, TimeUnit.SECONDS))
    val totalTime = System.currentTimeMillis() - timeStart

    assert(totalTime / 10 == task.timing / 10) // round down so that in case they are a few milliseconds off
  }
}
