import sbt._

object Dependencies {
  object Versions {
    // https://github.com/typelevel/cats
    val cats = "2.6.0"
    // https://github.com/fthomas/refined
    val refined = "0.9.24"
    // https://github.com/bartholomews/fsclient
    val fsClient = "0.1.2+10-69b86071-SNAPSHOT"
    // https://github.com/bartholomews/scala-iso
    val scalaIso = "0.1.3"
  }

  val circeDependencies: Seq[ModuleID] = Seq(
    "io.bartholomews" %% "fsclient-circe" % Versions.fsClient
  )

  val playDependencies: Seq[ModuleID] = Seq(
    "io.bartholomews" %% "fsclient-play" % Versions.fsClient
  )

  val dependencies: Seq[ModuleID] = Seq(
    "io.bartholomews" %% "fsclient-core" % Versions.fsClient,
    "org.typelevel"   %% "cats-core"     % Versions.cats,
    "eu.timepit"      %% "refined"       % Versions.refined,
    "io.bartholomews" %% "scala-iso"     % Versions.scalaIso
  )

  lazy val testDependencies: Seq[ModuleID] = Seq(
    "io.bartholomews" %% "scalatestudo" % Versions.fsClient
  ).map(_ % Test)
}
