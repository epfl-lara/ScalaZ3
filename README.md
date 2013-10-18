ScalaZ3 for Scala 2.10
======================

This is ScalaZ3 for Scala 2.10 and Z3 4.3. Switch to the branch '2.9.x' for
Scala 2.9 support.

Compiling ScalaZ3
=================

Prerequisites
----------------------
You should have Java and SBT 0.13.x installed.

Linux
----------------------

1) Download Z3 source code from http://z3.codeplex.com/, compile it, and copy
the headers and built library to z3/[z3version]/include and z3/[z3version]/lib
respectively. (eg: z3/4.3-unix-64b/include/z3.h and
z3/4.3-unix-64b/lib/libz3.so).

2) Run 'sbt package' to create the jar file. It will be in
'target/scala-2.10/scalaz3\_2.10-2.1.jar' and will contain the shared library
dependencies.

3) For testing, run

    sbt test

Mac
----------------------

1) Download Z3 source code from http://z3.codeplex.com/, compile it, and copy
the headers and built library to z3/[z3version]/include and z3/[z3version]/lib
respectively. (eg: z3/4.3-unix-64b/include/z3.h and
z3/4.3-unix-64b/lib/libz3.dnylib).

2) Run 'sbt package' to create the jar file. It will be in
'target/scala-2.10/scalaz3\_2.10-2.1.jar' and will contain the shared library
dependencies.

3) For testing, run

    sbt test

Windows
----------------------

1) Download Cygwin, and install packages for gcc.

2) Download Z3 4.3 release, and copy libz3.dll to z3/[z3version]/bin and
include/\*.h to /z3/[z3version]/include/. (eg: z3/4.3-win-64b/bin/libz3.dll)

3) Run 'sbt package' to create the jar file. It will end up in
'target/scala2.10/scalaz3\_2.10-2.1.jar' and will contain the shared library
dependencies.
