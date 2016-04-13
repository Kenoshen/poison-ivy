package com.poison.ivy.task

import scala.concurrent.Future

import scala.concurrent.ExecutionContext.Implicits.global

object Compile extends TaskGroup[String] {
  override def description: String = "Compile all of the relevant source files"

  override protected def run(input: Unit): Future[String] = for {
    buildFile <- FindBuildFile()
    buildFileContents <- ReadBuildFile(buildFile)
    yamlConfigObject <- ParseBuildFileContents(buildFileContents)
  } yield "/location/of/newly/compiled/files"
}
