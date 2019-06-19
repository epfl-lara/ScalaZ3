import ScalaZ3Build._

lazy val root = (project in file("."))
  .settings(
    name := "ScalaZ3",
    version := "4.7.1",
    organization := "ch.epfl.lara",
    scalacOptions ++= Seq(
      "-deprecation",
      "-unchecked",
      "-feature",
    ),
    scalaVersion := "2.12.8",
    crossScalaVersions := Seq("2.10.7", "2.11.12", "2.12.8", "2.13.0"),
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "3.0.8" % "test"
    ),
    libraryDependencies ++= {
      CrossVersion.partialVersion(scalaVersion.value) match {
        // if scala 2.11+ is used, add dependency on scala-xml module
        case Some((2, scalaMajor)) if scalaMajor >= 11 => Seq("org.scala-lang.modules" %% "scala-xml" % "1.2.0")
        case _                                         => Seq.empty
      }
    },
    fork in Test := true,
    checksumKey := checksumTask.value,
    gccKey := gccTask.value,
    z3Key := z3Task.value,
    Compile / Keys.`package` := packageTask.value,
    Compile / unmanagedJars += Attributed.blank(z3JarFile),
    Compile / compile := ((Compile / compile) dependsOn checksumTask).value,
    Test / test := ((Test / test) dependsOn (Compile / Keys.`package`)).value,
    Test / compile := ((Test / compile) dependsOn (Compile / Keys.`package`)).value,
    Test / internalDependencyClasspath := testClasspath.value,
    Compile / packageBin / mappings := newMappingsTask.value,
  )

