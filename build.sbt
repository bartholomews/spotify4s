import Dependencies._

name := "spotify4s"
scalaVersion := "2.13.2"
crossScalaVersions := Seq("2.12.10")

inThisBuild(List(
  organization := "io.bartholomews",
  homepage := Some(url("https://github.com/batholomews/spotify4s")),
  licenses += ("MIT", url("https://opensource.org/licenses/MIT")),
  developers := List(
    Developer(
      "bartholomews",
      "Federico Bartolomei",
      "spotify4s@bartholomews.io",
      url("https://bartholomews.io")
    )
  )
))

// TODO move options in a plugin
scalacOptions += "-Ymacro-annotations" // https://github.com/circe/circe/issues/975

libraryDependencies ++= dependencies ++ testDependencies

addCommandAlias("test-coverage", ";coverage ;test ;coverageReport")
addCommandAlias("test-fast", "testOnly * -l org.scalatest.tags.Slow")

coverageMinimum := 87.00 // FIXME
coverageFailOnMinimum := true

// http://www.scalatest.org/user_guide/using_scalatest_with_sbt
logBuffered in Test := false
parallelExecution in Test := false
