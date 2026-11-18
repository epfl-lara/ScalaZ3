ScalaZ3 ![Build status](http://laraquad4.epfl.ch:9000/epfl-lara/ScalaZ3/status/master)
=======

This is ScalaZ3 for Z3 5.4.1 and Scala 3.

[API documentation](https://epfl-lara.github.io/ScalaZ3/z3/index.html)
-------------------

How it works
------------

ScalaZ3 builds on the official Z3 Java distribution: each
[Z3 release](https://github.com/Z3Prover/z3/releases) comes with a `z3-<version>-all.jar`, which
contains the Java bindings (`com.microsoft.z3`) and the native libraries of all the supported
platforms:

| Platform      | Requirements          |
|---------------|-----------------------|
| `linux-x64`   | glibc 2.39 or later   |
| `linux-arm64` | glibc 2.38 or later   |
| `osx-arm64`   | macOS 13.3 or later   |
| `win-x64`     |                       |

The bindings pick, extract and load the native libraries of the current platform by themselves.
ScalaZ3 therefore neither compiles Z3 nor contains native code: it is a plain, platform-independent
Scala library, and the same JAR is used everywhere.

Compiling ScalaZ3
-----------------

You should have Java and SBT 1.x installed. Run

    sbt package

to get `target/scala-3.3.7/scalaz3_3-5.4.1.jar`. The build downloads the Z3 JAR in `z3/` and
checks it against the SHA-256 checksum recorded in `build.sbt`. To build from a copy you already
have (e.g. when offline), run `sbt -Dscalaz3.z3jar=/path/to/z3-5.4.1-all.jar package`.

For testing, run

    sbt test

To move to another Z3 release, update `version` and `z3JarSha256` in `build.sbt`.

Using ScalaZ3
-------------

ScalaZ3 needs the Z3 JAR on the class path, in the same version as the one it was compiled against.
There are two ways to get there.

### With the Z3 JAR as a separate dependency

Create a folder named `unmanaged` at the same level as your `build.sbt` file, and copy
`scalaz3_3-5.4.1.jar` and `z3-5.4.1-all.jar` into it. Then add the following lines to your
`build.sbt` file:

```scala
Compile / unmanagedJars ++= Seq(
  baseDirectory.value / "unmanaged" / "scalaz3_3-5.4.1.jar",
  baseDirectory.value / "unmanaged" / "z3-5.4.1-all.jar",
)
```

The Z3 JAR is large (about 54MB): rather than keeping a copy of it, you may let your build download
it, as `project/Z3Jar.scala` does for ScalaZ3 itself.

### With a standalone JAR

Run

    sbt packageBundle

to get `target/scala-3.3.7/scalaz3_3-5.4.1-bundle.jar`, which contains ScalaZ3 together with the
content of the Z3 JAR, and is all you need on the class path.

### Loading the native libraries

The native libraries are extracted once to `~/.z3/native` and loaded from there. This can be changed
with the system properties of the Z3 Java distribution:

- `-Dz3.nativeCacheDir=<dir>` moves the cache;
- `-Dz3.nativeCache=false` extracts the libraries to a fresh temporary directory at each run;
- `-Dz3.skipLibraryLoad=true` loads nothing, for applications that load `z3java` by themselves.

A `z3java` library found on `java.library.path` is preferred to the bundled ones. On Java 22 and
later, pass `--enable-native-access=ALL-UNNAMED` to the JVM to avoid a warning about JNI.

`z3.Z3Wrapper.ensureLoaded()` throws an `UnsatisfiedLinkError` if Z3 is not available on the
current platform.

### Resource limits

Next to timeouts, Z3 can bound the work of a solver with a resource limit (`rlimit`), which unlike
a timeout is deterministic: whether a query is solved does not depend on the machine or its load.

```scala
val solver = z3.mkSolver()
solver.setRlimit(1000000)  // for each subsequent check; 0 means no limit
solver.check()             // None once the limit is exhausted
solver.getRlimitCount()    // resources consumed so far
```

The same is available on `Z3Optimizer`. A default for all solvers can be set per context with
`new Z3Context("rlimit" -> 1000000)`, or globally with `Z3Context.setGlobalParam("rlimit", 1000000)`.
