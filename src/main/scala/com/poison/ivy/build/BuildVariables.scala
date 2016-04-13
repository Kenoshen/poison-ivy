package com.poison.ivy.build

import com.poison.ivy.task.TaskException
import net.jcazevedo.moultingyaml.{YamlString, YamlObject}

case class BuildVariables(variables: BuildVariable*){
  protected final val TEMPLATE_REGEX = """(\$\{(.*?)\})|(\$(.*?)[:"'|{}+=!@#$%^`~&*;,.<>?\/\\\[\]\(\)\n\s])|(\$(.*?)$)""".r

  def get(variableName: String): Option[String] = variables.find(_.name == variableName).map(_.value)
  def populateTemplateString(templateString: String): String = {
    val matches = TEMPLATE_REGEX.findAllMatchIn(templateString)
    var populated = templateString
    matches.foreach(m => {
      if (m.group(0).nonEmpty) {
        val matcher = if (m.group(1) != null) m.group(1) else if(m.group(3) != null) m.group(3).dropRight(1) else m.group(5)
        val key = if (m.group(2) != null) m.group(2) else if (m.group(4) != null) m.group(4) else m.group(6)
        populated = populated.replaceAllLiterally(matcher, get(key).getOrElse(throw new TaskException(s"There is no variable named '$key', found in '$templateString'")))
      }
    })
    populated
  }

  /**
    * Calling the scrub method is the only way to populate BuildVariables with values from other build variables.
    *
    * @example
    * <pre>
    * var1 = foo
    * var2 = bar
    * var3 = ${var1} and ${var2}
    * </pre>
    * <b>In order for var3 to equal 'foo and bar' you must use a scrubbed BuildVariables object</b>
    * @return A scrubbed clone of this object with variables referencing other variables filled in
    */
  def scrub:BuildVariables = BuildVariables(variables.map(v => BuildVariable(v.name, populateTemplateString(v.value))):_*)
}

object BuildVariables {
  def apply(yaml: YamlObject): BuildVariables = BuildVariables(yaml.fields.map {
      case (key: YamlString, value: YamlString) => BuildVariable(key.value, value.value)
      case (key, value) => throw new TaskException(s"Expected (string: string), instead got ($key: $value)")
    }.toSeq:_*).scrub
}

case class BuildVariable(name: String, value: String)