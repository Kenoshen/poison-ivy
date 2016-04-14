package com.poison.ivy.build

import com.poison.ivy.task.TaskException
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class BuildLibrariesTest extends FunSuite {
  test("validate basic library construction"){
    val libraries = BuildLibraries(Nil,
      BuildLibrary("lib1", "libstring1"),
      BuildLibrary("lib2", "libstring2"),
      BuildLibrary("lib3", Seq("lib1", "lib2"))
    )

    assert(libraries.get("lib1").head == "libstring1")
    assert(libraries.get("lib2").head == "libstring2")
    assert(libraries.get("lib3") == Seq("libstring1", "libstring2"))
  }

  test("library should return nil on missing library name"){
    val libraries = BuildLibraries(Nil)

    assert(libraries.get("lib1") == Nil)
  }

  test("library should fail on missing reference in group"){
    intercept[TaskException](BuildLibraries(Nil,
      BuildLibrary("lib3", Seq("lib1", "lib2"))
    ))
  }

  test("library should fail on group referencing group"){
    intercept[TaskException](BuildLibraries(Nil,
      BuildLibrary("lib1", "thing"),
      BuildLibrary("lib2", Seq("lib1")),
      BuildLibrary("lib3", Seq("lib1", "lib2"))
    ))
  }

  test("variables should populate library values"){
    val variables = BuildVariables(
      BuildVariable("version", "1.2.3")
    )
    val libraries = BuildLibraries(Nil,
      BuildLibrary("lib1", "group:name:$version")
    ).scrub(variables)

    assert(libraries.get("lib1").head == "group:name:1.2.3")
  }

  test("fail when duplicate module names"){
    intercept[TaskException](BuildLibraries(Seq("first", "second", "third", "first", "third")))
  }
}
