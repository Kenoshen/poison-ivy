package com.poison.ivy.build

import com.poison.ivy.task.TaskException
import net.jcazevedo.moultingyaml.{YamlValue, YamlArray, YamlString, YamlObject}

case class BuildModules(modules: BuildModule*)

object BuildModules {
  private def getString(key: String, fields: Map[YamlValue, YamlValue], moduleIndex: Int = 0, moduleName: String = ""):Option[String] = fields.get(YamlString(key)).map {
    case value:YamlString => value.value
    case x => throw new TaskException(s"Expected ${if (moduleName.nonEmpty) s"($moduleName) " else ""}modules[$moduleIndex].$key to be a string, instead found $x")
  }

  private def getPublishString(key: String, fields: Map[YamlValue, YamlValue], moduleIndex: Int = 0, moduleName: String = ""):Option[String] = fields.get(YamlString(key)).map {
    case value:YamlString => value.value
    case x => throw new TaskException(s"Expected ${if (moduleName.nonEmpty) s"($moduleName) " else ""}modules[$moduleIndex].publish.$key to be a string, instead found $x")
  }

  def apply(yaml: YamlArray, variables: BuildVariables, defaults: BuildDefaults): BuildModules = BuildModules(yaml.elements.zipWithIndex.map{
    case (yamlValue:YamlValue, index:Int) =>
      val obj = yamlValue.asYamlObject.fields
      val name = getString("name", obj, index).map(variables.populateTemplateString).getOrElse(throw new TaskException(s"modules[$index].name is missing")).replaceAll("""(?m)\s+$""", "")
      if (name.isEmpty) throw new TaskException(s"modules[$index].name cannot be empty")
      BuildModule(
        name,
        getString("group", obj, index, name).map(variables.populateTemplateString).orElse(defaults.group).getOrElse(throw new TaskException(s"modules[$index] ($name) must have a defined 'group'")),
        getString("version", obj, index, name).map(variables.populateTemplateString).orElse(defaults.version).getOrElse(throw new TaskException(s"modules[$index] ($name) must have a defined 'version'")),
        getString("path", obj, index, name).map(variables.populateTemplateString).orElse(defaults.path).getOrElse(throw new TaskException(s"modules[$index] ($name) must have a defined 'path' to the project directory")),
        getString("description", obj, index, name).map(variables.populateTemplateString).orElse(defaults.description).getOrElse(throw new TaskException(s"modules[$index] ($name) must have a defined 'description'")),
        obj.get(YamlString("dependencies")).map{
          case array:YamlArray => array.elements.map{
              case str:YamlString => str.value
              case x => throw new TaskException(s"modules[$index].dependencies ($name) must be strings, instead got $x")
            }.toSeq
          case x => throw new TaskException(s"modules[$index].dependencies ($name) must be an array of strings, instead got $x")
        }.getOrElse(throw new TaskException(s"modules[$index].dependencies ($name) must be an array of strings")),
        getString("scala", obj, index, name).map(variables.populateTemplateString).orElse(defaults.scala).getOrElse(throw new TaskException(s"modules[$index] ($name) must have an associated 'scala' version")),
        getString("source", obj, index, name).map(variables.populateTemplateString).orElse(defaults.source).getOrElse(throw new TaskException(s"modules[$index] ($name) must have an associated java 'source' version")),
        getString("target", obj, index, name).map(variables.populateTemplateString).orElse(defaults.target).getOrElse(throw new TaskException(s"modules[$index] ($name) must have an associated java 'target' version")),
        obj.get(YamlString("publish")).map{
          case publishYaml:YamlObject =>
            val publishFields = publishYaml.fields
            BuildPublish(
              getPublishString("url", publishFields, index, name).map(variables.populateTemplateString).orElse(defaults.publishUrl).getOrElse(throw new TaskException(s"modules[$index].publish ($name) must have an associated 'url' field")),
              getPublishString("repoType", publishFields, index, name).map(variables.populateTemplateString).orElse(defaults.publishRepoType).getOrElse(throw new TaskException(s"modules[$index].publish ($name) must have an associated 'repoType' field")),
              getPublishString("credentials", publishFields, index, name).map(variables.populateTemplateString).orElse(defaults.publishCredentials)
            )
        }.orElse(if ((defaults.publishUrl orElse defaults.publishRepoType orElse defaults.publishCredentials).nonEmpty) Option(BuildPublish(defaults.publishUrl.getOrElse(""), defaults.publishRepoType.getOrElse(""), defaults.publishCredentials)) else None)
      )
  }.toSeq:_*)
}

case class BuildModule(
                      name: String,
                      group: String,
                      version: String,
                      path: String,
                      description: String,
                      dependencies: Seq[String],
                      scala: String,
                      source: String,
                      target: String,
                      publish: Option[BuildPublish]
                      )

case class BuildPublish(
                       url: String,
                       repoType: String,
                       credentials: Option[String]
                       )