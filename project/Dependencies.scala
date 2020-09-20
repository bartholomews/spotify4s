import sbt._

object Dependencies {
  object Versions {
    val refined = "0.9.13"
    val fsClient = "0.0.2+56-e168b5bc-SNAPSHOT"
    val scalaIso = "0.1.1"
  }

  val dependencies: Seq[ModuleID] = Seq(
    "eu.timepit" %% "refined" % Versions.refined,
    // https://mvnrepository.com/artifact/com.github.bartholomews/spotify-scala-client
    "io.bartholomews" %% "fsclient"  % Versions.fsClient,
    "io.bartholomews" %% "scala-iso" % Versions.scalaIso
  )

  lazy val testDependencies: Seq[ModuleID] = Seq(
    "io.bartholomews" %% "scalatestudo" % "0.0.2+4-daf79e02-SNAPSHOT"
  ).map(_ % Test)
}
