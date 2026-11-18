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
    scalaVersion := "3.9.0",
    // ScalaZ3 runs on Java 21 and later, whatever the JDK it is built with.
    javacOptions ++= Seq("--release", "21"),
    scalacOptions ++= Seq("-release", "21"),
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.19" % "test",
    Test / fork := true,
    // Z3 loads its native libraries through JNI.
    Test / javaOptions ++= {
      if (scala.util.Properties.isJavaAtLeast("22")) Seq("--enable-native-access=ALL-UNNAMED") else Nil
    },
    // For the tests of the packaged JAR (PackagedJar.scala).
    Test / javaOptions ++= Seq(
      s"-Dscalaz3.test.packagedJar=${(Compile / packageBin).value}",
      s"-Dscalaz3.test.scalaLibrary=${(Compile / dependencyClasspath).value.files
        .filter(_.getName.startsWith("scala")).mkString(java.io.File.pathSeparator)}",
    ),

    // Publishing to Maven Central (see .github/workflows/release.yml): `sbt publishSigned` signs
    // the artifacts and stages them locally, `sbt sonaRelease` uploads them to the Central Portal
    // and releases them (`sbt sonaUpload` only uploads them, to inspect them in the Portal first).
    // Both read the Portal token from SONATYPE_USERNAME and SONATYPE_PASSWORD.
    description := "Scala bindings for the Z3 SMT solver, with Z3 for Linux, macOS and Windows",
    homepage := Some(url("https://github.com/epfl-lara/ScalaZ3")),
    licenses := Seq("Apache-2.0" -> url("https://www.apache.org/licenses/LICENSE-2.0")),
    scmInfo := Some(ScmInfo(
      url("https://github.com/epfl-lara/ScalaZ3"),
      "scm:git:git@github.com:epfl-lara/ScalaZ3.git",
    )),
    developers := List(
      Developer("epfl-lara", "EPFL Lab for Automated Reasoning and Analysis", "lara.maven@epfl.ch", url("https://lara.epfl.ch")),
    ),
    publishMavenStyle := true,
    pomIncludeRepository := { _ => false },
    publishTo := localStaging.value,
    // The key that signs the artifacts, when the keyring holds several
    pgpSigningKey := sys.env.get("PGP_FINGERPRINT"),

    z3Version := version.value,
    z3JarSha256 := "8399700e2dd33eb538de53e7d087bcb1ba8214ea66571daf3330f0c600ab4aa2",
    Z3Jar.settings,
  )
