name := "Spotify Scala Client"

organization := "it.turingtest"

version := "0.0.1-SNAPSHOT"

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "com.typesafe.play" % "play-ws_2.11" % "2.5.14",
  "com.typesafe.play" % "play-json_2.11" % "2.5.14",
  "org.scalatest" %% "scalatest" % "2.2.4" % "test"
)

publishTo := Some(Resolver.file("file", new File(Path.userHome.absolutePath+"/.m2/repository")))




