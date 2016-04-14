package com.poision.ivy.task

import java.util.concurrent.TimeUnit

import com.poison.ivy.build.BuildDefinition
import com.poison.ivy.phase.Help
import com.poison.ivy.task.{ParseBuildFileContents, ConvertYamlIntoBuildDefinition}
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

import scala.collection.mutable
import scala.concurrent.Await
import scala.concurrent.duration.Duration

import scala.concurrent.ExecutionContext.Implicits.global


@RunWith(classOf[JUnitRunner])
class ConvertYamlIntoBuildDefinitionTest extends FunSuite {
  test("parse the docs/poison.yml file into a build definition"){
    val definition:BuildDefinition = Await.result(for {
      yamlObj <- new ParseBuildFileContents()(yamlBuildDefinition)
      definition <- new ConvertYamlIntoBuildDefinition()(yamlObj)
    } yield definition, Duration(5, TimeUnit.SECONDS))

    assert(definition.libraries.get("play-json").head == "com.play:play-json::2.3.3")
    assert(definition.modules.modules.head.dependencies.head == "--internal:groucho-model")
    assert(definition.modules.modules.last.path.get == "groucho-model")
  }

  val yamlBuildDefinition =
    """
      |---
      |
      |variables:
      |  play-version: 2.3.3
      |
      |defaults:
      |  group: com.twc.groucho
      |  version: ${githash}-$date
      |  package: jar
      |  publish:
      |    type: mvn
      |    credentials: $user-home/.credentials/twc.artifactory.eg
      |    url: "http://eg-jenkins.analytics.cloud.twc.net:8081/artifactory/libs-release-local"
      |  source: 1.8
      |  target: 1.8
      |  scala: 2.10
      |
      |libraries:
      |  logback: "org.logback:logback:1.2.3"
      |  json: "org.json:json:1.2.3"
      |  junit: "org.junit:junit:1.2.3:test"
      |  xml: "org.xml:xml:1.2.3"
      |  play-json: "com.play:play-json::$play-version"
      |  play-xml: "com.play:play-xml::$play-version"
      |  play-yaml: "com.play:play-yaml::$play-version"
      |  common: [logback, junit, json, xml]
      |
      |modules:
      |  - name: groucho
      |    path: groucho/
      |    description: A cool web app that gives a detailed look at Event Gateway yay!
      |    dependencies:
      |      - groucho-model
      |      - common
      |      - play-json
      |      - play-xml
      |      - play-yaml
      |    package: zip
      |
      |  - name: groucho-model
      |    dependencies:
      |      - common
      |
    """.stripMargin
}
