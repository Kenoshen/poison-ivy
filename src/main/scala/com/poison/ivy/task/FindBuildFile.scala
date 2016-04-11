package com.poison.ivy.task

import java.io.File

import scala.concurrent.Future

object FindBuildFile extends Task[Unit, File] {
  override lazy val description: String = "Try to find the build file for parsing"

  override def run(input: Unit): Future[File] = {
    val file = new File("poison.build")
    if (file.exists)
      if (!file.isDirectory)
        Future.successful(file)
      else throw new TaskException(s"The posion.build is marked as a directory, please make it a file")
    else throw new TaskException(s"Could not find poison.build file at ${file.getAbsolutePath}")
  }
}
