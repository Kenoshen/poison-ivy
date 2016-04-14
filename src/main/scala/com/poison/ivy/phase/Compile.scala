package com.poison.ivy.phase

import com.poison.ivy.flag.Flag
import com.poison.ivy.task.{FindBuildFile, ParseBuildFileContents, ReadBuildFile}

import scala.collection.mutable
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class Compile extends Phase {
  override def description: String = "Compile all of the relevant source files"

  override protected def run(input: mutable.Seq[Flag]): Future[Unit] = for {
    buildFile <- new FindBuildFile()()
    buildFileContents <- new ReadBuildFile()(buildFile)
    yamlConfigObject <- new ParseBuildFileContents()(buildFileContents)
  } yield "/location/of/newly/compiled/files"
}
