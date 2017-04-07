ScalaZ3 for Scala 2.10, 2.11, and 2.12
======================================

This is ScalaZ3 for Z3 4.5.0 and Scala 2.10, 2.11, and 2.12.

ScalaZ3 for Z3 version 4.3.2 can be found in the branch `Z3-4.3.2`.

Switch to the branch `2.9.x` for Scala 2.9 support (Z3 version 4.3.2).

Compiling ScalaZ3
=================

You should have Java and SBT 0.13.x installed.

Mac & Unix
----------

Run

    sbt +package
    
to compile Z3 4.5.0 and cross-compile ScalaZ3 for Scala 2.10, 2.11 and 2.12.

The JAR files will be in `target/scala-2.XX/scalaz3_2.XX-3.0.jar`
and will contain the shared library dependencies.

For testing, run

    sbt +test

Windows
-------

### Prerequisites

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

### Packaging instructions

Open the native x64 command prompt (available in the start menu under the Visual Studio folder)

Now navigate to the scalaz3 folder and type:

    sbt +package

The JAR files will be in `target/scala-2.XX/scalaz3_2.XX-3.0.jar` and will contain the shared library
dependencies.

### Test your package.

Run

    sbt test

If this does not work, check that `lib-bin/scalaz3.dll` is a correctly set up 64 bit dll:

    dumpbin /headers lib-bin/scalaz3.dll | findstr machine

The output should be (x64). If you encounter any other issue, please let us know.