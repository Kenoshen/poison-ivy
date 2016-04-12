package com.poison.ivy.task

import scala.concurrent.Future

object ParseBuildFileContents extends Task[String, String] {
  override def description: String = "Parse the build file into a YAML object"

  override def run(input: String): Future[String] = ??? // TODO: MW make this a YAML object
}
