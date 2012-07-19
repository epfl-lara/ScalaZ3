import sbt._
import Process._

class ScalaZ3Project(info: ProjectInfo) extends DefaultProject(info) with FileTasks {
  val scalatest = "org.scalatest" %% "scalatest" % "1.8" % "test"

  // All Java classes that contain native methods.
  val nativeClasses = List("z3.Z3Wrapper")

  val z3DefaultVersion = "4.0"

  override def compileOptions = super.compileOptions ++ Seq(Unchecked)

  lazy val cPath : Path = "." / "src" / "c"
  lazy val cFiles : PathFinder = "." / "src" / "c" * "*.c"
  lazy val generatedHeaders : PathFinder = cPath * "z3_Z3Wrapper*.h"
  lazy val soName : String = System.mapLibraryName("scalaz3")
  lazy val libBinPath : Path = "." / "lib-bin"
  lazy val libBinFilePath : Path = libBinPath / soName

  val checksumFilePath = mainJavaSourcePath / "z3" / "LibraryChecksum.java"
  val checksumSourcePath = mainJavaSourcePath / "z3" / "Z3Wrapper.java"
  override val compileAction = super.compileAction dependsOn(computeChecksum)
  override val packagePaths = libBinFilePath +++ super.packagePaths
  override val packageAction = super.packageAction dependsOn(gcc)

  def z3IncludePath(vn : String) : Path = if(is64bit) {
    "." / "z3" / "x64" / vn / "include"
  } else {
    "." / "z3" / vn / "include"
  }

  def z3LibPath(vn : String) : Path = {
    val libString = if (isWindows) "bin" else "lib"
    if(is64bit) {
      "." / "z3" / "x64" / vn / libString
    } else {
      "." / "z3" / vn / libString
    }
  }

  override def cleanAction = super.cleanAction dependsOn(cleanMore)
  override def testAction = super.testAction dependsOn(loadLib)

  lazy val testArch = task {
    val o = if(isUnix) {
      "Unix/Linux"
    } else if(isWindows) {
      "Windows"
    } else if(isMac) {
      "Mac OS"
    } else {
      "** Unknown **"
    }

    val a = if(is64bit) {
      "64bit"
    } else {
      "32bit"
    }

    log.info("You appear to be running " + o + " on a " + a + " architecture. If this is wrong or unknown, you should stop here.")
    None
  }

  // This is brittle. It will only work on well-behaved Unix installations...
  // We should make this work with MacOS / Windows too...
  lazy val jdkIncludePath : Path = Path.fromFile(new java.io.File(System.getProperty("java.home")).getParent) / "include"
  lazy val jdkUnixIncludePath : Path = jdkIncludePath / "linux"
  lazy val jdkWinIncludePath : Path = jdkIncludePath / "win32"

  lazy val javah = myExec(
    "Generating JNI C headers",
    "javah -classpath " + mainCompilePath.absolutePath + " -d " + cPath.absolutePath + " " + nativeClasses.mkString(" ")
  ) dependsOn(compile)

  lazy val gcc : ManagedTask = if(isUnix) {
    gccUnix
  } else if (isMac) {
    gccOsx
  } else if (isWindows) {
    gccWin
  } else task {
    Some("Don't know how to compile the native library for your architecture.")
  }

  lazy val gccUnix = { 
    val z3VN = z3DefaultVersion
    val zip = z3IncludePath(z3VN)
    val zlp = z3LibPath(z3VN)
    if(!zip.exists || !zip.isDirectory)
      task { Some("Could not find the directory " + zip.absolutePath) }
    else if(!zlp.exists || !zlp.isDirectory)
      task { Some("Could not find the directory " + zlp.absolutePath) }
    else
      myExec(
        "Compiling C library",
        "gcc -o " + libBinFilePath.absolutePath + " " +
        "-shared -Wl,-soname," + soName + " " +
        "-I" + jdkIncludePath.absolutePath + " " +
        "-I" + jdkUnixIncludePath.absolutePath + " " +
        "-I" + z3IncludePath(z3VN).absolutePath + " " +
        "-L" + z3LibPath(z3VN).absolutePath + " " +
        "-g -lc -Wl,--no-as-needed -Wl,--copy-dt-needed -lz3 -fPIC -O2 -fopenmp " +
        cFiles.getPaths.mkString(" ")
      )
  } dependsOn(javah)

  lazy val gccOsx = { 
    val z3VN = z3DefaultVersion
    val zip = z3IncludePath(z3VN)
    val zlp = z3LibPath(z3VN)
    val frameworkPath = "/System/Library/Frameworks/JavaVM.framework/Versions/Current/Headers"

    if(!zip.exists || !zip.isDirectory)
      task { Some("Could not find the directory " + zip.absolutePath) }
    else if(!zlp.exists || !zlp.isDirectory)
      task { Some("Could not find the directory " + zlp.absolutePath) }
    else
      myExec(
        "Compiling C library",
        "gcc -o " + libBinFilePath.absolutePath + " " +
	"-dynamiclib" + " " +
        "-I" + jdkIncludePath.absolutePath + " " +
	"-I" + frameworkPath + " " +
        "-I" + z3IncludePath(z3VN).absolutePath + " " +
        "-L" + z3LibPath(z3VN).absolutePath + " " +
        "-g -lc -lz3 -fPIC -O2 -fopenmp " +
        cFiles.getPaths.mkString(" ")
      )
  } dependsOn(javah)

  lazy val gccWin = {
    val z3VN = z3DefaultVersion
    val zip = z3IncludePath(z3VN)
    val zlp = z3LibPath(z3VN)
    if(!zip.exists || !zip.isDirectory)
      task { Some("Could not find the directory " + zip.absolutePath) }
    else if(!zlp.exists || !zlp.isDirectory)
      task { Some("Could not find the directory " + zlp.absolutePath) }
    else
      myExec(
        "Compiling C library",
	"gcc -shared -o " + libBinFilePath.absolutePath + " " +
	"-D_JNI_IMPLEMENTATION_ -Wl,--kill-at " +
	"-I " + "\"" + jdkIncludePath.absolutePath + "\"" + " " +
	"-I " + "\"" + jdkWinIncludePath.absolutePath + "\"" + " " +
	"-I " + z3IncludePath(z3VN).absolutePath + " " +
	cFiles.getPaths.mkString(" ") + " " +
	z3LibPath(z3VN).absolutePath
      )
  } dependsOn(javah)

  // Creates a task that runs a command in a separate process, and succeeds if
  // return code is 0.
  private def myExec(logMsg: String, command: String) = task {
    log.info(logMsg)
    log.info(command)
    val exitCode = command ! log
    if(exitCode == 0) None else Some("Non-zero exit code.")
  }

  lazy val cleanMore = task {
    log.info("Deleting generated checksum file")
    checksumFilePath.asFile.delete
    log.info("Deleting generated headers")
    for(f <- generatedHeaders.getFiles) {
      f.delete
    }
    log.info("Deleting binary lib")
    libBinFilePath.asFile.delete
    None
  }

  // Forces to use the jar at least once, which as a result copies the native
  // library to its temporary location. Hackish.
  lazy val loadLib = {
    val libStr = (buildLibraryJar.absolutePath).toString
    val scalaHomeStr = libStr.substring(0, libStr.length - 21)
    //log.info("PATH : " + this.jarPath.absolutePath)
    val scalaJars : PathFinder = (buildLibraryJar +++ buildCompilerJar)
    myExec(
      "Preloading library",
      "java -Dscala.home=" + scalaHomeStr +
	" -classpath " + scalaJars.absString +
	" scala.tools.nsc.MainGenericRunner " +
	" -classpath " + jarPath.absolutePath + ":.:" + scalaJars.absString +
	" -e z3.Z3Wrapper.init"
    )
  } dependsOn(`package`)

  private lazy val osInf : String = System.getProperty("os.name")
  private lazy val osArch : String = {
    val s = System.getProperty("sun.arch.data.model")
    if(s != null) s else {
      val a = System.getProperty("os.arch")
      if(a != null) a else ""
    }
  }

  private lazy val isUnix : Boolean = osInf.indexOf("nix") >= 0 || osInf.indexOf("nux") >= 0
  private lazy val isWindows : Boolean = osInf.indexOf("Win") >= 0
  private lazy val isMac : Boolean = osInf.indexOf("Mac") >= 0  
  private lazy val is32bit : Boolean = !is64bit
  private lazy val is64bit : Boolean = osArch.indexOf("64") >= 0

  lazy val computeChecksum = fileTask(List(checksumFilePath) from List(checksumSourcePath)) {
    import java.io.{File,InputStream,FileInputStream}
    import java.security.MessageDigest

    log.info("Generating library checksum")
    try {
      val f : File = checksumSourcePath.asFile
      val is : InputStream = new FileInputStream(f)
      val bytes = new Array[Byte](f.length.asInstanceOf[Int])
      var offset : Int = 0
      var read : Int = 0

      while(read >= 0 && offset < bytes.length) {
        read = is.read(bytes, offset, bytes.length - offset)
        if(read >= 0) offset += read
      }
      is.close

      val algo = MessageDigest.getInstance("MD5")
      algo.reset
      algo.update(bytes)
      val digest : Array[Byte] = algo.digest
      val strBuf = new StringBuffer()
      digest.foreach(b => strBuf.append(Integer.toHexString(0xFF & b)))
      val md5String : String = strBuf.toString

      val fw = new java.io.FileWriter(checksumFilePath.asFile)
      val nl = System.getProperty("line.separator")
      fw.write("// THIS FILE IS AUTOMATICALLY GENERATED, DO NOT EDIT" + nl)
      fw.write("package z3;" + nl)
      fw.write(nl)
      fw.write("public final class LibraryChecksum {" + nl)
      fw.write("  public static final String value = \"" + md5String + "\";" + nl)
      fw.write("}" + nl)
      fw.close
      log.info("Wrote checksum " + md5String + " as part of " + checksumFilePath.asFile + ".")
      None
    } catch {
      case e => Some("There was an error while generating the checksum file: " + e.getLocalizedMessage)
    }
  }
}
