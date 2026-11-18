package z3;

import com.microsoft.z3.Native;
import z3.scala.Z3Context;

import java.lang.ref.WeakReference;
import java.util.HashMap;

/**
 * Entry point to the native Z3 library.
 *
 * The native libraries are provided by the official Z3 Java distribution
 * (<code>com.microsoft.z3</code>), which bundles them for all supported platforms and
 * extracts and loads the right ones when {@link Native} is initialized. The lookup can be
 * tuned with the system properties of that distribution, in particular:
 * <ul>
 *   <li><code>z3.skipLibraryLoad=true</code>: do not load anything, the application has
 *       already loaded <code>z3java</code> by itself;</li>
 *   <li><code>z3.nativeCache=false</code>: extract the libraries in a fresh temporary
 *       directory instead of the per-user cache;</li>
 *   <li><code>z3.nativeCacheDir</code>: location of that cache (<code>~/.z3/native</code>
 *       by default).</li>
 * </ul>
 * A <code>z3java</code> library found on <code>java.library.path</code> takes precedence
 * over the bundled ones.
 */
public final class Z3Wrapper {
    public static Object creation_lock = new Object();

    private static final String isDebug = System.getProperty("scalaz3.debug.load");

    // Forces the initialization of Native, and therefore the loading of the native libraries.
    // An UnsatisfiedLinkError is thrown if there are none for the current platform.
    static {
        debug("Z3 version: " + z3VersionString());
    }

    private static void debug(String msg) {
        if (isDebug != null) {
            System.out.println(msg);
        }
    }

    /**
     * Makes sure the native Z3 library is loaded.
     *
     * @throws UnsatisfiedLinkError if it could not be loaded (and NoClassDefFoundError
     *         on subsequent attempts).
     */
    public static void ensureLoaded() {
        // Nothing to do, getting here means the static initializer succeeded.
    }

    /**
     * Kept for compatibility: ScalaZ3 does not need to be run from its JAR anymore.
     * Like {@link #ensureLoaded()}, calling this forces the native library to be loaded.
     */
    @Deprecated
    public static boolean withinJar() {
        return true;
    }

    public static String wrapperVersionString() {
        // Version number should match smallest Z3 with which we know it to work, plus a letter for "internal" versions.
        return "ScalaZ3 5.4.1";
    }

    public static String z3VersionString() {
        Native.IntPtr major = new Native.IntPtr();
        Native.IntPtr minor = new Native.IntPtr();
        Native.IntPtr buildNumber = new Native.IntPtr();
        Native.IntPtr revisionNumber = new Native.IntPtr();
        Native.getVersion(major, minor, buildNumber, revisionNumber);
        return "Z3 " + major.value + "." + minor.value + " (build " + buildNumber.value + ", rev. " + revisionNumber.value + ")";
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
