name := "Spotify Scala Client"

organization := "it.turingtest"

version := "0.0.2"

licenses += ("MIT", url("http://opensource.org/licenses/MIT"))

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "org.scala-lang.modules" % "scala-xml_2.11" % "1.0.4",
  "com.typesafe.play" % "play-ws_2.11" % "2.5.14",
  "com.typesafe.play" % "play-json_2.11" % "2.5.14"
)

libraryDependencies += "com.vitorsvieira" %% "scala-iso" % "0.1.2"

libraryDependencies ++=Seq(
  "com.typesafe.play" % "play-test_2.11" % "2.5.14",
  "org.scalatest" %% "scalatest" % "3.0.3" % "test",
  "org.scalatestplus.play" %% "scalatestplus-play" % "2.0.0" % "test"
)

// libraryDependencies += "ch.qos.logback" % "logback-core" % "1.1.3"

/*
resolvers += Classpaths.sbtPluginReleases
resolvers += "Typesafe Repository" at "https://repo.typesafe.com/typesafe/releases/"
*/

// publishTo := Some(Resolver.file("file", new File(Path.userHome.absolutePath+"/.m2/repository")))




