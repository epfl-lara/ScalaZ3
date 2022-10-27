package z3;

import com.microsoft.z3.Native;
import z3.scala.Z3Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.security.CodeSource;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * This class contains all the native functions. It should be accessed
 * mostly through the other classes, though.
 */
public final class Z3Wrapper {
    // related to the path in the jar file
    private static final String DS = java.io.File.separator;

    private static final String LIB_BIN = DS + "lib-bin" + DS;

    public static Object creation_lock = new Object();

    private static final String versionString = LibraryChecksum.value;

    private static final String isDebug = System.getProperty("scalaz3.debug.load");

    // this is just to force class loading, and therefore library loading.
    static {
        if (!withinJar()) {
            System.err.println("It seems you are not running ScalaZ3 from its JAR");
            System.exit(1);
        }

        // We load the library by ourselves, so we kindly ask the Java Z3 bindings to not do so.
        System.setProperty("z3.skipLibraryLoad", "true");
        loadFromJar();
        // We run this to ensure class loading of Native.
        debug("Z3 version: " + z3VersionString());
    }

    private static void debug(String msg) {
        if (isDebug != null) {
            System.out.println(msg);
        }
    }

    public static boolean withinJar() {
        java.net.URL classJar = Z3Wrapper.class.getResource("/lib-bin/");
        return classJar != null;
    }

    public static String wrapperVersionString() {
        // Version number should match smallest Z3 with which we know it to work, plus a letter for "internal" versions.
        return "ScalaZ3 4.8.14a";
    }

    public static String z3VersionString() {
        Native.IntPtr major = new Native.IntPtr();
        Native.IntPtr minor = new Native.IntPtr();
        Native.IntPtr buildNumber = new Native.IntPtr();
        Native.IntPtr revisionNumber = new Native.IntPtr();
        Native.getVersion(major, minor, buildNumber, revisionNumber);
        return "Z3 " + major.value + "." + minor.value + " (build " + buildNumber.value + ", rev. " + revisionNumber.value + ")";
    }

    private static void loadFromJar() {
        String path = "SCALAZ3_" + versionString;
        File libDir = new File(System.getProperty("java.io.tmpdir") + DS + path + LIB_BIN);

        try {
            if (!libDir.isDirectory() || !libDir.canRead()) {
                libDir.mkdirs();
                extractFromJar(libDir);
            }

            System.load(libDir.getAbsolutePath() + DS + System.mapLibraryName("z3"));
            System.load(libDir.getAbsolutePath() + DS + System.mapLibraryName("z3java"));
            System.load(libDir.getAbsolutePath() + DS + System.mapLibraryName("scalaz3"));
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private static void extractFromJar(File toDir) throws Exception {
        CodeSource src = Z3Wrapper.class.getProtectionDomain().getCodeSource();
        if (src != null) {
            URL jar = src.getLocation();
            ZipInputStream zip = new ZipInputStream(jar.openStream());
            while (true) {
                ZipEntry e = zip.getNextEntry();
                if (e == null) break;

                String path = e.getName();

                if (path.startsWith("lib-bin/") && !e.isDirectory()) {

                    String name = new File(path).getName();

                    debug("Extracting " + path + " from jar to " + name + "...");

                    File to = new File(toDir.getAbsolutePath() + DS + name);

                    InputStream in = Z3Wrapper.class.getResourceAsStream("/" + path);
                    OutputStream out = new FileOutputStream(to);
                    byte[] buf = new byte[4096];
                    int len;
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                    out.close();
                    in.close();
                }
            }
        }

    }

    public static long[] toPtrArray(Native.LongPtr[] ptrs) {
        long[] result = new long[ptrs.length];
        for (int i = 0; i < ptrs.length; i++) {
            result[i] = ptrs[i].value;
        }
        return result;
    }

    private static final HashMap<Long, WeakReference<Z3Context>> ptrToCtx = new HashMap<>();

    public static void onZ3Error(long contextPtr, long code) {
        Z3Context ctx = ptrToCtx.get(contextPtr).get();
        ctx.onError(code);
    }

    public static void registerContext(long contextPtr, Z3Context ctx) {
        ptrToCtx.put(contextPtr, new WeakReference<>(ctx));
    }

    public static void unregisterContext(long contextPtr) {
        ptrToCtx.remove(contextPtr);
    }
}
