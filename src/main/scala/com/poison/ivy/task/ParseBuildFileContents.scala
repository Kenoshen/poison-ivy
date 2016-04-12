package com.poison.ivy.task

import org.yaml.snakeyaml.Yaml

import scala.concurrent.Future

object ParseBuildFileContents extends Task[String, Yaml] {
  override def description: String = "Parse the build file into a YAML object"

  override def run(input: String): Future[Yaml] = ???
}
