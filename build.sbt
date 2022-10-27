import ScalaZ3Build._

lazy val root = (project in file("."))
  .settings(
    name := "ScalaZ3",
    version := "4.8.14",
    organization := "ch.epfl.lara",
    scalacOptions ++= Seq(
      "-deprecation",
      "-unchecked",
      "-feature",
    ),
    scalaVersion := "3.2.0",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.14" % "test",
    Test / fork  := true,
    checksumKey := checksumTask.value,
    gccKey := gccTask.value,
    z3Key := z3Task.value,
    Compile / Keys.`package` := packageTask.value,
    Compile / unmanagedJars += Attributed.blank(z3JarFile),
    Compile / compile := ((Compile / compile) dependsOn checksumTask).value,
    Test / test := ((Test / test) dependsOn (Compile / Keys.`package`)).value,
    Test / compile := ((Test / compile) dependsOn (Compile / Keys.`package`)).value,
    Test / testOnly := ((Test / testOnly) dependsOn (Compile / Keys.`package`)).evaluated,
    Test / internalDependencyClasspath := testClasspath.value,
    Compile / packageBin / mappings := newMappingsTask.value,
  )

