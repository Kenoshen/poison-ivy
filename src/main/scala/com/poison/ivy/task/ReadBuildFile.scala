package com.poison.ivy.task

import better.files._

import scala.concurrent.Future

object ReadBuildFile extends Task[File, String] {
  override lazy val description: String = "Read the build file as a string"

  override def run(input: File): Future[String] = try Future.successful(input.contentAsString) catch {
    case e:Exception => throw new TaskException("Could not read contents from build file", e)
  }
}
