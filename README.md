ScalaZ3 for Scala 2.10
======================

This is ScalaZ3 for Scala 2.10 and Z3 4.3. Switch to the branch '2.9.x' for
Scala 2.9 support.


Downloading Z3
=================

Z3 can be downloaded from the [Z3 download site](http://z3.codeplex.com/)


Compiling ScalaZ3
=================

Setup steps, for Linux
----------------------

1) Download Z3, and copy the include and lib files to z3/[z3version]/include and
z3/[z3version]/lib respectively. (eg: z3/4.3-unix-64b/include/z3.h and
z3/4.3-unix-64b/lib/libz3.so).

2) Download sbt 0.12.x.

3) Run 'sbt package' to create the jar file. It will be in
target/[scalaversion]/scalaz3....jar and will contain the shared
library required by the bindings.

4) For testing, run

    LD_LIBRARY_PATH=z3/[z3version]/lib sbt test

Alternatively, start a console by running

    LD_LIBRARY_PATH=z3/[z3version]/lib scala -cp target/[scalaversion]scalaz3.jar

then try, e.g.,

    println(z3.scala.version).

Setup steps, for Mac
----------------------

1) Download Z3, and copy the include and lib files to z3/[z3version]/include and
z3/[z3version]/lib respectively. (eg: z3/4.3-osx-64b/include/z3.h and
z3/4.3-osx-64b/lib/libz3.so).

2) Download sbt 0.12.x.

3) Run 'sbt package' to create the jar file. It will be in
target/[scalaversion]/scalaz3....jar and will contain the shared
library required by the bindings.

4) For testing, run

    DYLD_LIBRARY_PATH=z3/[z3version]/lib sbt test

Alternatively, start a console by running

    DYLD_LIBRARY_PATH=z3/[z3version]/lib scala -cp target/[scalaversion]scalaz3.jar

then try, e.g.,

    println(z3.scala.version).

Setup steps, for Windows
------------------------

A) Download and install Z3 using the .msi installer

B) Download sbt (see 2 above).

C) The 'package' command in the current build script does not work with
Windows; there is no equivalent for the gcc command. Run 'javah' so that it
compiles the Java/Scala sources and generates the JNI header files.

If you encounter an error such as "Scala library not found in dependencies ?!?",
then open Build.sbt.scala, line 106, and replace the slashes by double backslashes.

    deps.map(_.data.absolutePath).find(_.endsWith("lib\\scala-library.jar")) match {

If you encounter an error such as "Error: Could not find class file for 'z3.Z3Wrapper'"
then run sbt last to recover the last run starting with 'javah' and run it yourself by
adding double quotes and double backslashes in the windows command line:

    javah -classpath ".\\target\\scala-2.10\\classes;.\\lib\\scala-library.jar" -d .\\src\\c z3.Z3Wrapper
	
D) Assuming you have copied the 'include' and 'bin' directories from the Z3
distribution in z3/[z3version], the following command should compile the shared
library, assuming you have installed MinGW:

    gcc -shared -o lib-bin\scalaz3.dll -D_JNI_IMPLEMENTATION_ -Wl,--kill-at -I "[jdkpath]\include" -I "[jdkpath]\include\win32" -I z3\[z3version]\include src\c\*.h src\c\*.c z3\[z3version]\bin\z3.lib

Replace backslashes by double backslashes if you are using Cygwin.

    gcc -shared -o lib-bin\\scalaz3.dll -D_JNI_IMPLEMENTATION_ -Wl,--kill-at -I "$JAVA_HOME\\include" -I "$JAVA_HOME\\include\\win32" -I z3\\4.3-win-64b\\include src/c/*.h src/c/*.c z3\\4.3-win-64b\\bin\\libz3.dll

E) You can manually create a jar with the contents of target/[scalaversion] and
lib-bin.

To add a dll to the jar, use the following command:

    jar uf target/scala-2.10/scalaz3_2.10-2.0.jar lib-bin/scalaz3.dll
