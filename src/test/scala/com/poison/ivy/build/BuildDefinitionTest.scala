package com.poison.ivy.build

import com.poison.ivy.task.TaskException
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class BuildDefinitionTest extends FunSuite {
  test("validate basic build definition"){
    val definition = BuildDefinition(
      BuildVariables(BuildVariable("version", "1.2.3"), BuildVariable("SECOND_PATH", "foobar$version")),
      BuildLibraries(Seq("first", "second"), BuildLibrary("library", "yay"), BuildLibrary("lib", Seq("library"))),
      BuildDefaults(group = Option("com.group"), description = Option("hello world"), dependencies = Seq("lib")),
      BuildModules(
        BuildModule(
          "first",
          "first.group",
          "$version",
          None,
          None,
          Seq("library"),
          "2.10",
          None
        ),
        BuildModule(
          "second",
          "second.group",
          "$version",
          Option("$SECOND_PATH"),
          Option("goodbye"),
          Nil,
          "2.11",
          Option(BuildPublish("localhost:9200", "mvn", None))
        )
      )
    ).scrub

    assert(definition.modules.modules.head.version == "1.2.3")
    assert(definition.modules.modules.head.description.get == "hello world")
    assert(definition.modules.modules.head.dependencies.head == "yay")
    assert(definition.modules.modules.head.dependencies.length == 1)

    assert(definition.modules.modules.last.version == "1.2.3")
    assert(definition.modules.modules.last.path.get == "foobar1.2.3")
    assert(definition.modules.modules.last.description.get == "goodbye")
    assert(definition.modules.modules.last.dependencies.head == "yay")
    assert(definition.modules.modules.last.dependencies.length == 1)
    assert(definition.modules.modules.last.publish.get.url == "localhost:9200")
  }
}
