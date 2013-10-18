name := "ScalaZ3"

version := "2.1"

organization := "ch.epfl.lara"

scalacOptions += "-deprecation"

scalacOptions += "-unchecked"

scalacOptions += "-feature"

scalaVersion := "2.10.2"

libraryDependencies ++= Seq(
    "org.scalatest" %% "scalatest" % "1.9.1" % "test"
)

fork in Test := true
