import Dependencies._

name := "spotify4s"

organization := "io.bartholomews"

version := "0.1.0-SNAPSHOT"

licenses += ("MIT", url("http://opensource.org/licenses/MIT"))

scalaVersion := "2.13.2"
crossScalaVersions := Seq("2.12.10")

// TODO move options in a plugin
scalacOptions += "-Ymacro-annotations" // https://github.com/circe/circe/issues/975

resolvers += "Sonatype OSS Snapshots".at("https://oss.sonatype.org/content/repositories/snapshots")

libraryDependencies ++= dependencies ++ testDependencies

addCommandAlias("test-coverage", ";coverage ;test ;coverageReport")
addCommandAlias("test-fast", "testOnly * -l org.scalatest.tags.Slow")

coverageMinimum := 86.89 // FIXME
coverageFailOnMinimum := true

// http://www.scalatest.org/user_guide/using_scalatest_with_sbt
logBuffered in Test := false
parallelExecution in Test := false
