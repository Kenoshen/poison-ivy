package com.poison.ivy.build

import com.poison.ivy.task.TaskException
import net.jcazevedo.moultingyaml.{YamlArray, YamlValue, YamlString, YamlObject}

case class BuildLibraries(moduleNames: Seq[String], libraries: BuildLibrary*){
  if (moduleNames.distinct.length != moduleNames.length) throw new TaskException(s"Module names must be unique, found duplicate module names (${moduleNames.diff(moduleNames.distinct).mkString(",")})")

  libraries.foreach(l => {
    get(l.name) // validate that no group references another group (avoids circular dependencies)
    if (l.isGroup){
      if (l.group.isEmpty) throw new TaskException(s"A library group (${l.name}) cannot be an empty list")
    } else {
      if (l.lib.getOrElse("").isEmpty) throw new TaskException(s"A library (${l.name}) cannot be empty")
    }
  })

  def get(name:String):Seq[String] = libraries.find(l => l.name == name || l.lib.orNull == name).map(bl => {
    if (bl.isGroup) bl.group.map(libName => subGet(libName, bl.name))
    else bl.group
  }).orElse(moduleNames.find(m => m == name || s"${BuildLibraries.MODULE_PREFIX}$m" == name).map(m => Seq(s"${BuildLibraries.MODULE_PREFIX}$m"))).getOrElse(Nil)

  private def subGet(name:String, groupName: String):String = libraries.find(_.name == name).flatMap(bl => {
    if (bl.isGroup) throw new TaskException(s"A library group ($groupName) cannot reference another library group ($name)")
    else bl.lib
  }).orElse(moduleNames.find(m => m == name || s"${BuildLibraries.MODULE_PREFIX}$m" == name).map(m => s"${BuildLibraries.MODULE_PREFIX}$m")).getOrElse(throw new TaskException(s"A library group ($groupName) is referencing a library that does not exist ($name"))

  def scrub(variables: BuildVariables): BuildLibraries = BuildLibraries(moduleNames, libraries.map(l => {
    if (l.isGroup) l
    else BuildLibrary(l.name, variables.populateTemplateString(l.lib.getOrElse("")))
  }):_*)
}

object BuildLibraries {
  protected final lazy val MODULE_PREFIX = "--internal:"

  def apply(yaml: YamlObject, variables: BuildVariables, moduleNames: Seq[String]): BuildLibraries = BuildLibraries(moduleNames, yaml.fields.map {
    case (key: YamlString, lib: YamlString) => BuildLibrary(key.value, variables.populateTemplateString(lib.value))
    case (key: YamlString, group: YamlArray) => BuildLibrary(key.value, group.elements.map {
      case libName: YamlString => libName.value
      case x => throw new TaskException(s"Expected a string, instead found $x")
    }.toSeq)
    case x => throw new TaskException(s"Expected a (string, string) or a (string, array), instead found $x")
  }.toSeq:_*)
}

case class BuildLibrary(name: String, group: Seq[String], isGroup:Boolean = true){
  val lib:Option[String] = if (isGroup) None else group.headOption
  if (isGroup && group.contains(name)) throw new TaskException("A library group cannot reference itself or other library groups")
}

object BuildLibrary {
  def apply(name: String, lib: String):BuildLibrary = BuildLibrary(name, Seq(lib), isGroup = false)
}