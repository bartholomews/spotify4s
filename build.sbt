import Dependencies._
import sbt.Keys.organization

name := "spotify4s"
scalaVersion := "2.13.2"

lazy val root = (project in file("."))
  .settings(TestSettings())
  .settings(
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
    ),
  )

// TODO move options in a plugin
scalacOptions += "-Ymacro-annotations" // https://github.com/circe/circe/issues/975

libraryDependencies ++= dependencies ++ testDependencies

coverageMinimum := 90 // FIXME
coverageFailOnMinimum := true

// http://www.scalatest.org/user_guide/using_scalatest_with_sbt
logBuffered in Test := false
parallelExecution in Test := false

resolvers +=
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
