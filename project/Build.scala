import sbt._
import sbt.Keys._
import org.eclipse.jgit.api._
import scala.sys.process._

object ScalaZ3Build {

  lazy val z3SourceRepo = "https://github.com/Z3Prover/z3.git"
  lazy val z3SourceTag  = "z3-4.7.1"

  lazy val PS = java.io.File.pathSeparator
  lazy val DS = java.io.File.separator

  lazy val soName = System.mapLibraryName("scalaz3")

  lazy val z3Name = {
    if (isMac) "libz3.dylib"
    else if (isWindows) "libz3.dll"
    else System.mapLibraryName("z3")
  }

  lazy val javaZ3Name = {
    if (isMac) "libz3java.dylib"
    else if (isWindows) "libz3java.dll"
    else System.mapLibraryName("z3java")
  }

  lazy val libBinPath        = file("lib-bin")
  lazy val z3BinFilePath     = z3BuildPath / z3Name
  lazy val javaZ3BinFilePath = z3BuildPath / javaZ3Name
  lazy val libBinFilePath    = libBinPath / soName

  lazy val jdkIncludePath     = file(System.getProperty("java.home")) / ".." / "include"
  lazy val jdkUnixIncludePath = jdkIncludePath / "linux"
  lazy val jdkMacIncludePath  = jdkIncludePath / "darwin"
  lazy val jdkWinIncludePath  = jdkIncludePath / "win32"

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

  lazy val z3Path = file(".") / "z3" / z3SourceTag
  lazy val z3BuildPath = z3Path / "build"
  lazy val z3BinaryFiles = Seq(z3BuildPath / z3Name, z3BuildPath / javaZ3Name)

  lazy val z3JavaDepsPrefixes = Seq(
    "com.microsoft.z3.Native",
    "com.microsoft.z3.Z3Exception",
    "com.microsoft.z3.enumerations",
  )

  def exec(cmd: String, s: TaskStreams): Int = {
    s.log.info("$ " + cmd)
    cmd ! s.log
  }

  def exec(cmd: String, dir: File, s: TaskStreams): Int = {
    s.log.info("$ cd " + dir + " && " + cmd)
    Process(cmd, dir) ! s.log
  }

  val z3Key       = TaskKey[String]("z3", "Compiles z3 sources")
  val gccKey      = TaskKey[Unit]("gcc", "Compiles the C sources")
  val checksumKey = TaskKey[String]("checksum", "Generates checksum file.")

  def listAllFiles(f: File): List[File] =
    f :: (if (f.isDirectory) f.listFiles().toList.flatMap(listAllFiles) else Nil)

  def hashFiles(files: List[File], base: String = ""): String = {
    import java.io.{File,InputStream,FileInputStream}
    import java.security.MessageDigest

    val algo = MessageDigest.getInstance("MD5")
    algo.reset
    algo.update(base.getBytes)

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

  lazy val z3JarFile = z3BuildPath / "com.microsoft.z3.jar"

  val z3Task = Def.task {
    val s = streams.value

    if (!z3Path.asFile.exists) {
      s.log.info(s"Cloning Z3 source repository to $z3Path...")
      Git.cloneRepository()
        .setDirectory(z3Path.asFile)
        .setURI(z3SourceRepo)
        .call()
    }

    Git.open(z3Path.asFile)
      .checkout()
      .setName(z3SourceTag)
      .call()

    val hashFile = z3Path / ".build-hash"

    def computeHash(): String = {
      hashFiles(listAllFiles((z3Path / "src").asFile).filter { f =>
        !f.getName.endsWith(".pyc") &&
        !f.isHidden &&
        !f.getName.startsWith(".") &&
        !f.getName.endsWith(".a")
      })
    }

    val initialHash = computeHash()
    s.log.info("Checksum of Z3 source file: " + initialHash)

    if (hashFile.exists && IO.read(hashFile).trim == initialHash.trim) {
      s.log.info("Checksum of Z3 source files matched previous, skipping build...")
      initialHash
    } else {
      s.log.info("Compiling Z3...")

      val code = if (isUnix) {
        val python = if (("which python2.7" #> file("/dev/null")).! == 0) "python2.7" else "python"

        val i1 = exec(python + " scripts/mk_make.py --java", z3Path, s)
        if (i1 != 0) i1 else exec("make", z3Path / "build", s)
      } else if (isWindows) {
        val i1 = if (is64b) exec("python scripts/mk_make.py -x --java", z3Path, s)
          else exec("python scripts/mk_make.py --java", z3Path, s)
        if (i1 != 0) i1 else exec("nmake", z3Path / "build", s)
      } else if (isMac) {
        val i1 = exec("python scripts/mk_make.py --java", z3Path, s)
        if (i1 != 0) i1 else exec("make", z3Path / "build", s)
      } else {
        sys.error("Don't know how to compile Z3 on arch: " + osInf + " - " + osArch)
      }

      if (code == 0) {
        val finalHash = computeHash()
        IO.write(hashFile, finalHash)

        s.log.info("Wrote checksum " + finalHash + " for z3 build.")
        finalHash
      } else {
        sys.error("Compilation of Z3 failed... aborting")
      }
    }
  }

  val checksumTask = Def.task {
    val s = streams.value
    val z3checksum = z3Key.value
    val sd = (Compile / sourceDirectory).value

    val checksumFilePath = sd / "java" / "z3" / "LibraryChecksum.java"

    val extensions = Set("java", "c", "h", "sbt", "properties")

    val checksumSourcePaths = listAllFiles(sd.asFile).filter { file =>
      val pathParts = file.getPath.split(".")
      pathParts.size > 1 && extensions(pathParts.last)
    }

    val md5String = hashFiles(checksumSourcePaths.map(_.asFile), z3checksum)
    s.log.info(s"Library checksum: $md5String")

    val regenerate = if (checksumFilePath.exists()) {
      val source = scala.io.Source.fromFile(checksumFilePath.asFile, "UTF-8")
      !source.getLines.exists(_.contains(md5String))
    } else {
      true
    }

    if (regenerate) {
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
    } else {
      s.log.info("Found library file with matching checksum.")
    }

    md5String
  }

  val gccTask = Def.task {
    val s = streams.value
    val cs = checksumKey.value

    s.log.info("Compiling dummy C sources ...")

    def extractDir(checksum: String): String = {
      System.getProperty("java.io.tmpdir") + DS + "SCALAZ3_" + checksum + DS + "lib-bin" + DS
    }

    // First, we look for z3
    for (file <- (z3BinaryFiles :+ z3JarFile) if !file.exists) {
      sys.error("Could not find Z3 : " + file.absolutePath)
    }

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
      exec("gcc -std=gnu89 -m64 -shared -o " + libBinFilePath.absolutePath + " " +
           "-D_JNI_IMPLEMENTATION_ -Wl,--kill-at " +
           "-D__int64=\"long long\" " +
           "-I " + "\"" + jdkIncludePath.absolutePath + "\" " +
           "-I " + "\"" + jdkWinIncludePath.absolutePath + "\" " +
           "-I " + "\"" + z3BinFilePath.getParentFile.absolutePath + "\" " +
           "-Wreturn-type ", s)

    } else if (isMac) {
      val frameworkPath = "/System/Library/Frameworks/JavaVM.framework/Versions/Current/Headers"

      exec("install_name_tool -id @loader_path/" + z3Name + " " + z3BinFilePath.absolutePath, s)
      exec("install_name_tool -id @loader_path/" + javaZ3Name + " " + javaZ3BinFilePath.absolutePath, s)
      // make the dependency to z3 be relative to the caller's location
      exec("install_name_tool -change " + z3Name + " @loader_path/" + z3Name + " " + javaZ3BinFilePath.absolutePath, s)

      exec("gcc -std=gnu89 -o " + libBinFilePath.absolutePath + " " +
           "-dynamiclib" + " " +
           "-install_name @loader_path/"+soName + " " +
           "-I" + jdkIncludePath.absolutePath + " " +
           "-I" + jdkMacIncludePath.absolutePath + " " +
           "-I" + frameworkPath + " " +
           "-L" + z3BuildPath.absolutePath + " " +
           "-g -lc " +
           "-Wl,-rpath," + extractDir(cs) + " " +
           "-lz3 -fPIC -O2", s)

    } else {
      sys.error("Unknown arch: " + osInf + " - " + osArch)
    }

    () // unit task!
  }

  val packageTask = (Compile / Keys.`package`).dependsOn(gccKey)

  val newMappingsTask = Def.task {
    val s = streams.value
    val normalFiles = (Compile / packageBin / mappings).value

    val newBinaryFiles = (libBinFilePath +: z3BinaryFiles).map { f =>
      f.getAbsoluteFile -> ("lib-bin" + DS + f.getName)
    }

    s.log.info("Bundling binary files:")
    for ((from, to) <- newBinaryFiles) {
      s.log.info(" - " + from+" -> " + to)
    }

    s.log.info("Bundling relevant java-Z3 files:")
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

  val testClasspath = Def.task {
    val jar = (Compile / packageBin / artifactPath).value
    Seq(Attributed.blank(jar))
  }
}
