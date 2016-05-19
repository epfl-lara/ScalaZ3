package z3.java;

import com.microsoft.z3.Native;
import com.microsoft.z3.Z3Exception;

public class Z3Model extends Z3Pointer {
    private final Z3Context context;

    protected Z3Model(Z3Context context) {
        super(0L);
        this.context = context;
    }

    public Z3AST eval(Z3AST ast, boolean completion) throws Z3Exception {
        if(this.value == 0L) {
            throw new IllegalStateException("The model is not initialized.");
        }
        Z3AST out = new Z3AST(0L);
        boolean result = Native.modelEval(context.value, this.value, ast.value, completion, out);
        if (result) {
            return out;
        } else {
            return null;
        }
    }

    public Z3AST eval(Z3AST ast) throws Z3Exception {
        return eval(ast, false);
    }

    public Integer evalAsInt(Z3AST ast) throws Z3Exception {
        Z3AST res = this.eval(ast);
        if(res == null) return null;
        return context.getNumeralInt(res);
    }

    public Boolean evalAsBool(Z3AST ast) throws Z3Exception {
        Z3AST res = this.eval(ast);
        if(res == null) return null;
        return context.getBoolValue(res);
    }

    public void incRef() throws Z3Exception {
        Native.modelIncRef(context.value, this.value);
    }

    public void decRef() throws Z3Exception {
        Native.modelDecRef(context.value, this.value);
    }
}
