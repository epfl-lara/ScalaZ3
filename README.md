Precompiled library
===================

You can download a precompiled version of ScalaZ3, including shared libraries
(32bit) for Linux and Windows through the GitHub "Download" menu. Unless
otherwise mentioned, it is compiled with Scala 2.9.1.

Compiling ScalaZ3
=================

Setup steps, for Linux
----------------------

1) Download Z3, and copy the include and lib files to z3/[z3version]/include and
z3/[z3version]/lib respectively. (eg: z3/3.2/include/z3.h and
z3/3.2/lib/libz3.so).

2) Download sbt. Run 'sbt update' from the current directory. (<i>Warning:
currently only versions of sbt older than 0.10 are supported!</i>)

3) Run 'sbt package' to create the jar file. It will be in
target/[scalaversion]/scalaz3.jar and will contain the shared library required
by the bindings.

4) For testing, run
    LD_LIBRARY_PATH=z3/[z3version]/lib sbt test
Alternatively, start a console by running
    LD_LIBRARY_PATH=z3/[z3version]/lib scala -cp target/[scalaversion]scalaz3.jar
then try, e.g.,
    println(z3.scala.version).

Setup steps, for Windows
------------------------

A) Download and install Z3 using the .msi installer. 

B) Download sbt (see 2 above).

C) The 'package' command in the current build script does not work with
Windows; there is no equivalent for the gcc command. Run 'package' anyway so
that it compiles the Java/Scala sources and generates the JNI header files.

D) Assuming you have copied the 'include' and 'bin' directories from the Z3
distribution in z3/[z3version], the following command should compile the shared
library, assuming you have installed MinGW:
    gcc -shared -o lib-bin\scalaz3.dll -D_JNI_IMPLEMENTATION_ -Wl,--kill-at -I "[jdkpath]\include" -I "[jdkpath]\include\win32" -I z3\[z3version]\include src\c\*.h src\c\*.c z3\[z3version]\bin\z3.lib

E) You can manually create a jar with the contents of target/[scalaversion] and
lib-bin.
