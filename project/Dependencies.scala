import sbt._

object Dependencies {
  object Versions {
    val cats = "2.3.0"
    val refined = "0.9.13"
    val fsClient = "0.1.0+13-21a52034-SNAPSHOT"
    val scalaIso = "0.1.1+4-0b19443b-SNAPSHOT"
  }

  val circeDependencies: Seq[ModuleID] = Seq(
    "io.bartholomews" %% "fsclient-circe" % Versions.fsClient
  )

  val playDependencies: Seq[ModuleID] = Seq(
    "io.bartholomews" %% "fsclient-play" % Versions.fsClient
  )

  val dependencies: Seq[ModuleID] = Seq(
    "io.bartholomews" %% "fsclient-core" % Versions.fsClient,
    "org.typelevel"   %% "cats-core" % Versions.cats,
    "eu.timepit"      %% "refined"   % Versions.refined,
    "io.bartholomews" %% "scala-iso" % Versions.scalaIso
  )

  lazy val testDependencies: Seq[ModuleID] = Seq(
    "io.bartholomews" %% "scalatestudo" % "0.0.3+2-4ad35c91-SNAPSHOT"
  ).map(_ % Test)
}
