package com.poison.ivy.task

import com.poison.ivy.build.{BuildLibraries, BuildDefinition}
import net.jcazevedo.moultingyaml._

import scala.concurrent.Future

object ConvertYamlIntoBuildDefinition extends Task[YamlObject, BuildDefinition] {
  override def description: String = "Convert the yaml object into the build definition"

  override def run(input: YamlObject): Future[BuildDefinition] = {
    val fields = input.fields
    ???
  }
}
