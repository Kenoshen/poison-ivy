package com.poison.ivy.task

import scala.concurrent.Future

object Compile extends Task[Unit, String] {
  override def description: String = "Compile all of the relevant source files"

  override def run(input: Unit): Future[String] = for {
    buildFile <- FindBuildFile()
    buildFileContents <- ReadBuildFile(buildFile)
    yamlConfigObject <- ParseBuildFileContents(buildFileContents)
  } yield "/location/of/newly/compiled/files"
}
