package com.poison.ivy.build

import com.poison.ivy.task.TaskException
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class BuildVariablesTest extends FunSuite {
  test("get variable values"){
    val variables = BuildVariables(
      BuildVariable("thing1", "value1"),
      BuildVariable("thing2", "value2"),
      BuildVariable("thing3", "value3"),
      BuildVariable("thing4", "value4")
    ).scrub

    assert(variables.get("thing1") == Option("value1"))
    assert(variables.get("thing2") == Option("value2"))
    assert(variables.get("thing4") == Option("value4"))
    assert(variables.get("thing5").isEmpty)
    assert(variables.get("THING1").isEmpty)
  }

  test("validate basic variable substitution"){
    val variables = BuildVariables(BuildVariable("test", "1234")).scrub

    assert(variables.populateTemplateString("this is just a test!") == "this is just a test!")
    assert(variables.populateTemplateString("this is just a ${test}!") == "this is just a 1234!")
    assert(variables.populateTemplateString("this is just a ${test}{}!") == "this is just a 1234{}!")
    assert(variables.populateTemplateString("this is just a $test!") == "this is just a 1234!")
    assert(variables.populateTemplateString("this is just a $test!yo") == "this is just a 1234!yo")
    assert(variables.populateTemplateString("this is just a $test") == "this is just a 1234")
    assert(variables.populateTemplateString("this is just a $test$test") == "this is just a 12341234")
    assert(variables.populateTemplateString("this is just a $test $test") == "this is just a 1234 1234")
  }

  test("empty variables"){
    val variables = BuildVariables().scrub

    assert(variables.get("thing").isEmpty)
    assert(variables.populateTemplateString("this is a test") == "this is a test")
    intercept[TaskException](variables.populateTemplateString("this $variable doesn't exist"))
  }

  test("variables referencing variables"){
    val variables = BuildVariables(
      BuildVariable("var1", "1"),
      BuildVariable("var2", "2"),
      BuildVariable("var3", "$var1 $var2")
    ).scrub

    assert(variables.get("var1") == Option("1"))
    assert(variables.get("var2") == Option("2"))
    assert(variables.get("var3") == Option("1 2"))
  }
}
