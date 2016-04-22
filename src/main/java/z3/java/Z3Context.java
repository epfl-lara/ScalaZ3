package z3.java;

import z3.Z3Wrapper;
import java.util.Map;
import com.microsoft.z3.Native;

public class Z3Context extends Z3Pointer {
    public static Boolean lbool2Boolean(int v) {
        if(v == -1)
            return false;
        else if (v == 0)
            return null;
        else
            return true;
    }

    public Z3Context(Map<String, String> config) {
        synchronized(Z3Wrapper.creation_lock) {
            long cfgPtr = Native.mkConfig();
            for (Map.Entry<String, String> entry : config.entrySet()) {
              Native.setParamValue(cfgPtr, entry.getKey(), entry.getValue());
            }
            value = Native.mkContext(cfgPtr);
            Native.delConfig(cfgPtr);
        }
    }

    public void delete() {
        Native.delContext(this.value);
        this.value = 0;
    }

    public void updateParamValue(String paramID, String paramValue) {
        Native.updateParamValue(this.value, paramID, paramValue);
    }

    public Z3Symbol mkIntSymbol(int i) {
        return new Z3Symbol(Native.mkIntSymbol(this.value, i));
    }

    public Z3Symbol mkStringSymbol(String s) {
        return new Z3Symbol(Native.mkStringSymbol(this.value, s));
    }

    public boolean isEqSort(Z3Sort s1, Z3Sort s2) {
        return Native.isEqSort(this.value, s1.value, s2.value);
    }

    public Z3Sort mkUninterpretedSort(Z3Symbol s) {
        return new Z3Sort(Native.mkUninterpretedSort(this.value, s.value));
    }

    public Z3Sort mkBoolSort() {
        return new Z3Sort(Native.mkBoolSort(this.value));
    }

    public Z3Sort mkIntSort() {
        return new Z3Sort(Native.mkIntSort(this.value));
    }

    public Z3Sort mkRealSort() {
        return new Z3Sort(Native.mkRealSort(this.value));
    }

    public boolean isEqAST(Z3AST t1, Z3AST t2) {
        return Native.isEqAst(this.value, t1.value, t2.value);
    }

    public Z3AST mkConst(Z3Symbol symbol, Z3Sort sort) {
        return new Z3AST(Native.mkConst(this.value, symbol.value, sort.value));
    }

    public Z3AST mkTrue() {
        return new Z3AST(Native.mkTrue(this.value));
    }

    public Z3AST mkFalse() {
        return new Z3AST(Native.mkFalse(this.value));
    }

    public Z3AST mkEq(Z3AST ast1, Z3AST ast2) {
        return new Z3AST(Native.mkEq(this.value, ast1.value, ast2.value));
    }

    public Z3AST mkDistinct(Z3AST ... args) {
        if(args.length == 0)
            throw new IllegalArgumentException("mkDistinct needs at least one argument");
        return new Z3AST(Native.mkDistinct(this.value, args.length, Z3Wrapper.toPtrArray(args)));
    }

    public Z3AST mkNot(Z3AST ast) {
        return new Z3AST(Native.mkNot(this.value, ast.value));
    }

    public Z3AST mkITE(Z3AST t1, Z3AST t2, Z3AST t3) {
        return new Z3AST(Native.mkIte(this.value, t1.value, t2.value, t3.value));
    }

    public Z3AST mkIff(Z3AST t1, Z3AST t2) {
        return new Z3AST(Native.mkIff(this.value, t1.value, t2.value));
    }

    public Z3AST mkImplies(Z3AST t1, Z3AST t2) {
        return new Z3AST(Native.mkImplies(this.value, t1.value, t2.value));
    }

    public Z3AST mkXor(Z3AST t1, Z3AST t2) {
        return new Z3AST(Native.mkXor(this.value, t1.value, t2.value));
    }

    public Z3AST mkAnd(Z3AST ... args) {
        if(args.length == 0)
            throw new IllegalArgumentException("mkAnd needs at least one argument");
        return new Z3AST(Native.mkAnd(this.value, args.length, Z3Wrapper.toPtrArray(args)));
    }

    public Z3AST mkOr(Z3AST ... args) {
        if(args.length == 0)
            throw new IllegalArgumentException("mkOr needs at least one argument");
        return new Z3AST(Native.mkOr(this.value, args.length, Z3Wrapper.toPtrArray(args)));
    }

    public Z3AST mkAdd(Z3AST ... args) {
        if(args.length == 0)
            throw new IllegalArgumentException("mkAdd needs at least one argument");
        return new Z3AST(Native.mkAdd(this.value, args.length, Z3Wrapper.toPtrArray(args)));
    }

    public Z3AST mkMul(Z3AST ... args) {
        if(args.length == 0)
            throw new IllegalArgumentException("mkMul needs at least one argument");
        return new Z3AST(Native.mkMul(this.value, args.length, Z3Wrapper.toPtrArray(args)));
    }

    public Z3AST mkSub(Z3AST ... args) {
        if(args.length == 0)
            throw new IllegalArgumentException("mkSub needs at least one argument");
        return new Z3AST(Native.mkSub(this.value, args.length, Z3Wrapper.toPtrArray(args)));
    }

    public Z3AST mkUnaryMinus(Z3AST ast) {
        return new Z3AST(Native.mkUnaryMinus(this.value, ast.value));
    }

    public Z3AST mkDiv(Z3AST ast1, Z3AST ast2) {
        return new Z3AST(Native.mkDiv(this.value, ast1.value, ast2.value));
    }

    public Z3AST mkMod(Z3AST ast1, Z3AST ast2) {
        return new Z3AST(Native.mkMod(this.value, ast1.value, ast2.value));
    }

    public Z3AST mkRem(Z3AST ast1, Z3AST ast2) {
        return new Z3AST(Native.mkRem(this.value, ast1.value, ast2.value));
    }

    public Z3AST mkLT(Z3AST ast1, Z3AST ast2) {
        return new Z3AST(Native.mkLt(this.value, ast1.value, ast2.value));
    }

    public Z3AST mkLE(Z3AST ast1, Z3AST ast2) {
        return new Z3AST(Native.mkLe(this.value, ast1.value, ast2.value));
    }

    public Z3AST mkGT(Z3AST ast1, Z3AST ast2) {
        return new Z3AST(Native.mkGt(this.value, ast1.value, ast2.value));
    }

    public Z3AST mkGE(Z3AST ast1, Z3AST ast2) {
        return new Z3AST(Native.mkGe(this.value, ast1.value, ast2.value));
    }

    public Z3AST mkInt2Real(Z3AST ast) {
        return new Z3AST(Native.mkInt2real(this.value, ast.value));
    }

    public Z3AST mkReal2Int(Z3AST ast) {
        return new Z3AST(Native.mkReal2int(this.value, ast.value));
    }

    public Z3AST mkIsInt(Z3AST ast) {
        return new Z3AST(Native.mkIsInt(this.value, ast.value));
    }

    public Z3AST mkInt(int value, Z3Sort sort) {
        return new Z3AST(Native.mkInt(this.value, value, sort.value));
    }
    
    public Z3AST mkReal(double value, int numerator, int denominator) {
        return new Z3AST(Native.mkReal(this.value, numerator, denominator));
    }

    public Integer getNumeralInt(Z3AST ast) {
        Native.IntPtr ip = new Native.IntPtr();
        boolean res = Native.getNumeralInt(this.value, ast.value, ip);
        if(res)
            return ip.value;
        else
            return null;
    }

    public Boolean getBoolValue(Z3AST ast) {
        return lbool2Boolean(Native.getBoolValue(this.value, ast.value));
    }

}
