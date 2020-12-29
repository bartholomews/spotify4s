import sbt._

object Dependencies {
  object Versions {
    val refined = "0.9.13"
    val fsClient = "0.1.0"
    val scalaIso = "0.1.1+3-191bbc64-SNAPSHOT"
    // https://github.com/softwaremill/sttp/releases
    val sttp = "2.2.9"
  }

  lazy val sttp = Seq(
    "com.softwaremill.sttp.client" %% "core" % Versions.sttp,
    "com.softwaremill.sttp.client" %% "circe" % Versions.sttp
  )

  val dependencies: Seq[ModuleID] = Seq(
    "eu.timepit" %% "refined" % Versions.refined,
    // https://mvnrepository.com/artifact/com.github.bartholomews/spotify-scala-client
    "io.bartholomews" %% "fsclient"  % Versions.fsClient,
    "io.bartholomews" %% "scala-iso" % Versions.scalaIso
  ) ++ sttp

  lazy val testDependencies: Seq[ModuleID] = Seq(
    "io.bartholomews" %% "scalatestudo" % "0.0.2+10-e954ea69-SNAPSHOT"
  ).map(_ % Test)
}
