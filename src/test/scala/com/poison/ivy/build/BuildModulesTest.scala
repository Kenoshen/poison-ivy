package com.poison.ivy.build

import com.poison.ivy.task.TaskException
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class BuildModulesTest extends FunSuite {
  test("validate basic module"){
    val modules = BuildModules(
      BuildModule(
        "test",
        "com.test",
        "version",
        None,
        Option("Hello world"),
        Nil,
        "2.10",
        None
      )
    ).scrub(BuildVariables(), BuildDefaults(), BuildLibraries())

    assert(modules.modules.head.name == "test")
    assert(modules.modules.head.path == Option("test"))
    assert(modules.modules.head.description == Option("Hello world"))
  }

  test("validate variables in modules"){
    val modules = BuildModules(
      BuildModule(
        "test",
        "com.test",
        "$version",
        None,
        Option("Hello world"),
        Nil,
        "2.10",
        None
      )
    ).scrub(BuildVariables(BuildVariable("version", "1.2.3")), BuildDefaults(), BuildLibraries())

    assert(modules.modules.head.version == "1.2.3")
  }

  test("fail on missing variable"){
    val modules = BuildModules(
      BuildModule(
        "test",
        "com.test",
        "$version",
        None,
        Option("Hello world"),
        Nil,
        "2.10",
        None
      )
    )
    intercept[TaskException](modules.scrub(BuildVariables(), BuildDefaults(), BuildLibraries()))
  }

  test("validate defaults in modules"){
    val modules = BuildModules(
      BuildModule(
        "test",
        "com.test",
        "version",
        None,
        None,
        Nil,
        "2.10",
        None
      )
    ).scrub(BuildVariables(), BuildDefaults(description = Option("default")), BuildLibraries())

    assert(modules.modules.head.description == Option("default"))
  }

  test("use module before defaults"){
    val modules = BuildModules(
      BuildModule(
        "test",
        "com.test",
        "version",
        None,
        Option("Hello world"),
        Nil,
        "2.10",
        None
      )
    ).scrub(BuildVariables(), BuildDefaults(description = Option("default")), BuildLibraries())

    assert(modules.modules.head.description == Option("Hello world"))
  }

  test("validate libraries in modules"){
    val modules = BuildModules(
      BuildModule(
        "test",
        "com.test",
        "version",
        None,
        Option("Hello world"),
        Seq("library"),
        "2.10",
        None
      )
    ).scrub(BuildVariables(), BuildDefaults(), BuildLibraries(BuildLibrary("library", "libstring")))

    assert(modules.modules.head.dependencies.head == "libstring")
  }

  test("remove duplicate libraries"){
    val modules = BuildModules(
      BuildModule(
        "test",
        "com.test",
        "version",
        None,
        Option("Hello world"),
        Seq("library", "library"),
        "2.10",
        None
      )
    ).scrub(BuildVariables(), BuildDefaults(), BuildLibraries(BuildLibrary("library", "libstring")))

    assert(modules.modules.head.dependencies.head == "libstring")
    assert(modules.modules.head.dependencies.length == 1)
  }

  test("fail when missing library"){
    val modules = BuildModules(
      BuildModule(
        "test",
        "com.test",
        "version",
        None,
        Option("Hello world"),
        Seq("library"),
        "2.10",
        None
      )
    )
    intercept[TaskException](modules.scrub(BuildVariables(), BuildDefaults(), BuildLibraries()))
  }
}
