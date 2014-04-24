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

Cygwin does not work for generating scalaZ3. Use Visual Studio instead, if the given dll does not work.

If you wish to use the given DLL for x64 architectures:

1) Download [Z3 4.3 release for 64bit Windows](http://z3.codeplex.com/downloads/get/528578), and copy `libz3.dll` to `z3\[z3version]-win-[arch]\bin` and
`include\*.h` to `z3\[z3version]-win-[arch]\include\`. (eg: z3\4.3-win-64b\bin\libz3.dll)

2) If you wish, you can create `scalaz3.dll` by yourself by following these steps, else just use the current version and move to step 3).

1. Create a new Visual Studio Project: `Win32 Project`, `DLL`, named `scalaz3`
2. In the project menu, properties, platform, create a new one X64 from win32 settings, and set it up as default
3. Copy-paste all `*.h` and `*.c` files from `ScalaZ3\src\c` to the folder where you created your visual studio project, e.g.:
`C:\Users\...\Documents\Visual Studio 2013\Projects\scalaz3\scalaz3`
4. Copy-paste all include files from the `z3\[z3version]-win-[arch]\include` directory, as well as all `dll` files from the `z3\[z3version]-win-[arch]\bin` folder to the project folder.
5. In the Visual Studio interface, right-click the project, `add existing item...` and add all of the above.
6. In project properties, configuration properties, C/C++, General, Other include directory, add both `C:\Program Files\Java\jdk1.7.0_25\include` and `C:\Program Files\Java\jdk1.7.0_25\include\win32`. You should have this jdk installed already. If not, [download it there](http://www.oracle.com/technetwork/java/javase/downloads/jdk7-downloads-1880260.html).
7. Open the Visual Studio Command Line Prompt for x64 bits (Start menu, All programs Visual Studio 2013, Visual Studio Tools, Native tool command line x64 for VS2013 or something similar)
8. Navigate to the visual studio project (see step 4.) and run:

```
dumpbin /exports libz3.dll > libz3.def
```

9. Change the content of libz3.def so that the start looks like (use Notepad++ for example)

```
EXPORTS
Z3_app_to_ast
Z3_append_log
```

10. Create the lib with the Visual Studio Command Line with the command:

```
lib /def:.\libz3.def /OUT:.\libz3.lib
```

11. Rename `cast.c`,`extra.c`, `z3_thycallbacks.c` and `z3_Z3Wrapper.c` with the `*.cpp` extension
12. Change (*ENV)->get....(ENV,  to (*ENV).->get...( everywhere (ENV is a string which is not necessarily "env") in `extra.cpp`, `z3_Z3Wrapper.cpp` and `Z3_thycallbacks.cpp`.  Fix other compilation errors if any. You can use the regexp to find 

    \(\*(\w)+\)->(\w+)\(\1, 
   
and replace by

    \1->\2\(

The following four instructions also help to fix errors depending on your jni.h version and visual studio version:
13. Changé the quotes for z3 in `cast.h`,`extra.h`, `z3_thycallbacks.h`, `z3_thycallbacks.cpp` et `z3_Z3Wrapper.cpp` :
`#include <z3.h>` to `#include "z3.h"`
14. Add `#include "stdafx.h"` at the very beginning of `cast.h`, `z3_thycallbacks.cpp`, `extra.cpp` and `casts.cpp`
15. Remove all `inline` keywords in `cast.cpp` and `cast.h`
16. Locate line 27 in `Z3_thycallbacks.cpp` and change `(*env)->NewGlobalRef(env, pc);` to `pc`.

17. Now compiling (Project menu, generate the solution) gives the DLL in the following repository (Note that this is the "solution" folder, not the "project" one)

    C:\Users\Mikael\Documents\Visual Studio 2013\Projects\scalaz3\x64\Debug

18. Copy the `scalaZ3.dll` from step 17 to `ScalaZ3/lib-bin` and replace the existing dll.

*** THE STEP WHICH MADE EVERYTHING WORK: COMPLETELY CUT FROM CYGWIN ***

19. Remove  C:\cygwin\bin from the PATH environment variable so that when invoking sbt it makes sure that no cygwin is involved.


3) Run to create the jar file. It will end up in
'target/scala2.10/scalaz3\_2.10-2.1.jar' and will contain the shared library
dependencies.

    sbt compile
	sbt package  //this fails because it cannot run gcc
	sbt packageBin

4) Run the tests with

    sbt test
