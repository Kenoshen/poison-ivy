package com.poision.ivy.task

import java.util.concurrent.TimeUnit

import com.poison.ivy.build.BuildDefinition
import com.poison.ivy.phase.Help
import com.poison.ivy.task.{ConvertYamlIntoBuildDefinition, ParseBuildFileContents}
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

import scala.collection.mutable
import scala.concurrent.Await
import scala.concurrent.duration.Duration

@RunWith(classOf[JUnitRunner])
class HelpTaskTest extends FunSuite {
  test("help"){
    val help = new Help()
    Await.result(help(mutable.Seq()), Duration(5, TimeUnit.SECONDS))
  }
}
