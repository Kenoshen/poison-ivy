package com.poision.ivy.task

import java.util.concurrent.TimeUnit

import com.poison.ivy.phase.Help
import com.poison.ivy.task.{TaskException, Task}
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

import scala.collection.mutable
import scala.concurrent.{Future, Await}
import scala.concurrent.duration.Duration

@RunWith(classOf[JUnitRunner])
class TaskExceptionTest extends FunSuite {
  test("task exception"){
    val fail = new TestingTaskException()
    intercept[TaskException](fail())
  }
}

class TestingTaskException extends Task[Unit, Unit] {
  override def description: String = "Just for testing exception handling"

  override protected def run(input: Unit): Future[Unit] = throw new TaskException("This is suppose to fail")
}
