package z3.scala

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

import java.io.File
import java.nio.file.Files
import java.util.zip.ZipFile
import scala.jdk.CollectionConverters._

/** ScalaZ3's JAR bundles the Z3 JAR (the Java bindings of Z3 and its native libraries for all
  * supported platforms), so that it is all one needs on the class path.
  */
class PackagedJar extends AnyFunSuite with Matchers {

  private lazy val jar = new File(sys.props("scalaz3.test.packagedJar"))

  private lazy val entries: Set[String] = {
    val zip = new ZipFile(jar)
    try zip.entries().asScala.map(_.getName).toSet
    finally zip.close()
  }

  test("The JAR contains ScalaZ3 and the Z3 Java bindings") {
    entries should contain allOf (
      "z3/Z3Wrapper.class",
      "z3/scala/Z3Context.class",
      "com/microsoft/z3/Native.class",
      "com/microsoft/z3/NativeLibraryLoader.class",
    )
  }

  test("The JAR contains the native libraries of all supported platforms") {
    entries should contain allOf (
      "com/microsoft/z3/native/linux-x64/libz3.so",     "com/microsoft/z3/native/linux-x64/libz3java.so",
      "com/microsoft/z3/native/linux-arm64/libz3.so",   "com/microsoft/z3/native/linux-arm64/libz3java.so",
      "com/microsoft/z3/native/osx-arm64/libz3.dylib",  "com/microsoft/z3/native/osx-arm64/libz3java.dylib",
      "com/microsoft/z3/native/win-x64/libz3.dll",      "com/microsoft/z3/native/win-x64/z3java.dll",
    )
  }

  test("The JAR keeps the licenses of Z3, and ScalaZ3's manifest") {
    entries should contain allOf ("META-INF/z3/LICENSE.txt", "META-INF/z3/provenance.json")
    val zip = new java.util.jar.JarFile(jar)
    try zip.getManifest.getMainAttributes.getValue("Implementation-Title") should equal ("ScalaZ3")
    finally zip.close()
  }

  test("The classes of the JAR run on Java 8 and later") {
    val zip = new ZipFile(jar)
    try {
      val tooRecent = zip.entries().asScala.filter(_.getName.endsWith(".class")).flatMap { entry =>
        val in = new java.io.DataInputStream(zip.getInputStream(entry))
        try {
          in.readInt() // magic
          in.readUnsignedShort() // minor version
          val major = in.readUnsignedShort()
          if (major > 52) Some(s"${entry.getName} (class file version $major)") else None
        } finally in.close()
      }.toList
      tooRecent should be (empty)
    } finally zip.close()
  }

  test("The JAR alone (with the Scala library) is enough to run Z3") {
    val dir = Files.createTempDirectory("scalaz3-packaged-jar")
    val main = dir.resolve("Main.java")
    Files.write(main,
      """public class Main {
        |  public static void main(String[] args) {
        |    z3.scala.Z3Context ctx = new z3.scala.Z3Context(scala.collection.immutable.Map$.MODULE$.empty());
        |    z3.scala.Z3Solver solver = ctx.mkSolver();
        |    z3.scala.Z3AST x = ctx.mkIntConst("x");
        |    solver.assertCnstr(ctx.mkGT(x, ctx.mkInt(41, ctx.mkIntSort())));
        |    System.out.println(z3.scala.package$.MODULE$.version() + " " + solver.check() + " " +
        |      solver.getModel().eval(x, true));
        |    ctx.delete();
        |  }
        |}""".stripMargin.getBytes("UTF-8"))
    val cp = jar.getPath + File.pathSeparator + sys.props("scalaz3.test.scalaLibrary")

    // Compiled first, as Java 8 can't run a source file.
    val javac = javax.tools.ToolProvider.getSystemJavaCompiler
    javac should not be (null)
    val compileOutput = new java.io.ByteArrayOutputStream
    withClue(compileOutput) {
      javac.run(null, compileOutput, compileOutput, "-cp", cp, "-d", dir.toString, main.toString) should equal (0)
    }

    val javaBin = Seq("java.exe", "java").map(new File(new File(sys.props("java.home"), "bin"), _))
      .find(_.isFile).getOrElse(fail("No java executable in " + sys.props("java.home"))).getPath
    val nativeAccess = if (scala.util.Properties.isJavaAtLeast("22")) Seq("--enable-native-access=ALL-UNNAMED") else Nil
    val cmd = Seq(javaBin) ++ nativeAccess ++ Seq(
      // A fresh cache, to check that the libraries are extracted from the JAR.
      s"-Dz3.nativeCacheDir=${dir.resolve("cache")}",
      "-cp", dir.toString + File.pathSeparator + cp, "Main")
    val proc = new ProcessBuilder(cmd.asJava).redirectErrorStream(true).start()
    val output = scala.io.Source.fromInputStream(proc.getInputStream, "UTF-8").mkString
    withClue(output) {
      proc.waitFor() should equal (0)
      output should include ("Z3 5.4 ")
      output should include ("Some(true) Some(42)")
    }
    Files.walk(dir.resolve("cache")).iterator().asScala.map(_.getFileName.toString).toSeq should contain (System.mapLibraryName("z3java"))
  }
}
