import sbt._
import sbt.Keys._

import java.net.{HttpURLConnection, URI}
import java.nio.file.{Files, StandardCopyOption}
import java.security.MessageDigest

/** Fetches the official Z3 Java distribution.
  *
  * Z3 publishes, with each release, a single JAR which contains the Java bindings
  * (`com.microsoft.z3`) together with the native libraries of all supported platforms.
  * The bindings extract and load the right native libraries by themselves, so ScalaZ3 has
  * neither to build Z3 nor any native code of its own: it bundles the content of that JAR in
  * its own, which is then all one needs on the class path, on every platform.
  */
object Z3Jar {

  val z3Version = settingKey[String]("Version of the Z3 release ScalaZ3 is built against")
  val z3JarUrl  = settingKey[String]("Where to download the all-platforms Z3 JAR from")
  val z3JarSha256 = settingKey[String]("Expected SHA-256 checksum of the all-platforms Z3 JAR")
  val z3JarFile = settingKey[File]("Where the all-platforms Z3 JAR is stored locally")
  val z3Jar     = taskKey[File]("Downloads (if needed) and verifies the all-platforms Z3 JAR")
  val z3JarContents = taskKey[Seq[(File, String)]]("The files of the all-platforms Z3 JAR, to bundle in ScalaZ3's")

  def sha256(file: File): String = {
    val digest = MessageDigest.getInstance("SHA-256")
    val in = new java.io.FileInputStream(file)
    try {
      val buf = new Array[Byte](1 << 16)
      Iterator.continually(in.read(buf)).takeWhile(_ >= 0).foreach(digest.update(buf, 0, _))
    } finally in.close()
    digest.digest().map("%02x".format(_)).mkString
  }

  private def download(url: String, to: File): Unit = {
    val conn = new URI(url).toURL.openConnection().asInstanceOf[HttpURLConnection]
    conn.setInstanceFollowRedirects(true)
    conn.setConnectTimeout(30000)
    conn.setReadTimeout(60000)
    try {
      val code = conn.getResponseCode
      if (code != 200) sys.error(s"Could not download $url (HTTP $code)")
      val in = conn.getInputStream
      try Files.copy(in, to.toPath, StandardCopyOption.REPLACE_EXISTING)
      finally in.close()
    } finally conn.disconnect()
  }

  lazy val settings: Seq[Setting[_]] = Seq(
    z3JarUrl :=
      s"https://github.com/Z3Prover/z3/releases/download/z3-${z3Version.value}/z3-${z3Version.value}-all.jar",

    // `-Dscalaz3.z3jar=/path/to/z3-all.jar` uses a local copy instead (e.g. when offline).
    z3JarFile := sys.props.get("scalaz3.z3jar").map(file)
      .getOrElse(baseDirectory.value / "z3" / s"z3-${z3Version.value}-all.jar"),

    z3Jar := {
      val log = streams.value.log
      val jar = z3JarFile.value
      val expected = z3JarSha256.value

      if (!jar.exists) {
        if (sys.props.contains("scalaz3.z3jar")) sys.error(s"No such file: $jar")
        log.info(s"Downloading ${z3JarUrl.value} to $jar ...")
        IO.createDirectory(jar.getParentFile)
        val tmp = jar.getParentFile / (jar.getName + ".part")
        download(z3JarUrl.value, tmp)
        val actual = sha256(tmp)
        if (actual != expected) {
          IO.delete(tmp)
          sys.error(s"Checksum mismatch for ${z3JarUrl.value}: expected $expected, got $actual")
        }
        IO.move(tmp, jar)
      }

      // Cheap enough (~0.2s) to be done only when the file changes.
      val stamp = streams.value.cacheDirectory / "z3-jar-verified"
      val key = s"$expected ${jar.getAbsolutePath} ${jar.length} ${jar.lastModified}"
      if (!stamp.exists || IO.read(stamp) != key) {
        val actual = sha256(jar)
        if (actual != expected)
          sys.error(s"Checksum mismatch for $jar: expected $expected, got $actual. " +
            "Delete it to download it again, or update z3JarSha256.")
        IO.write(stamp, key)
      }

      jar
    },

    Compile / unmanagedJars += Attributed.blank(z3Jar.value),

    // The Z3 JAR is merged into ScalaZ3's own, so that the latter is all one needs on the class path.
    z3JarContents := {
      val jar = z3Jar.value
      val dir = target.value / "z3-jar-contents" / jar.base
      val stamp = dir / ".unzipped"
      if (!stamp.exists) {
        IO.delete(dir)
        IO.unzip(jar, dir)
        IO.touch(stamp)
      }
      (dir ** "*").get.filter(f => f.isFile && f != stamp).map(f => f -> IO.relativize(dir, f).get.replace('\\', '/'))
        .filterNot(_._2 == "META-INF/MANIFEST.MF")
    },

    Compile / packageBin / mappings ++= z3JarContents.value,
  )
}
