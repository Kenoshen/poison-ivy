package com.poison.ivy.build

import com.poison.ivy.task.TaskException
import net.jcazevedo.moultingyaml.{YamlObject, YamlArray, YamlString, YamlValue}

case class BuildDefaults(
                        group: Option[String],
                        version: Option[String],
                        path: Option[String],
                        description: Option[String],
                        dependencies: Seq[String],
                        scala: Option[String],
                        source: Option[String],
                        target: Option[String],
                        publishUrl: Option[String],
                        publishRepoType: Option[String],
                        publishCredentials: Option[String]
                        )


object BuildDefaults {
  private def getString(key: String, fields: Map[YamlValue, YamlValue]):Option[String] = fields.get(YamlString(key)).map {
    case value:YamlString => value.value
    case x => throw new TaskException(s"Expected defaults.$key to be a string, instead found $x")
  }

  private def getPublishString(key: String, fields: Map[YamlValue, YamlValue]):Option[String] = fields.get(YamlString(key)).map {
    case value:YamlString => value.value
    case x => throw new TaskException(s"Expected defaults.publish.$key to be a string, instead found $x")
  }

  def apply(yaml: YamlObject, variables: BuildVariables): BuildDefaults = {
    val obj = yaml.fields
    val publishObj = obj.get(YamlString("publish")).map(_.asYamlObject.fields)
    BuildDefaults(
      getString("group", obj).map(variables.populateTemplateString),
      getString("version", obj).map(variables.populateTemplateString),
      getString("path", obj).map(variables.populateTemplateString),
      getString("description", obj).map(variables.populateTemplateString),
      obj.get(YamlString("dependencies")).map{
        case array:YamlArray => array.elements.map{
          case str:YamlString => str.value
          case x => throw new TaskException(s"Expected defaults.dependencies to contain an array of strings, instead found $x")
        }.toSeq
        case x => throw new TaskException(s"Expected defaults.dependencies to be an array of strings, instead found $x")
      }.getOrElse(Nil),
      getString("scala", obj).map(variables.populateTemplateString),
      getString("source", obj).map(variables.populateTemplateString),
      getString("target", obj).map(variables.populateTemplateString),
      publishObj.flatMap(p => getPublishString("url", p).map(variables.populateTemplateString)),
      publishObj.flatMap(p => getPublishString("repoType", p).map(variables.populateTemplateString)),
      publishObj.flatMap(p => getPublishString("credentials", p).map(variables.populateTemplateString))
    )
  }
}