import sbt._

object Dependencies {
  object Versions {
    val refined = "0.9.13"
    val fsClient = "0.0.2"
    val scalaIso = "0.1.0"
  }

  val dependencies: Seq[ModuleID] = Seq(
    "eu.timepit" %% "refined" % Versions.refined,
    // https://mvnrepository.com/artifact/com.github.bartholomews/spotify-scala-client
    "io.bartholomews" %% "fsclient"  % Versions.fsClient,
    "io.bartholomews" %% "scala-iso" % Versions.scalaIso
  )

  lazy val testDependencies: Seq[ModuleID] = Seq(
    "io.bartholomews" %% "scalatestudo" % Versions.fsClient
  ).map(_ % Test)
}
