package com.poison.ivy.build

import com.poison.ivy.task.TaskException
import net.jcazevedo.moultingyaml.{YamlArray, YamlString, YamlObject}

case class BuildDefinition(variables: BuildVariables, libraries: BuildLibraries, defaults: BuildDefaults, modules: BuildModules) {
  def scrub:BuildDefinition = {
    val scrubbedVariables = variables.scrub
    val scrubbedLibraries = libraries.scrub(scrubbedVariables)
    val scrubbedDefaults = defaults.scrub(scrubbedVariables, scrubbedLibraries)
    val scrubbedModules = modules.scrub(scrubbedVariables, scrubbedDefaults, scrubbedLibraries)
    BuildDefinition(scrubbedVariables, scrubbedLibraries, scrubbedDefaults, scrubbedModules)
  }
}


object BuildDefinition {
  def apply(yamlObj: YamlObject): BuildDefinition = {
    val obj = yamlObj.fields
    val variables:BuildVariables = obj.get(YamlString("variables")).map(y => BuildVariables(y.asYamlObject)).getOrElse(BuildVariables())
    val libraries:BuildLibraries = obj.get(YamlString("libraries")).map(y => BuildLibraries(y.asYamlObject, variables)).getOrElse(BuildLibraries())
    val defaults:BuildDefaults = obj.get(YamlString("defaults")).map(y => BuildDefaults(y.asYamlObject, variables, libraries)).getOrElse(BuildDefaults())
    val modules:BuildModules = obj.get(YamlString("modules")).map {
      case y:YamlArray => BuildModules(y, variables, defaults, libraries)
      case x => throw new TaskException("'modules' must be an array of modules")
    }.getOrElse(BuildModules())
    BuildDefinition(variables, libraries, defaults, modules)
  }
}