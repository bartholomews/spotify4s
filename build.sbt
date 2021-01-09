import Dependencies._
import sbt.Keys.{libraryDependencies, organization, parallelExecution}
import scoverage.ScoverageKeys.coverageFailOnMinimum

ThisBuild / scalaVersion := "2.13.3"
inThisBuild(
  List(
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
  )
)

val commonSettings = Seq(
  // TODO move options in a plugin
  scalacOptions += "-Ymacro-annotations", // https://github.com/circe/circe/issues/975
  // http://www.scalatest.org/user_guide/using_scalatest_with_sbt
  logBuffered in Test := false,
  parallelExecution in Test := false,
  resolvers +=
    "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
)

lazy val core = (project in file("modules/core"))
  .settings(
    commonSettings,
    name := "spotify4s-core",
    libraryDependencies ++= dependencies ++ testDependencies,
    coverageMinimum := 85,
    coverageFailOnMinimum := true
)

lazy val circe = (project in file("modules/circe"))
  .dependsOn(core % "test->test; compile->compile")
  .settings(commonSettings)
  .settings(
    name := "spotify4s-circe",
    libraryDependencies ++= circeDependencies,
    coverageMinimum := 85,
    coverageFailOnMinimum := true
  )

lazy val play = (project in file("modules/play"))
  .dependsOn(core % "test->test; compile->compile")
  .settings(commonSettings)
  .settings(
    name := "spotify4s-play",
    libraryDependencies ++= playDependencies,
    coverageMinimum := 80,
    coverageFailOnMinimum := true
  )

// https://www.scala-sbt.org/1.x/docs/Multi-Project.html
// https://stackoverflow.com/questions/11899723/how-to-turn-off-parallel-execution-of-tests-for-multi-project-builds
lazy val spotify4s = (project in file("."))
  .settings(commonSettings)
  .settings(addCommandAlias("test", ";core/test;circe/test;play/test"): _*)
  .settings(skip in publish := true)
  .aggregate(core, circe, play)

addCommandAlias("test-coverage", ";clean ;coverage ;test ;coverageReport")
addCommandAlias("test-fast", "testOnly * -- -l org.scalatest.tags.Slow")
