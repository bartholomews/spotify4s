name := "Spotify Scala Client"

organization := "it.turingtest"

version := "0.0.4-SNAPSHOT"

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

// @see https://rwlive.wordpress.com/2016/02/21/setting-up-travis-ci-coveralls-codacy-and-bintray-for-your-sbt-based-github-project/

/*
resolvers += Resolver.url("scoverage-bintray", url("https://dl.bintray.com/sksamuel/sbt-plugins/"))(Resolver.ivyStylePatterns)
resolvers += Classpaths.sbtPluginReleases
resolvers += "Typesafe Repository" at "https://repo.typesafe.com/typesafe/releases/"

addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.3.3")
//addSbtPlugin("org.scoverage" % "sbt-coveralls" % "1.0.0")
*/

publishTo := Some(Resolver.file("file", new File(Path.userHome.absolutePath+"/.m2/repository")))




