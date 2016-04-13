package com.poison.ivy.build

import com.poison.ivy.task.TaskException
import net.jcazevedo.moultingyaml.{YamlValue, YamlArray, YamlString, YamlObject}

case class BuildModules(modules: BuildModule*){

  def scrub(variables: BuildVariables, defaults: BuildDefaults, libraries: BuildLibraries): BuildModules = BuildModules(modules.map(_.scrub(variables, defaults, libraries)):_*)
}

object BuildModules {
  private def getString(key: String, fields: Map[YamlValue, YamlValue], moduleIndex: Int = 0, moduleName: String = ""):Option[String] = fields.get(YamlString(key)).map {
    case value:YamlString => value.value
    case x => throw new TaskException(s"Expected ${if (moduleName.nonEmpty) s"($moduleName) " else ""}modules[$moduleIndex].$key to be a string, instead found $x")
  }

  private def getPublishString(key: String, fields: Map[YamlValue, YamlValue], moduleIndex: Int = 0, moduleName: String = ""):Option[String] = fields.get(YamlString(key)).map {
    case value:YamlString => value.value
    case x => throw new TaskException(s"Expected ${if (moduleName.nonEmpty) s"($moduleName) " else ""}modules[$moduleIndex].publish.$key to be a string, instead found $x")
  }

  def apply(yaml: YamlArray, variables: BuildVariables, defaults: BuildDefaults, libraries: BuildLibraries): BuildModules = BuildModules(yaml.elements.zipWithIndex.map{
    case (yamlValue:YamlValue, index:Int) =>
      val obj = yamlValue.asYamlObject.fields
      val name = getString("name", obj, index).map(variables.populateTemplateString).getOrElse(throw new TaskException(s"modules[$index].name is missing")).replaceAll("""(?m)\s+$""", "")
      if (name.isEmpty) throw new TaskException(s"modules[$index].name cannot be empty")
      BuildModule(
        name,
        getString("group", obj, index, name).orElse(defaults.group).map(variables.populateTemplateString).getOrElse(throw new TaskException(s"modules[$index] ($name) must have a defined 'group'")),
        getString("version", obj, index, name).orElse(defaults.version).map(variables.populateTemplateString).getOrElse(throw new TaskException(s"modules[$index] ($name) must have a defined 'version'")),
        getString("path", obj, index, name),
        getString("description", obj, index, name),
        obj.get(YamlString("dependencies")).map{
          case array:YamlArray => (array.elements.map{
              case str:YamlString => str.value
              case x => throw new TaskException(s"modules[$index].dependencies ($name) must be strings, instead got $x")
            }.toSeq ++ defaults.dependencies).flatMap(d => {
            val libs = libraries.get(d)
            if (libs.isEmpty) throw new TaskException(s"$name at modules[$index].dependencies.$d did not exist as a library")
            else libs
          }).distinct
          case x => throw new TaskException(s"modules[$index].dependencies ($name) must be an array of strings, instead got $x")
        }.orElse(Option(defaults.dependencies)).getOrElse(throw new TaskException(s"modules[$index].dependencies ($name) must be an array of strings")),
        getString("scala", obj, index, name).orElse(defaults.scala).map(variables.populateTemplateString).getOrElse(throw new TaskException(s"modules[$index] ($name) must have an associated 'scala' version")),
        obj.get(YamlString("publish")).map{
          case publishYaml:YamlObject =>
            val publishFields = publishYaml.fields
            BuildPublish(
              getPublishString("url", publishFields, index, name).orElse(defaults.publishUrl).map(variables.populateTemplateString).getOrElse(throw new TaskException(s"modules[$index].publish ($name) must have an associated 'url' field")),
              getPublishString("repoType", publishFields, index, name).orElse(defaults.publishRepoType).map(variables.populateTemplateString).getOrElse(throw new TaskException(s"modules[$index].publish ($name) must have an associated 'repoType' field")),
              getPublishString("credentials", publishFields, index, name).orElse(defaults.publishCredentials).map(variables.populateTemplateString)
            )
        }.orElse(if ((defaults.publishUrl orElse defaults.publishRepoType orElse defaults.publishCredentials).nonEmpty) Option(BuildPublish(defaults.publishUrl.getOrElse(""), defaults.publishRepoType.getOrElse(""), defaults.publishCredentials)) else None)
      ).scrub(variables, defaults, libraries)
  }.toSeq:_*)
}

case class BuildModule(
                      name: String,
                      group: String,
                      version: String,
                      path: Option[String],
                      description: Option[String],
                      dependencies: Seq[String],
                      scala: String,
                      publish: Option[BuildPublish]
                      ){
  def scrub(variables: BuildVariables, defaults: BuildDefaults, libraries: BuildLibraries):BuildModule = BuildModule(
      variables.populateTemplateString(name),
      variables.populateTemplateString(group),
      variables.populateTemplateString(version),
      (path orElse defaults.path orElse Option(name)).map(variables.populateTemplateString),
      (description orElse defaults.description).map(variables.populateTemplateString),
      (dependencies ++ defaults.dependencies).flatMap(d => {
        val libs = libraries.get(d)
        if (libs.isEmpty) throw new TaskException(s"modules.$name.dependencies.$d did not exist as a library")
        else libs
      }).distinct,
      variables.populateTemplateString(scala),
      (publish orElse defaults.publish).map(_.scrub(variables, defaults))
    )
}

case class BuildPublish(
                       url: String,
                       repoType: String,
                       credentials: Option[String]
                       ){
  def scrub(variables: BuildVariables, defaults: BuildDefaults):BuildPublish = BuildPublish(
    variables.populateTemplateString(url),
    variables.populateTemplateString(repoType),
    (credentials orElse defaults.publishCredentials).map(variables.populateTemplateString)
  )
}