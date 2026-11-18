import Z3Jar._

lazy val root = (project in file("."))
  .settings(
    name := "ScalaZ3",
    // ScalaZ3 is versioned after the Z3 release it wraps.
    version := "5.4.1",
    organization := "ch.epfl.lara",
    scalacOptions ++= Seq(
      "-deprecation",
      "-unchecked",
      "-feature",
    ),
    scalaVersion := "3.3.7",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.19" % "test",
    Test / fork := true,
    // Z3 loads its native libraries through JNI.
    Test / javaOptions ++= {
      if (scala.util.Properties.isJavaAtLeast("22")) Seq("--enable-native-access=ALL-UNNAMED") else Nil
    },

    z3Version := version.value,
    z3JarSha256 := "8399700e2dd33eb538de53e7d087bcb1ba8214ea66571daf3330f0c600ab4aa2",
    Z3Jar.settings,
  )
