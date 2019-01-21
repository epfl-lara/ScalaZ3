import ScalaZ3Build._

lazy val root = (project in file("."))
  .settings(
    name := "ScalaZ3",
    version := "3.0",
    organization := "ch.epfl.lara",
    scalacOptions ++= Seq(
      "-deprecation",
      "-unchecked",
      "-feature",
    ),
    scalaVersion := "2.12.7",
    crossScalaVersions := Seq("2.10.7", "2.11.12", "2.12.7"),
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "3.0.1" % "test"
    ),
    fork in Test := true,
    checksumKey := checksumTask.value,
    gccKey := gccTask.value,
    z3Key := z3Task.value,
    Compile / Keys.`package` := packageTask.value,
    Compile / unmanagedJars += Attributed.blank(z3JarFile),
    Compile / compile := ((Compile / compile) dependsOn checksumTask).value,
    Test / test := ((Test / test) dependsOn (Compile / Keys.`package`)).value,
    Test / internalDependencyClasspath := testClasspath.value,
    Compile / packageBin / mappings := newMappingsTask.value,
  )

