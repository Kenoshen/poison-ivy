package com.poison.ivy.build

import com.poison.ivy.task.TaskException
import net.jcazevedo.moultingyaml._

case class BuildDefaults(
                        group: Option[String] = None,
                        version: Option[String] = None,
                        path: Option[String] = None,
                        description: Option[String] = None,
                        dependencies: Seq[String] = Nil,
                        scala: Option[String] = None,
                        publishUrl: Option[String] = None,
                        publishRepoType: Option[String] = None,
                        publishCredentials: Option[String] = None
                        ){

  lazy val publish: Option[BuildPublish] = if ((publishUrl orElse publishRepoType orElse publishCredentials).nonEmpty) Option(BuildPublish(publishUrl.orNull, publishRepoType.orNull, publishCredentials)) else None

  def scrub(variables: BuildVariables, libraries: BuildLibraries):BuildDefaults = BuildDefaults(
    group.map(variables.populateTemplateString),
    version.map(variables.populateTemplateString),
    path.map(variables.populateTemplateString),
    description.map(variables.populateTemplateString),
    dependencies.map(dep => if (libraries.get(dep).isEmpty) throw new TaskException(s"Defaults cannot reference a non-existent library ($dep)") else dep),
    scala.map(variables.populateTemplateString),
    publishUrl.map(variables.populateTemplateString),
    publishRepoType.map(variables.populateTemplateString),
    publishCredentials.map(variables.populateTemplateString)
  )
}


object BuildDefaults {
  private def getString(key: String, fields: Map[YamlValue, YamlValue]):Option[String] = fields.get(YamlString(key)).map {
    case value:YamlString => value.value
    case value:YamlNumber[Double] => value.prettyPrint
    case value:YamlNumber[Long] => value.prettyPrint
    case x => throw new TaskException(s"Expected defaults.$key to be a string, instead found $x")
  }

  private def getPublishString(key: String, fields: Map[YamlValue, YamlValue]):Option[String] = fields.get(YamlString(key)).map {
    case value:YamlString => value.value
    case x => throw new TaskException(s"Expected defaults.publish.$key to be a string, instead found $x")
  }

  def apply(yaml: YamlObject, variables: BuildVariables, libraries: BuildLibraries): BuildDefaults = {
    val obj = yaml.fields
    val publishObj = obj.get(YamlString("publish")).map(_.asYamlObject.fields)
    BuildDefaults(
      getString("group", obj),
      getString("version", obj),
      getString("path", obj),
      getString("description", obj),
      obj.get(YamlString("dependencies")).map{
        case array:YamlArray => array.elements.map{
          case str:YamlString => str.value
          case x => throw new TaskException(s"Expected defaults.dependencies to contain an array of strings, instead found $x")
        }.toSeq
        case x => throw new TaskException(s"Expected defaults.dependencies to be an array of strings, instead found $x")
      }.getOrElse(Nil),
      getString("scala", obj),
      publishObj.flatMap(p => getPublishString("url", p)),
      publishObj.flatMap(p => getPublishString("repoType", p)),
      publishObj.flatMap(p => getPublishString("credentials", p))
    ).scrub(variables, libraries)
  }
}