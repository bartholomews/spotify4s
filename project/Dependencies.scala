import sbt._

object Dependencies {
  object Versions {
    val cats = "2.3.0"
    val refined = "0.9.13"
    val fsClient = "0.1.0+2-f6fce5d9-SNAPSHOT"
    val scalaIso = "0.1.1+4-0b19443b-SNAPSHOT"
    // https://github.com/softwaremill/sttp/releases
    // https://search.maven.org/search?q=sttp%20softwaremill%20play-json
    val sttp = "3.0.0-RC13"
  }

  val circeDependencies: Seq[ModuleID] = Seq(
    "io.bartholomews" %% "fsclient-circe" % Versions.fsClient
  )

  val playDependencies: Seq[ModuleID] = Seq(
    "com.softwaremill.sttp.client3" %% "play-json"            % Versions.sttp,
    "com.beachape"                  %% "enumeratum-play-json" % "1.6.1"
//    "io.bartholomews" %% "fsclient-play" % Versions.fsClient
  )

  val dependencies: Seq[ModuleID] = Seq(
    "org.typelevel" %% "cats-core" % Versions.cats,
    "eu.timepit"    %% "refined"   % Versions.refined,
    // https://mvnrepository.com/artifact/com.github.bartholomews/spotify-scala-client
    "io.bartholomews" %% "fsclient-core" % Versions.fsClient,
    "io.bartholomews" %% "scala-iso"     % Versions.scalaIso
  )

  lazy val testDependencies: Seq[ModuleID] = Seq(
    "io.bartholomews" %% "scalatestudo" % "0.0.3+2-4ad35c91-SNAPSHOT"
  ).map(_ % Test)
}
