ScalaZ3 for Scala 2.11
======================

This is ScalaZ3 for Scala 2.11 and Z3 4.4.2.
ScalaZ3 for Z3 version 4.3.2 can be found in the
branch 'Z3-4.3.2'. Switch to the branch '2.9.x' for
Scala 2.9 support (Z3 version 4.3.2).

Compiling ScalaZ3
=================

You should have Java and SBT 0.13.x installed.

Run 'sbt package' to create the jar file. It will be in
'target/scala-2.11/scalaz3\_2.11-3.0.jar' and will contain the shared library
dependencies.

For testing, run

    sbt test

Windows
-------

Install Visual Studio Community edition 2015
Make sure to have the following:
- Programming Languages
  - Visual C++
    - Common tools for Visual C++ 2015 (CHECK)

Open the native x64 command prompt (available in the start menu under the Visual Studio folder)

Now navigate to the scalaz3 folder and type:

    sbt package

The jar file will be in
'target/scala-2.11/scalaz3\_2.11-3.0.jar' and will contain the shared library
dependencies.

Mac
---

Automatically building Z3 for Mac is not supported yet.
The build.sbt file will be patched soon to deal with these.
