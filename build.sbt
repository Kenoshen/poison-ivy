name := "poison-ivy"

version := "0.1.0"

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "com.github.pathikrit" %% "better-files" % "2.15.0",
  "org.yaml" % "snakeyaml" % "1.16",
  "net.jcazevedo" %% "moultingyaml" % "0.2",
  "junit" % "junit" % "4.8.1" % "test",
  "org.scalatest" %% "scalatest" % "2.2.6" % "test"
)