package com.poison.ivy.task

import net.jcazevedo.moultingyaml._

import scala.concurrent.Future

object ParseBuildFileContents extends Task[String, YamlObject] {
  override def description: String = "Parse the build file into a YAML object"

  override def run(input: String): Future[YamlObject] = try {
    val yamlAst = input.parseYaml
    Future.successful(yamlAst.asYamlObject)
  } catch {
    case e: Exception => throw new TaskException("Could not parse the build file as a yaml object", e)
  }
}
