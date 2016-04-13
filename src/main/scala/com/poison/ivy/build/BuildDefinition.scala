package com.poison.ivy.build

import net.jcazevedo.moultingyaml.{YamlString, YamlObject}

case class BuildDefinition(libraries: BuildLibraries, variables: BuildVariables, defaults: BuildDefaults, modules: BuildModules) {

}


object BuildDefinition {
  def apply(yamlObj: YamlObject): BuildDefinition = {
    ???

  }
}