organization := "com.preact"
name := "preact-scala"
version := "1.0-SNAPSHOT"

scalaVersion := "2.11.7"


libraryDependencies += "net.databinder.dispatch" %% "dispatch-core" % "0.11.3"
libraryDependencies += "com.typesafe.play" %% "play-json" % "2.4.2"
libraryDependencies += "org.slf4j" % "slf4j-api" % "1.7.7"

scalacOptions ++= Seq(
  //"-deprecation",
  "-unchecked",
  "-feature",
  "-optimise",
  "-explaintypes",
  "-encoding", "UTF-8",
  "-Xlint")
//exportJars := true