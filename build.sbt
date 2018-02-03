name := "Spotify Scala Client"

organization := "it.turingtest"

version := "0.0.4"

licenses += ("MIT", url("http://opensource.org/licenses/MIT"))

scalaVersion := "2.12.4"

//resolvers += Classpaths.sbtPluginReleases
//resolvers += "Typesafe Repository" at "https://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-json" % "2.6.8",
  "com.typesafe.play" %% "play-ws" % "2.6.11",
  "org.scala-lang.modules" %% "scala-xml" % "1.0.6"
)

libraryDependencies += "com.vitorsvieira" %% "scala-iso" % "0.1.2"

libraryDependencies ++=Seq(
  "com.typesafe.play" %% "play-test" % "2.6.11" % "test",
  "org.scalatest" %% "scalatest" % "3.0.4" % "test",
  "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % "test"
)

// libraryDependencies += "ch.qos.logback" % "logback-core" % "1.1.3"

// publishTo := Some(Resolver.file("file", new File(Path.userHome.absolutePath+"/.m2/repository")))
