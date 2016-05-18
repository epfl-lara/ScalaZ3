import sbt._
import Keys._
import org.eclipse.jgit.api._

object ScalaZ3Build extends Build {

  lazy val PS               = java.io.File.pathSeparator
  lazy val DS               = java.io.File.separator

  lazy val soName           = System.mapLibraryName("scalaz3")

  lazy val z3Name     = if (isMac) "libz3.dylib" else if (isWindows) "libz3.dll" else System.mapLibraryName("z3")
  lazy val javaZ3Name = if (isMac) "libz3java.dylib" else if (isWindows) "libz3java.dll" else System.mapLibraryName("z3java")

  lazy val libBinPath        = file("lib-bin")
  lazy val z3BinFilePath     = z3BuildPath / z3Name
  lazy val javaZ3BinFilePath = z3BuildPath / javaZ3Name
  lazy val libBinFilePath    = libBinPath / soName

  lazy val jdkIncludePath     = file(System.getProperty("java.home")) / ".." / "include"
  lazy val jdkUnixIncludePath = jdkIncludePath / "linux"
  lazy val jdkMacIncludePath  = jdkIncludePath / "darwin"
  lazy val jdkWinIncludePath  = jdkIncludePath / "win32"

  lazy val z3SourceRepo = "https://github.com/Z3Prover/z3.git"

  lazy val osInf: String = Option(System.getProperty("os.name")).getOrElse("")

  lazy val osArch: String = {
    Option(System.getProperty("sun.arch.data.model"))
      .orElse(Option(System.getProperty("os.arch")))
      .getOrElse("N/A")
  }

  lazy val is64b = osArch.indexOf("64") >= 0

  lazy val isUnix    = osInf.indexOf("nix") >= 0 || osInf.indexOf("nux") >= 0
  lazy val isWindows = osInf.indexOf("Win") >= 0
  lazy val isMac     = osInf.indexOf("Mac") >= 0

  lazy val z3BuildPath = file("z3") / "build"
  lazy val z3BinaryFiles = Seq(z3BuildPath / z3Name, z3BuildPath / javaZ3Name)
  lazy val z3JarFile = z3BuildPath / "com.microsoft.z3.jar"

  lazy val z3JavaDepsPrefixes = Seq(
    "com.microsoft.z3.Native",
    "com.microsoft.z3.Z3Exception",
    "com.microsoft.z3.enumerations"
  )

  def exec(cmd: String, s: TaskStreams): Unit = {
    s.log.info("$ "+cmd)
    cmd ! s.log
  }

  def exec(cmd: String, dir: File, s: TaskStreams): Unit = {
    s.log.info("$ cd " + dir + " && "+cmd)
    Process(cmd, dir) ! s.log
  }

  val z3Key       = TaskKey[Unit]("z3", "Compiles z3 sources")
  val gccKey      = TaskKey[Unit]("gcc", "Compiles the C sources")
  val checksumKey = TaskKey[String]("checksum", "Generates checksum file.")

  def listAllFiles(f: File): List[File] =
    f :: (if (f.isDirectory) f.listFiles().toList.flatMap(listAllFiles) else Nil)

  def hashFiles(files: List[File]): String = {
    import java.io.{File,InputStream,FileInputStream}
    import java.security.MessageDigest

    val algo = MessageDigest.getInstance("MD5")
    algo.reset

    for (f <- files.sortBy(_.absolutePath) if !f.isDirectory) {
      val is : InputStream = new FileInputStream(f)
      val bytes = new Array[Byte](f.length.asInstanceOf[Int])

      var offset : Int = 0
      var read : Int = 0
      while(read >= 0 && offset < bytes.length) {
        read = is.read(bytes, offset, bytes.length - offset)
        if(read >= 0) offset += read
      }
      is.close

      algo.update(f.absolutePath.getBytes)
      algo.update(bytes)
    }

    val digest : Array[Byte] = algo.digest
    val strBuf = new StringBuffer()
    digest.foreach(b => strBuf.append(Integer.toHexString(0xFF & b)))
    strBuf.toString
  }

  val z3Task = (streams) map { case s =>
    s.log.info("Compiling Z3 ...")

    val z3Path = file(".") / "z3"

    if (!z3Path.asFile.exists) {
      s.log.info("Cloning Z3 source repository ...")
      Git.cloneRepository()
        .setDirectory(z3Path.asFile)
        .setURI(z3SourceRepo)
        .call()
    }

    val hashFile = z3Path / ".build-hash"
    def computeHash(): String = {
      hashFiles(listAllFiles(z3Path.asFile).filter { f =>
        !f.getName.endsWith(".pyc") && !f.isHidden && !f.getName.startsWith(".")
      })
    }

    val initialHash = computeHash()
    s.log.info("New checksum is: " + initialHash)

    if (hashFile.exists && IO.read(hashFile) == initialHash) {
      s.log.info("Checksum matched previous, skipping build...")
    } else {
      if (isUnix) {
        val python = if (("which python2.7" #> file("/dev/null")).! == 0) "python2.7" else "python"

        exec(python + " scripts/mk_make.py --java", z3Path, s)
        exec("make", z3Path / "build", s)
      } else if (isWindows) {
        if (is64b) exec("python scripts/mk_make.py -x --java", z3Path, s)
        else exec("python scripts/mk_make.py --java", z3Path, s)
        exec("nmake", z3Path / "build", s)
      } else if (isMac) {
        exec("python scripts/mk_make.py --java", z3Path, s)
        exec("make", z3Path / "build", s)
      } else {
        error("Don't know how to compile Z3 on arch: "+osInf+" - "+osArch)
      }

      val finalHash = computeHash()
      IO.write(hashFile, finalHash)

      s.log.info("Wrote checksum " + finalHash + " for z3 build.")
    }
  }

  val checksumTask = (streams, sourceDirectory in Compile) map {
    case (s, sd) =>
      val checksumFilePath = sd / "java" / "z3" / "LibraryChecksum.java"

      val extensions = Set("java", "c", "h", "sbt", "properties")

      val checksumSourcePaths = listAllFiles(sd.asFile).filter { file =>
        val pathParts = file.getPath.split(".")
        pathParts.size > 1 && extensions(pathParts.last)
      }

      s.log.info("Generating library checksum")

      val md5String = hashFiles(checksumSourcePaths.map(_.asFile))

      val fw = new java.io.FileWriter(checksumFilePath.asFile)
      val nl = System.getProperty("line.separator")
      fw.write("// THIS FILE IS AUTOMATICALLY GENERATED, DO NOT EDIT" + nl)
      fw.write("package z3;" + nl)
      fw.write(nl)
      fw.write("public final class LibraryChecksum {" + nl)
      fw.write("  public static final String value = \"" + md5String + "\";" + nl)
      fw.write("}" + nl)
      fw.close

      s.log.info("Wrote checksum " + md5String + " as part of " + checksumFilePath.asFile + ".")

      md5String
  }

  val gccTask = (streams, checksumKey, z3Key).map { case (s, cs, _) =>
    s.log.info("Compiling dummy C sources ...")

    def extractDir(checksum: String): String = {
      System.getProperty("java.io.tmpdir") + DS + "SCALAZ3_" + checksum + DS + "lib-bin" + DS
    }

    // First, we look for z3
    for (file <- (z3BinaryFiles :+ z3JarFile) if !file.exists) error("Could not find Z3 : " + file.absolutePath)
    libBinFilePath.getParentFile.mkdirs()

    if (isUnix) {
      exec("gcc -std=gnu89 -o " + libBinFilePath.absolutePath + " " +
           "-shared -Wl,-soname," + soName + " " +
           "-I" + jdkIncludePath.absolutePath + " " +
           "-I" + jdkUnixIncludePath.absolutePath + " " +
           "-L" + z3BuildPath.absolutePath + " " +
           "-Wall " +
           "-g -lc " +
           "-Wl,-rpath,"+extractDir(cs)+" -Wl,--no-as-needed -Wl,--copy-dt-needed " +
           "-lz3 -fPIC -O2 -fopenmp", s)

    } else if (isWindows) {
      exec("gcc -std=gnu89 -shared -o " + libBinFilePath.absolutePath + " " +
           "-D_JNI_IMPLEMENTATION_ -Wl,--kill-at " +
           "-D__int64=\"long long\" " +
           "-I " + "\"" + jdkIncludePath.absolutePath + "\" " +
           "-I " + "\"" + jdkWinIncludePath.absolutePath + "\" " +
           "-Wreturn-type " +
           z3BinFilePath.absolutePath + "\" ", s)

    } else if (isMac) {
      val frameworkPath = "/System/Library/Frameworks/JavaVM.framework/Versions/Current/Headers"

      exec("install_name_tool -id @loader_path/"+z3Name+" "+z3BinFilePath.absolutePath, s)

      exec("gcc -std=gnu89 -o " + libBinFilePath.absolutePath + " " +
           "-dynamiclib" + " " +
           "-install_name "+extractDir(cs)+soName + " " +
           "-I" + jdkIncludePath.absolutePath + " " +
           "-I" + jdkMacIncludePath.absolutePath + " " +
           "-I" + frameworkPath + " " +
           "-L" + z3BuildPath.absolutePath + " " +
           "-g -lc " +
           "-Wl,-rpath,"+extractDir(cs)+" " +
           "-lz3 -fPIC -O2 -fopenmp", s)

    } else {
      error("Unknown arch: "+osInf+" - "+osArch)
    }
  }

  val packageTask = (Keys.`package` in Compile).dependsOn(gccKey)

  val newMappingsTask = mappings in (Compile, packageBin) <<= (mappings in (Compile, packageBin), streams) map {
    case (normalFiles, s) =>
      val newBinaryFiles = (libBinFilePath +: z3BinaryFiles).map { f => f.getAbsoluteFile -> ("lib-bin" + DS + f.getName) }

      s.log.info("Bundling binary files:")
      for ((from, to) <- newBinaryFiles) {
        s.log.info(" - "+from+" -> "+to)
      }

      s.log.info("Bunding relevant java-Z3 files:")
      val outputDir = new File(System.getProperty("java.io.tmpdir") + DS + "Z3JAR_jars" + DS)
      outputDir.delete
      outputDir.mkdirs
      IO.unzip(z3JarFile, outputDir)

      val z3JavaDepsMappings: Seq[(File, String)] = listAllFiles(outputDir).flatMap { f =>
        if (f.isDirectory) None else {
          import java.nio.file.Paths
          val path = Paths.get(outputDir.getAbsolutePath).relativize(Paths.get(f.getAbsolutePath)).toString
          val extensionSplit = path.split("\\.")
          if (extensionSplit.length < 2 || extensionSplit(1) != "class") None else {
            val classPath = extensionSplit(0).replace(DS, ".")
            if (z3JavaDepsPrefixes.exists(prefix => classPath.startsWith(prefix))) {
              s.log.info(" - " + classPath)
              Some(f.getAbsoluteFile -> path)
            } else {
              None
            }
          }
        }
      }

      newBinaryFiles ++ z3JavaDepsMappings ++ normalFiles
  }

  val testClasspath = internalDependencyClasspath in (Test) <<= (artifactPath in (Compile, packageBin)) map {
    case jar => List(Attributed.blank(jar))
  }

  lazy val root = Project(
    id = "ScalaZ3",
    base = file("."),
    settings = Project.defaultSettings ++ Seq(
      checksumKey <<= checksumTask,
      gccKey <<= gccTask,
      z3Key <<= z3Task,
      (Keys.`package` in Compile) <<= packageTask,
      (unmanagedJars in Compile) += Attributed.blank(z3JarFile),
      (compile in Compile) <<= (compile in Compile) dependsOn (checksumTask),
      (test in Test) <<= (test in Test) dependsOn (Keys.`package` in Compile),
      testClasspath,
      newMappingsTask
    )
  )
}
