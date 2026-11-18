package z3.scala

import com.microsoft.z3.{Native, Z3Exception}
import com.microsoft.z3.enumerations.Z3_decl_kind
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

/** ScalaZ3 relies on the official Z3 JAR for both the Java bindings and the native libraries. */
class NativeLibrary extends AnyFunSuite with Matchers {

  test("The native library is loaded from the Z3 JAR") {
    z3.Z3Wrapper.ensureLoaded()
    (z3.Z3Wrapper.withinJar(): @annotation.nowarn("cat=deprecation")) should be (true)

    val major, minor, build, revision = new Native.IntPtr
    Native.getVersion(major, minor, build, revision)
    (major.value, minor.value, build.value) should equal ((5, 4, 1))
  }

  test("ScalaZ3 and Z3 versions agree") {
    version should startWith ("Z3 5.4 ")
    version should include ("ScalaZ3 5.4.1")
    Native.getFullVersion() should startWith ("5.4.1")
  }

  test("The Z3 JAR bundles the native libraries of all supported platforms") {
    val libraries = Seq(
      "linux-x64/libz3.so",      "linux-x64/libz3java.so",
      "linux-arm64/libz3.so",    "linux-arm64/libz3java.so",
      "osx-arm64/libz3.dylib",   "osx-arm64/libz3java.dylib",
      "win-x64/libz3.dll",       "win-x64/z3java.dll",
    )
    for (library <- libraries) withClue(library) {
      classOf[Native].getResource("/com/microsoft/z3/native/" + library) should not be (null)
    }
  }

  test("ScalaZ3 does not ship native code of its own") {
    getClass.getResource("/lib-bin/") should be (null)
  }

  test("Z3 errors are reported as exceptions") {
    val z3 = new Z3Context()
    val thrown = intercept[Z3Exception] {
      z3.mkAnd(z3.mkIntConst("x"), z3.mkIntConst("y"))
    }
    thrown.getMessage should include ("Sort mismatch")

    // The context is still usable afterwards.
    val solver = z3.mkSolver()
    solver.assertCnstr(z3.mkFalse())
    solver.check() should equal (Some(false))
    z3.delete()
  }

  test("Contexts can be used concurrently") {
    val results = new java.util.concurrent.ConcurrentLinkedQueue[Option[Boolean]]
    val threads = for (i <- 1 to 8) yield new Thread(() => {
      val z3 = new Z3Context("MODEL" -> true)
      val x = z3.mkIntConst("x")
      val solver = z3.mkSolver()
      solver.assertCnstr(z3.mkEq(z3.mkMul(x, x), z3.mkInt(i * i, z3.mkIntSort())))
      results.add(solver.check())
      z3.delete()
    })
    threads.foreach(_.start())
    threads.foreach(_.join())
    results.toArray.toSeq should equal (Seq.fill(8)(Some(true)))
  }

  test("All the declaration kinds of Z3 are known to ScalaZ3") {
    val unknown = Z3_decl_kind.values().toSeq.filter { kind =>
      try { Z3DeclKind.fromInt(kind.toInt); false } catch { case _: Throwable => true }
    }
    unknown should be (empty)
  }
}
