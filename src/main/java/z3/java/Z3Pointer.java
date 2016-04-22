package z3.java;

import com.microsoft.z3.Native;

class Z3Pointer extends Native.LongPtr {
    protected Z3Pointer() {
        this(0L);
    }

    protected Z3Pointer(long ptr) {
        this.value = ptr;
    }
}
