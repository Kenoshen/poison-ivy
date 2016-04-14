package com.poision.ivy.task

import com.poison.ivy.task.Task
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

import scala.concurrent.Future

class TestingDocumentationTask extends Task[Unit, Unit] {
  override val description: String = "Just for testing the documentation"

  override protected def run(input: Unit): Future[Unit] = ???
}

@RunWith(classOf[JUnitRunner])
class DocumentTaskTest extends FunSuite {
  test("check that the documentation is populated as expected") {
    val task = new TestingDocumentationTask()

    assert(task.name == "TestingDocumentationTask")
    assert(task.description == "Just for testing the documentation")
  }

}
