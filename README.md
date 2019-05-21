ScalaZ3 for Scala 2.10, 2.11, and 2.12
======================================

This is ScalaZ3 for Z3 4.7.1 and Scala 2.10, 2.11, and 2.12.

Compiling ScalaZ3
-----------------

You should have Java and SBT 1.2.x installed.

### Mac & Unix

Run

    sbt +package

to compile Z3 4.7.1 and cross-compile ScalaZ3 for Scala 2.10, 2.11 and 2.12.

The JAR files will be in `target/scala-2.XX/scalaz3_2.XX-4.7.1.jar`
and will contain the shared library dependencies.

For testing, run

    sbt +test

### Windows

#### Prerequisites

Install Visual Studio Community edition 2015
Make sure to have the following:
- Programming Languages
  - Visual C++
    - Common tools for Visual C++ 2015 (CHECK)

Install JDK 1.8
* There is a folder `include` in `C:\Program Files\Java\jdk1.8.0_121`. Create a copy of this `include` folder directly in `C:\Program Files\Java\`

Install a 64-bit version of GCC. To chec that, run `gcc -v`, it should display 64. If it shows  `mingw32` you need to install a new version.

We were able to successfully package and test ScalaZ3 with the MinGW 64bit compiler suite with the following options.

    Version: 6.3.0
    Architecture: x86_64
    Threads: wind32
    Esception: seh
    Build revision: 2

#### Packaging instructions

Open the native x64 command prompt (available in the start menu under the Visual Studio folder)

Now navigate to the scalaz3 folder and type:

    sbt +package

The JAR files will be in `target/scala-2.XX/scalaz3_2.XX-4.7.1.jar` and will contain the shared library
dependencies.

#### Test your package.

Run

    sbt test

If this does not work, check that `lib-bin/scalaz3.dll` is a correctly set up 64 bit dll:

    dumpbin /headers lib-bin/scalaz3.dll | findstr machine

The output should be (x64). If you encounter any other issue, please let us know.

Using ScalaZ3
-------------

### On a single operating system / architecture

Create a folder named `unmanaged` at the same level as your `build.sbt` file, and copy the JAR file in `target/scala-2.XX/scalaz3_2.XX-3.0.jar` into it.

Then add, the following lines to your `build.sbt` file:

```scala
unmanagedJars in Compile += {
  baseDirectory.value / "unmanaged" / s"scalaz3_${scalaBinaryVersion.value}-3.0.jar"
}
```

### On multiple operating systems / architectures

If you want to use ScalaZ3 in a project which must support various operating systems and architectures, you will have to compile ScalaZ3 on each of those systems/architectures, following the instructions above.

Make sure to name the resulting JAR files as `scalaz3-[osName]-[osArch]-[scalaBinaryVersion].jar`, where:

- `[osName]` is one of: `mac`, `win`, `unix`.
- `[osArch]` corresponds to `System.getProperty("sun.arch.data.model")`, ie. `x64`, `fds`, etc.
- `[scalaBinaryVersion]` is one of: `2.11`, `2.12`, `2.13`.

Create a folder named `unmanaged` at the same level as your `build.sbt` file, and copy the aforementioned JAR files into it.

Add the following lines to your `build.sbt` file:

```scala
val osInf = Option(System.getProperty("os.name")).getOrElse("")

val isUnix    = osInf.indexOf("nix") >= 0 || osInf.indexOf("nux") >= 0
val isWindows = osInf.indexOf("Win") >= 0
val isMac     = osInf.indexOf("Mac") >= 0

val osName = if (isWindows) "win" else if (isMac) "mac" else "unix"

unmanagedJars in Compile += {
  baseDirectory.value / "unmanaged" / s"scalaz3-$osName-$osArch-${scalaBinaryVersion.value}.jar"
}
```
