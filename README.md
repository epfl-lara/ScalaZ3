ScalaZ3 for Scala 2.11
======================

This is ScalaZ3 for Scala 2.11 and Z3 4.4.2.
ScalaZ3 for Z3 version 4.3.2 can be found in the
branch 'Z3-4.3.2'. Switch to the branch '2.9.x' for
Scala 2.9 support (Z3 version 4.3.2).

Compiling ScalaZ3
=================

You should have Java and SBT 0.13.x installed.

1) Clone the [Z3 source repository](https://github.com/Z3Prover/z3.git) into
./z3 (default location from project base).

2) Run 'sbt package' to create the jar file. It will be in
'target/scala-2.11/scalaz3\_2.11-2.1.jar' and will contain the shared library
dependencies.

3) For testing, run

    sbt test

Windows and Mac
----------------------

Automatically building Z3 for these targets is not supported yet.
The build.sbt file will be patched soon to deal with these.
