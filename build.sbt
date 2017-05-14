name := "Spotify Scala Client"

organization := "it.turingtest"

version := "0.0.2-SNAPSHOT"

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "org.scala-lang.modules" % "scala-xml_2.11" % "1.0.4",
  "com.typesafe.play" % "play-ws_2.11" % "2.5.14",
  "com.typesafe.play" % "play-json_2.11" % "2.5.14"
)
libraryDependencies ++=Seq(
  "com.typesafe.play" % "play-test_2.11" % "2.5.14",
  "org.scalatest" %% "scalatest" % "2.2.4" % "test"
)

libraryDependencies += "ch.qos.logback" % "logback-core" % "1.1.3"

publishTo := Some(Resolver.file("file", new File(Path.userHome.absolutePath+"/.m2/repository")))




