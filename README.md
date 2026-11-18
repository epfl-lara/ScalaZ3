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
ScalaZ3 therefore neither compiles Z3 nor has native code of its own. Its JAR bundles the content of
the Z3 JAR, so that it is all you need on the class path, and the same JAR is used everywhere.

Compiling ScalaZ3
-----------------

You should have Java 21 or later and SBT 1.x installed. Run

    sbt package

to get `target/scala-3.3.7/scalaz3_3-5.4.1.jar` (about 55MB). The build downloads the Z3 JAR in
`z3/`, checks it against the SHA-256 checksum recorded in `build.sbt`, and bundles its content. To build from a copy you already
have (e.g. when offline), run `sbt -Dscalaz3.z3jar=/path/to/z3-5.4.1-all.jar package`.

For testing, run

    sbt test

To move to another Z3 release, update `version` and `z3JarSha256` in `build.sbt`.

Using ScalaZ3
-------------

Add the following line to your `build.sbt` file:

```scala
libraryDependencies += "ch.epfl.lara" %% "scalaz3" % "5.4.1"
```

To use a JAR you built yourself instead, create a folder named `unmanaged` at the same level as
your `build.sbt` file, copy `scalaz3_3-5.4.1.jar` into it, and add:

```scala
Compile / unmanagedJars += baseDirectory.value / "unmanaged" / "scalaz3_3-5.4.1.jar"
```

ScalaZ3 includes the Z3 Java bindings (`com.microsoft.z3`): do not put another version of them on
the class path.

ScalaZ3 runs on Java 21 and later.

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

Releasing
---------

ScalaZ3 is published to Maven Central as `"ch.epfl.lara" %% "scalaz3" % "5.4.1"`, by the
[Release workflow](.github/workflows/release.yml):

1. Set `version` in `build.sbt` (and `z3JarSha256`, for a new Z3 release), and merge it. A version
   can't be published twice: a new build of the same Z3 release gets a suffix, e.g. `5.4.1-1`.
2. Run the workflow from the Actions tab ("Run workflow" on `master`), or with
   `gh workflow run release.yml -f version=v5.4.1`, giving the version with a leading `v`. It tests,
   signs and publishes that version to Maven Central, then tags the commit `v<version>` and creates
   the GitHub release, with generated notes and the JAR attached.

To check a release before it goes out, choose the mode `upload` instead: it only uploads the
artifacts to the [Central Portal](https://central.sonatype.com/publishing/deployments), to publish
or drop them there, and creates neither the tag nor the release.

Locally, `sbt publishSigned` signs the artifacts and stages them in `target/sona-staging`.
