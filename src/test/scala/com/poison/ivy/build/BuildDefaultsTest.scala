package com.poison.ivy.build

import com.poison.ivy.task.TaskException
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class BuildDefaultsTest extends FunSuite {
  test("validate libraries within defaults"){
    BuildDefaults(
      Option("group"),
      Option("version"),
      Option("path"),
      Option("description"),
      Seq("dependency"),
      Option("scalaversion"),
      Option("publishUrl"),
      Option("publishRepoType"),
      Option("publishCredentials")
    ).scrub(BuildVariables(), BuildLibraries(Nil, BuildLibrary("dependency", "dep")))
  }

  test("fail on missing library"){
    intercept[TaskException](BuildDefaults(
      Option("group"),
      Option("version"),
      Option("path"),
      Option("description"),
      Seq("dependency"),
      Option("scalaversion"),
      Option("publishUrl"),
      Option("publishRepoType"),
      Option("publishCredentials")
    ).scrub(BuildVariables(), BuildLibraries(Nil)))
  }

  test("validate variables within defaults"){
    val defaults = BuildDefaults(
      Option("group"),
      Option("$version"),
      Option("path"),
      Option("description"),
      Seq("dependency"),
      Option("scalaversion"),
      Option("publishUrl"),
      Option("publishRepoType"),
      Option("publishCredentials")
    ).scrub(BuildVariables(BuildVariable("version", "1.2.3")), BuildLibraries(Nil, BuildLibrary("dependency", "dep")))

    assert(defaults.version.get == "1.2.3")
  }

  test("fail on missing variable"){
    intercept[TaskException](BuildDefaults(
      Option("group"),
      Option("$version"),
      Option("path"),
      Option("description"),
      Seq("dependency"),
      Option("scalaversion"),
      Option("publishUrl"),
      Option("publishRepoType"),
      Option("publishCredentials")
    ).scrub(BuildVariables(), BuildLibraries(Nil)))
  }
}
