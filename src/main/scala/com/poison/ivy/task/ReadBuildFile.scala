package com.poison.ivy.task

import java.io.File

import scala.concurrent.Future

object ReadBuildFile extends Task[File, String] {
  override lazy val description: String = "Read the file as a string"

  override def run(input: File): Future[String] = {
    val source = scala.io.Source.fromFile(input)
    try Future.successful(source.mkString)
    catch {
      case e:Exception => throw new TaskException("Could not read from file", e)
    }
    finally source.close
  }
}
