#include "z3_Z3Wrapper.h"
#include "z3_thycallbacks.h"
#include "casts.h"
#include "extra.h"
#include <z3.h>

#ifdef __cplusplus
extern "C" {
#endif
    JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkConfig (JNIEnv * env, jclass cls) {
        return configToJLong(Z3_mk_config());
    }

    JNIEXPORT void JNICALL Java_z3_Z3Wrapper_delConfig (JNIEnv * env, jclass cls, jlong configPtr) {
        Z3_config conf = asZ3Config(configPtr);
        Z3_del_config(conf);
        return;
    }

    JNIEXPORT void JNICALL Java_z3_Z3Wrapper_setParamValue(JNIEnv * env, jclass cls, jlong configPtr, jstring paramID, jstring paramValue) {
        const jbyte * str1;
        const jbyte * str2;
        str1 = (*env)->GetStringUTFChars(env, paramID, NULL);
        if (str1 == NULL) return;
        str2 = (*env)->GetStringUTFChars(env, paramValue, NULL);
        if (str2 == NULL) return;
        Z3_set_param_value(asZ3Config(configPtr), (const char*)str1, (const char*)str2);
        // (*env)->ReleaseStringUTFChars(env, paramID, str1);
        // (*env)->ReleaseStringUTFChars(env, paramValue, str2);
    }

    JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkContext (JNIEnv * env, jclass cls, jlong configPtr) {
        Z3_config conf = asZ3Config(configPtr);
        return contextToJLong(Z3_mk_context(conf));
    }

    JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkContextRC (JNIEnv * env, jclass cls, jlong configPtr) {
        Z3_config conf = asZ3Config(configPtr);
        return contextToJLong(Z3_mk_context_rc(conf));
    }


    JNIEXPORT void JNICALL Java_z3_Z3Wrapper_incRef (JNIEnv * env, jclass cls, jlong contextPtr, jlong ptr) {
        Z3_inc_ref(asZ3Context(contextPtr), asZ3AST(ptr));
    }

    JNIEXPORT void JNICALL Java_z3_Z3Wrapper_decRef (JNIEnv * env, jclass cls, jlong contextPtr, jlong ptr) {
        Z3_dec_ref(asZ3Context(contextPtr), asZ3AST(ptr));
    }


    JNIEXPORT void JNICALL Java_z3_Z3Wrapper_delContext (JNIEnv * env, jclass cls, jlong contextPtr) {
        Z3_context cont = asZ3Context(contextPtr);
        Z3_del_context(cont);
        return;
    }

    JNIEXPORT void JNICALL Java_z3_Z3Wrapper_softCheckCancel (JNIEnv * env, jclass cls, jlong contextPtr) {
        Z3_soft_check_cancel(asZ3Context(contextPtr));
        return;
    }

    JNIEXPORT void JNICALL Java_z3_Z3Wrapper_toggleWarningMessages (JNIEnv * env, jclass cls, jboolean enabled) {
        Z3_toggle_warning_messages((Z3_bool)enabled);
    }

    JNIEXPORT void JNICALL Java_z3_Z3Wrapper_updateParamValue(JNIEnv * env, jclass cls, jlong configPtr, jstring paramID, jstring paramValue) {
        const jbyte * str1;
        const jbyte * str2;
        str1 = (*env)->GetStringUTFChars(env, paramID, NULL);
        if (str1 == NULL) return;
        str2 = (*env)->GetStringUTFChars(env, paramValue, NULL);
        if (str2 == NULL) return;
        Z3_set_param_value(asZ3Config(configPtr), (const char*)str1, (const char*)str2);
    }

    JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkIntSymbol (JNIEnv * env, jclass cls, jlong contextPtr, jint i) {
        Z3_context cont = asZ3Context(contextPtr);
        return symbolToJLong(Z3_mk_int_symbol(cont, (int)i));
    }

    JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkStringSymbol (JNIEnv * env, jclass cls, jlong contextPtr, jstring s) {
        Z3_context cont = asZ3Context(contextPtr);
        const jbyte * str;
        str = (*env)->GetStringUTFChars(env, s, NULL);
        if (str == NULL) return JLONG_MY_NULL;
        return symbolToJLong(Z3_mk_string_symbol(cont, (const char*)str));
    }

    JNIEXPORT jboolean JNICALL Java_z3_Z3Wrapper_isEqSort (JNIEnv * env, jclass cls, jlong contextPtr, jlong sortPtr1, jlong sortPtr2) {
        int result = Z3_is_eq_sort(asZ3Context(contextPtr), asZ3Sort(sortPtr1), asZ3Sort(sortPtr2));
        return (result == 0 ? JNI_FALSE : JNI_TRUE);
    }

    JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkUninterpretedSort (JNIEnv * env, jclass cls, jlong contextPtr, jlong symbolPtr) {
        return sortToJLong(Z3_mk_uninterpreted_sort(asZ3Context(contextPtr), asZ3Symbol(symbolPtr)));
    }

    JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkBoolSort (JNIEnv * env, jclass cls, jlong contextPtr) {
        return sortToJLong(Z3_mk_bool_sort(asZ3Context(contextPtr)));
    }

    JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkIntSort (JNIEnv * env, jclass cls, jlong contextPtr) {
        return sortToJLong(Z3_mk_int_sort(asZ3Context(contextPtr)));
    }

    JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkRealSort (JNIEnv * env, jclass cls, jlong contextPtr) {
        return sortToJLong(Z3_mk_real_sort(asZ3Context(contextPtr)));
    }

    JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkConstructor (JNIEnv * env, jclass cls, jlong contextPtr, jlong symbolPtr1, jlong symbolPtr2, jint numFields, jlongArray fieldNames, jlongArray sorts, jintArray sortRefs) {
        Z3_symbol * fieldnms = (Z3_symbol*)malloc(numFields * sizeof(Z3_symbol));
        Z3_sort   * fieldsrs = (Z3_sort*)malloc(numFields * sizeof(Z3_sort));
        unsigned  * fieldsor = (unsigned*)malloc(numFields * sizeof(unsigned));
        jlong * jfna = (*env)->GetLongArrayElements(env, fieldNames, NULL);
        jlong * jfsr = (*env)->GetLongArrayElements(env, sorts, NULL);
        jint  * jsor = (*env)->GetIntArrayElements(env, sortRefs, NULL);
        int i = 0;
        jlong result;

        if(jfna == 0 || jfsr == 0) return 0;
        for(i = 0; i < numFields; ++i) {
            fieldnms[i] = asZ3Symbol(jfna[i]);
            fieldsrs[i] = asZ3Sort(jfsr[i]);
            fieldsor[i] = (unsigned)jsor[i];
        }

        result = constructorToJLong(Z3_mk_constructor(asZ3Context(contextPtr), asZ3Symbol(symbolPtr1), asZ3Symbol(symbolPtr2), (unsigned)numFields, fieldnms, fieldsrs, fieldsor));

        (*env)->ReleaseLongArrayElements(env, fieldNames, jfna, JNI_ABORT);
        (*env)->ReleaseLongArrayElements(env, sorts, jfsr, JNI_ABORT);
        (*env)->ReleaseIntArrayElements(env, sortRefs, jsor, JNI_ABORT);
        free(fieldnms);
        free(fieldsrs);
        free(fieldsor);

        return result;
    }

    JNIEXPORT void JNICALL Java_z3_Z3Wrapper_queryConstructor (JNIEnv * env, jclass cls, jlong contextPtr, jlong constructorPtr, jint numFields, jobject consFun, jobject testFun, jlongArray selectors) {
        Z3_func_decl consPtr;
        Z3_func_decl testPtr;
        Z3_func_decl * accPtrs;
        jlong * accPtrsCopy;
        int i = 0;
        jclass mc1;
        jfieldID fid1;
        jclass mc2;
        jfieldID fid2;

        if(numFields > 0) {
            accPtrs = (Z3_func_decl*)malloc(numFields * sizeof(Z3_func_decl));
            accPtrsCopy = (jlong*)malloc(numFields * sizeof(jlong));
        } else {
            accPtrs = NULL;
            accPtrsCopy = NULL;
        }

        Z3_query_constructor(asZ3Context(contextPtr), asZ3Constructor(constructorPtr), (unsigned)numFields, &consPtr, &testPtr, accPtrs);

        mc1  = (*env)->GetObjectClass(env, consFun);
        fid1 = (*env)->GetFieldID(env, mc1, "ptr", "J");
        (*env)->SetLongField(env, consFun, fid1, funcDeclToJLong(consPtr));
        mc2  = (*env)->GetObjectClass(env, testFun);
        fid2 = (*env)->GetFieldID(env, mc2, "ptr", "J");
        (*env)->SetLongField(env, testFun, fid2, funcDeclToJLong(testPtr));

        if(numFields > 0) {
            for (i = 0; i < numFields; ++i) {
                accPtrsCopy[i] = funcDeclToJLong(accPtrs[i]);
            }
            (*env)->SetLongArrayRegion(env, selectors, 0, (jsize)numFields, accPtrsCopy);
            free(accPtrs);
            free(accPtrsCopy);
        }
    }


    JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkConstructorList (JNIEnv * env, jclass cls, jlong contextPtr, jint arrSize, jlongArray constructors) {
        Z3_constructor * conss = (Z3_constructor*)malloc(arrSize * sizeof(Z3_constructor));
        jlong * jcst = (*env)->GetLongArrayElements(env, constructors, NULL);
        int i = 0;
        jlong result;

        if(jcst == 0) return 0;
        for(i = 0; i < arrSize; ++i) {
            conss[i] = asZ3Constructor(jcst[i]);
        }

        (*env)->ReleaseLongArrayElements(env, constructors, jcst, JNI_ABORT);

        result = constructorListToJLong(Z3_mk_constructor_list(asZ3Context(contextPtr), (unsigned)arrSize, conss));
        free(conss);
        return result;
    }

    JNIEXPORT void JNICALL Java_z3_Z3Wrapper_delConstructorList (JNIEnv * env, jclass cls, jlong contextPtr, jlong consListPtr) {
        Z3_del_constructor_list(asZ3Context(contextPtr), asZ3ConstructorList(consListPtr));
    }

    JNIEXPORT jlongArray JNICALL Java_z3_Z3Wrapper_mkDatatypes (JNIEnv * env, jclass cls, jlong contextPtr, jint numSorts, jlongArray sortNames, jlongArray constructorLists) {
        Z3_symbol * cSortNames = (Z3_symbol*)malloc(numSorts * sizeof(Z3_symbol));
        Z3_sort * cSorts = (Z3_sort*)malloc(numSorts * sizeof(Z3_sort));
        Z3_constructor_list * cCL = (Z3_constructor_list*)malloc(numSorts * sizeof(Z3_constructor_list));

        jlong * jsns = (*env)->GetLongArrayElements(env, sortNames, NULL);
        jlong * jcls = (*env)->GetLongArrayElements(env, constructorLists, NULL);
        int i = 0;
        jlongArray sorts;
        jlong * jsorts;

        if(jsns == 0 || jcls == 0) {
            fprintf(stderr, "Could not access java arrays in mkDatatypes.\n");
            return (jlongArray)NULL;
        }

        for(i = 0; i < numSorts; ++i) {
            cSortNames[i] = asZ3Symbol(jsns[i]);
            cCL[i]        = asZ3ConstructorList(jcls[i]);
        }
        Z3_mk_datatypes(asZ3Context(contextPtr), (unsigned)numSorts, cSortNames, cSorts, cCL);
        (*env)->ReleaseLongArrayElements(env, sortNames, jsns, JNI_ABORT);
        (*env)->ReleaseLongArrayElements(env, constructorLists, jcls, JNI_ABORT);
        free(cSortNames);
        free(cCL);

        // now we still need to build and return an array of sorts.
        sorts = (*env)->NewLongArray(env, (jsize)numSorts);
        jsorts = (jlong*)malloc(numSorts * sizeof(jlong));
        for(i = 0; i < numSorts; ++i) {
            jsorts[i] = sortToJLong(cSorts[i]);
        }
        (*env)->SetLongArrayRegion(env, sorts, 0, (jsize)numSorts, jsorts);
        free(cSorts);
        free(jsorts);
        return sorts;
    }

    JNIEXPORT jboolean JNICALL Java_z3_Z3Wrapper_isEqAST (JNIEnv * env, jclass cls, jlong contextPtr, jlong astPtr1, jlong astPtr2) {
        Z3_bool result = Z3_is_eq_ast(asZ3Context(contextPtr), asZ3AST(astPtr1), asZ3AST(astPtr2));
        if(result == Z3_TRUE)
            return JNI_TRUE;
        else
            return JNI_FALSE;
    }

    JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkApp (JNIEnv * env, jclass cls, jlong contextPtr, jlong funcDeclPtr, jint argCount, jlongArray args) {
        Z3_ast * nargs = (Z3_ast*)malloc(argCount * sizeof(Z3_ast));
        jlong * jargs = (*env)->GetLongArrayElements(env, args, NULL);
        int i = 0;
        jlong result;

        if(jargs == 0) return 0;

        for(i = 0; i < argCount; ++i) {
            nargs[i] = asZ3AST(jargs[i]);
        }
        (*env)->ReleaseLongArrayElements(env, args, jargs, 0);
        result = astToJLong(Z3_mk_app(asZ3Context(contextPtr), asZ3FuncDecl(funcDeclPtr), (unsigned)argCount, nargs));
        free(nargs);
        return result;
    }

    JNIEXPORT jboolean JNICALL Java_z3_Z3Wrapper_isEqFuncDecl (JNIEnv * env, jclass cls, jlong contextPtr, jlong fdPtr1, jlong fdPtr2) {
        Z3_bool result = Z3_is_eq_func_decl(asZ3Context(contextPtr), asZ3FuncDecl(fdPtr1), asZ3FuncDecl(fdPtr2));
        if(result == Z3_TRUE)
            return JNI_TRUE;
        else
            return JNI_FALSE;
    }

    JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkConst (JNIEnv * env, jclass cls, jlong contextPtr, jlong symbolPtr, jlong sortPtr) {
        return astToJLong(Z3_mk_const(asZ3Context(contextPtr), asZ3Symbol(symbolPtr), asZ3Sort(sortPtr)));
    }

    JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkFuncDecl (JNIEnv * env, jclass cls, jlong contextPtr, jlong symbolPtr, jint domainSize, jlongArray domainSortPtrs, jlong rangeSortPtr) {
        Z3_sort * nargs = (Z3_sort*)malloc(domainSize * sizeof(Z3_sort));
        jlong   * jargs = (*env)->GetLongArrayElements(env, domainSortPtrs, NULL);
        int i = 0;
        jlong result;

        if(jargs == 0) return 0;

        for(i = 0; i < domainSize; ++i) {
            nargs[i] = asZ3Sort(jargs[i]);
        }
        (*env)->ReleaseLongArrayElements(env, domainSortPtrs, jargs, 0);
        result = funcDeclToJLong(Z3_mk_func_decl(asZ3Context(contextPtr), asZ3Symbol(symbolPtr), (unsigned)domainSize, nargs, asZ3Sort(rangeSortPtr)));
        free(nargs);
        return result;
    }

    JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkFreshConst (JNIEnv * env, jclass cls, jlong contextPtr, jstring prefix, jlong sortPtr) {
        const jbyte * str = (*env)->GetStringUTFChars(env, prefix, NULL);
        jlong result;

        if (str == NULL) {
            fprintf(stderr, "Could not allow memory for string in mkFreshConst.\n");   
            return JLONG_MY_NULL;
        }

        result = astToJLong(Z3_mk_fresh_const(asZ3Context(contextPtr), (const char*)str, asZ3Sort(sortPtr)));
        (*env)->ReleaseStringUTFChars(env, prefix, str);
        return result;
    }

    JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkFreshFuncDecl (JNIEnv * env, jclass cls, jlong contextPtr, jstring prefix, jint domainSize, jlongArray domainSortPtrs, jlong rangeSortPtr) {
        const jbyte * str = (*env)->GetStringUTFChars(env, prefix, NULL);
        Z3_sort * nargs = (Z3_sort*)malloc(domainSize * sizeof(Z3_sort));
        jlong   * jargs = (*env)->GetLongArrayElements(env, domainSortPtrs, NULL);
        int i = 0;
        jlong result;

        if (str == NULL) return JLONG_MY_NULL;
        if(jargs == 0) return 0;

        for(i = 0; i < domainSize; ++i) {
            nargs[i] = asZ3Sort(jargs[i]);
        }
        (*env)->ReleaseLongArrayElements(env, domainSortPtrs, jargs, 0);
        result = funcDeclToJLong(Z3_mk_fresh_func_decl(asZ3Context(contextPtr), (const char*)str, (unsigned)domainSize, nargs, asZ3Sort(rangeSortPtr)));
        free(nargs);
        return result;
    }

    JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkTrue (JNIEnv * env, jclass cls, jlong contextPtr) {
        return astToJLong(Z3_mk_true(asZ3Context(contextPtr)));
    }

    JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkFalse (JNIEnv * env, jclass cls, jlong contextPtr) {
        return astToJLong(Z3_mk_false(asZ3Context(contextPtr)));
    }

    JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkEq (JNIEnv * env, jclass cls, jlong contextPtr, jlong astPtr1, jlong astPtr2) {
        return astToJLong(Z3_mk_eq(asZ3Context(contextPtr), asZ3AST(astPtr1), asZ3AST(astPtr2)));
    }

    JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkDistinct (JNIEnv * env, jclass cls, jlong contextPtr, jint argCount, jlongArray args) {
        Z3_ast * nargs = (Z3_ast*)malloc(argCount * sizeof(Z3_ast));
        jlong * jargs = (*env)->GetLongArrayElements(env, args, NULL);
        int i = 0;
        jlong result;

        if(jargs == 0) return 0;
        for(i = 0; i < argCount; ++i) {
            nargs[i] = asZ3AST(jargs[i]);
        }
        (*env)->ReleaseLongArrayElements(env, args, jargs, 0);
        result = astToJLong(Z3_mk_distinct(asZ3Context(contextPtr), (unsigned)argCount, nargs));
        free(nargs);
        return result;
    }

    JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkNot (JNIEnv * env, jclass cls, jlong contextPtr, jlong astPtr) {
        return astToJLong(Z3_mk_not(asZ3Context(contextPtr), asZ3AST(astPtr)));
    }

    JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkITE (JNIEnv * env, jclass cls, jlong contextPtr, jlong astPtr1, jlong astPtr2, jlong astPtr3) {
        return astToJLong(Z3_mk_ite(asZ3Context(contextPtr), asZ3AST(astPtr1), asZ3AST(astPtr2), asZ3AST(astPtr3)));
    }

    JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkIff (JNIEnv * env, jclass cls, jlong contextPtr, jlong astPtr1, jlong astPtr2) {
        return astToJLong(Z3_mk_iff(asZ3Context(contextPtr), asZ3AST(astPtr1), asZ3AST(astPtr2)));
    }

    JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkImplies (JNIEnv * env, jclass cls, jlong contextPtr, jlong astPtr1, jlong astPtr2) {
        return astToJLong(Z3_mk_implies(asZ3Context(contextPtr), asZ3AST(astPtr1), asZ3AST(astPtr2)));
    }

    JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkXor (JNIEnv * env, jclass cls, jlong contextPtr, jlong astPtr1, jlong astPtr2) {
        return astToJLong(Z3_mk_xor(asZ3Context(contextPtr), asZ3AST(astPtr1), asZ3AST(astPtr2)));
    }

    JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkAnd (JNIEnv * env, jclass cls, jlong contextPtr, jint argCount, jlongArray args) {
        Z3_ast * nargs = (Z3_ast*)malloc(argCount * sizeof(Z3_ast*));
        jlong * jargs = (*env)->GetLongArrayElements(env, args, NULL);
        int i = 0;
        jlong result;

        if(jargs == 0) return 0;
        for(i = 0; i < argCount; ++i) {
            nargs[i] = asZ3AST(jargs[i]);
        }
        (*env)->ReleaseLongArrayElements(env, args, jargs, JNI_ABORT);
        result = astToJLong(Z3_mk_and(asZ3Context(contextPtr), (unsigned)argCount, nargs));
        free(nargs);
        return result;
    }

    JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkOr (JNIEnv * env, jclass cls, jlong contextPtr, jint argCount, jlongArray args) {
        Z3_ast * nargs = (Z3_ast*)malloc(argCount * sizeof(Z3_ast*));
        jlong * jargs = (*env)->GetLongArrayElements(env, args, NULL);
        int i = 0;
        jlong result;

        if(jargs == 0) return 0;
        for(i = 0; i < argCount; ++i) {
            nargs[i] = asZ3AST(jargs[i]);
        }
        (*env)->ReleaseLongArrayElements(env, args, jargs, 0);
        result = astToJLong(Z3_mk_or(asZ3Context(contextPtr), (unsigned)argCount, nargs));
        free(nargs);
        return result;
    }

    JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkAdd (JNIEnv * env, jclass cls, jlong contextPtr, jint argCount, jlongArray args) {
        Z3_ast * nargs = (Z3_ast*)malloc(argCount * sizeof(Z3_ast*));
        jlong * jargs = (*env)->GetLongArrayElements(env, args, NULL);
        int i = 0;
        jlong result;

        if(jargs == 0) return 0;
        for(i = 0; i < argCount; ++i) {
            nargs[i] = asZ3AST(jargs[i]);
        }
        (*env)->ReleaseLongArrayElements(env, args, jargs, 0);
        result = astToJLong(Z3_mk_add(asZ3Context(contextPtr), (unsigned)argCount, nargs));
        free(nargs);
        return result;
    }

    JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkMul (JNIEnv * env, jclass cls, jlong contextPtr, jint argCount, jlongArray args) {
        Z3_ast * nargs = (Z3_ast*)malloc(argCount * sizeof(Z3_ast*));
        jlong * jargs = (*env)->GetLongArrayElements(env, args, NULL);
        jlong result;
        int i = 0;

        if(jargs == 0) return 0;
        for(i = 0; i < argCount; ++i) {
            nargs[i] = asZ3AST(jargs[i]);
        }
        (*env)->ReleaseLongArrayElements(env, args, jargs, 0);
        result = astToJLong(Z3_mk_mul(asZ3Context(contextPtr), (unsigned)argCount, nargs));
        free(nargs);
        return result;
    }

    JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkSub (JNIEnv * env, jclass cls, jlong contextPtr, jint argCount, jlongArray args) {
        Z3_ast * nargs = (Z3_ast*)malloc(argCount * sizeof(Z3_ast*));
        jlong * jargs = (*env)->GetLongArrayElements(env, args, NULL);
        int i = 0;
        jlong result;

        if(jargs == 0) return 0;
        for(i = 0; i < argCount; ++i) {
            nargs[i] = asZ3AST(jargs[i]);
        }
        (*env)->ReleaseLongArrayElements(env, args, jargs, 0);
        result = astToJLong(Z3_mk_sub(asZ3Context(contextPtr), (unsigned)argCount, nargs));
        free(nargs);
        return result;
    }

    JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkUnaryMinus (JNIEnv * env, jclass cls, jlong contextPtr, jlong astPtr) {
        return astToJLong(Z3_mk_unary_minus(asZ3Context(contextPtr), asZ3AST(astPtr)));
    }

    JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkDiv (JNIEnv * env, jclass cls, jlong contextPtr, jlong astPtr1, jlong astPtr2) {
        return astToJLong(Z3_mk_div(asZ3Context(contextPtr), asZ3AST(astPtr1), asZ3AST(astPtr2)));
    }

    JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkMod (JNIEnv * env, jclass cls, jlong contextPtr, jlong astPtr1, jlong astPtr2) {
        return astToJLong(Z3_mk_mod(asZ3Context(contextPtr), asZ3AST(astPtr1), asZ3AST(astPtr2)));
    }

    JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkRem (JNIEnv * env, jclass cls, jlong contextPtr, jlong astPtr1, jlong astPtr2) {
        return astToJLong(Z3_mk_rem(asZ3Context(contextPtr), asZ3AST(astPtr1), asZ3AST(astPtr2)));
    }

    JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkLT (JNIEnv * env, jclass cls, jlong contextPtr, jlong astPtr1, jlong astPtr2) {
        return astToJLong(Z3_mk_lt(asZ3Context(contextPtr), asZ3AST(astPtr1), asZ3AST(astPtr2)));
    }

    JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkLE (JNIEnv * env, jclass cls, jlong contextPtr, jlong astPtr1, jlong astPtr2) {
        return astToJLong(Z3_mk_le(asZ3Context(contextPtr), asZ3AST(astPtr1), asZ3AST(astPtr2)));
    }

    JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkGT (JNIEnv * env, jclass cls, jlong contextPtr, jlong astPtr1, jlong astPtr2) {
        return astToJLong(Z3_mk_gt(asZ3Context(contextPtr), asZ3AST(astPtr1), asZ3AST(astPtr2)));
    }

    JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkGE (JNIEnv * env, jclass cls, jlong contextPtr, jlong astPtr1, jlong astPtr2) {
        return astToJLong(Z3_mk_ge(asZ3Context(contextPtr), asZ3AST(astPtr1), asZ3AST(astPtr2)));
    }

    JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkInt2Real (JNIEnv * env, jclass cls, jlong contextPtr, jlong astPtr) {
        return astToJLong(Z3_mk_int2real(asZ3Context(contextPtr), asZ3AST(astPtr)));
    }

    JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkReal2Int (JNIEnv * env, jclass cls, jlong contextPtr, jlong astPtr) {
        return astToJLong(Z3_mk_real2int(asZ3Context(contextPtr), asZ3AST(astPtr)));
    }

    JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkIsInt (JNIEnv * env, jclass cls, jlong contextPtr, jlong astPtr) {
        return astToJLong(Z3_mk_is_int(asZ3Context(contextPtr), asZ3AST(astPtr)));
    }

    JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkArraySort (JNIEnv * env, jclass cls, jlong contextPtr, jlong domainSortPtr, jlong rangeSortPtr) {
        return sortToJLong(Z3_mk_array_sort(asZ3Context(contextPtr), asZ3Sort(domainSortPtr), asZ3Sort(rangeSortPtr)));
    }

    JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkSelect (JNIEnv * env, jclass cls, jlong contextPtr, jlong arrayASTPtr, jlong indexASTPtr) {
        return astToJLong(Z3_mk_select(asZ3Context(contextPtr), asZ3AST(arrayASTPtr), asZ3AST(indexASTPtr)));
    }

    JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkStore (JNIEnv * env, jclass cls, jlong contextPtr, jlong arrayASTPtr, jlong indexASTPtr, jlong valueASTPtr) {
        return astToJLong(Z3_mk_store(asZ3Context(contextPtr), asZ3AST(arrayASTPtr), asZ3AST(indexASTPtr), asZ3AST(valueASTPtr)));
    }

    JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkConstArray (JNIEnv * env, jclass cls, jlong contextPtr, jlong sortPtr, jlong valueASTPtr) {
        return astToJLong(Z3_mk_const_array(asZ3Context(contextPtr), asZ3Sort(sortPtr), asZ3AST(valueASTPtr)));
    }

    JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkArrayDefault (JNIEnv * env, jclass cls, jlong contextPtr, jlong arrayASTPtr) {
        return astToJLong(Z3_mk_array_default(asZ3Context(contextPtr), asZ3AST(arrayASTPtr)));
    }

    JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkTupleSort (JNIEnv * env, jclass cls, jlong contextPtr, jlong symbolPtr, jint numFields, jlongArray fieldNames, jlongArray fieldSorts, jobject consFunPtr, jlongArray projFunPtrs) {
        Z3_func_decl consFuncDecl;
        Z3_func_decl * projFuncDecls;
        Z3_symbol * cFieldSymbols;
        Z3_sort * cFieldSorts;
        jlong * fsyms = (*env)->GetLongArrayElements(env, fieldNames, NULL);
        jlong * fsorts = (*env)->GetLongArrayElements(env, fieldSorts, NULL);
        jlong * newProjFuns;
        jlong newSortPtr;
        int i = 0;
        int sz = (int)numFields;
        jclass mc;
        jfieldID fid;

        if(sz > 0) {
            cFieldSymbols = (Z3_symbol*)malloc(sz * sizeof(Z3_symbol));
            cFieldSorts = (Z3_sort*)malloc(sz * sizeof(Z3_sort));
            projFuncDecls = (Z3_func_decl*)malloc(sz * sizeof(Z3_func_decl));
            newProjFuns = (jlong*)malloc(sz * sizeof(jlong));
        }

        for(i = 0; i < sz; ++i) {
            cFieldSymbols[i] = asZ3Symbol(fsyms[i]);
            cFieldSorts[i] = asZ3Sort(fsorts[i]);
        }

        (*env)->ReleaseLongArrayElements(env, fieldNames, fsyms, JNI_ABORT);
        (*env)->ReleaseLongArrayElements(env, fieldSorts, fsorts, JNI_ABORT);

        newSortPtr = sortToJLong(Z3_mk_tuple_sort(asZ3Context(contextPtr), asZ3Symbol(symbolPtr), sz, cFieldSymbols, cFieldSorts, &consFuncDecl, projFuncDecls));

        mc = (*env)->GetObjectClass(env, consFunPtr);
        fid = (*env)->GetFieldID(env, mc, "ptr", "J");
        (*env)->SetLongField(env, consFunPtr, fid, funcDeclToJLong(consFuncDecl));

        if(sz > 0) {
          for(i = 0; i < sz; ++i) {
              newProjFuns[i] = funcDeclToJLong(projFuncDecls[i]);
          }
          (*env)->SetLongArrayRegion(env, projFunPtrs, 0, (jsize)sz, newProjFuns);
          free(cFieldSymbols);
          free(cFieldSorts);
          free(projFuncDecls);
          free(newProjFuns);
        }

        return newSortPtr;
    }
    

    JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkSetSort (JNIEnv * env, jclass cls, jlong contextPtr, jlong sortPtr) {
        return sortToJLong(Z3_mk_set_sort(asZ3Context(contextPtr), asZ3Sort(sortPtr)));
    }

    JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkEmptySet (JNIEnv * env, jclass cls, jlong contextPtr, jlong sortPtr) {
        return astToJLong(Z3_mk_empty_set(asZ3Context(contextPtr), asZ3Sort(sortPtr)));
    }

    JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkFullSet (JNIEnv * env, jclass cls, jlong contextPtr, jlong sortPtr) {
        return astToJLong(Z3_mk_full_set(asZ3Context(contextPtr), asZ3Sort(sortPtr)));
    }

    JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkSetAdd (JNIEnv * env, jclass cls, jlong contextPtr, jlong setPtr, jlong elemPtr) {
        return astToJLong(Z3_mk_set_add(asZ3Context(contextPtr), asZ3AST(setPtr), asZ3AST(elemPtr)));
    }

    JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkSetDel (JNIEnv * env, jclass cls, jlong contextPtr, jlong setPtr, jlong elemPtr) {
        return astToJLong(Z3_mk_set_del(asZ3Context(contextPtr), asZ3AST(setPtr), asZ3AST(elemPtr)));
    }

    JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkSetUnion (JNIEnv * env, jclass cls, jlong contextPtr, jint argCount, jlongArray args) {
        Z3_ast * nargs = (Z3_ast*)malloc(argCount * sizeof(Z3_ast));
        jlong * jargs = (*env)->GetLongArrayElements(env, args, NULL);
        int i = 0;
        jlong result;

        if(jargs == 0) return 0;
        for(i = 0; i < argCount; ++i) {
            nargs[i] = asZ3AST(jargs[i]);
        }
        (*env)->ReleaseLongArrayElements(env, args, jargs, 0);
        result = astToJLong(Z3_mk_set_union(asZ3Context(contextPtr), (unsigned)argCount, nargs));
        free(nargs);
        return result;
    }

    JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkSetIntersect (JNIEnv * env, jclass cls, jlong contextPtr, jint argCount, jlongArray args) {
        Z3_ast * nargs = (Z3_ast*)malloc(argCount * sizeof(Z3_ast));
        jlong * jargs = (*env)->GetLongArrayElements(env, args, NULL);
        int i = 0;
        jlong result;

        if(jargs == 0) return 0;
        for(i = 0; i < argCount; ++i) {
            nargs[i] = asZ3AST(jargs[i]);
        }
        (*env)->ReleaseLongArrayElements(env, args, jargs, 0);
        result = astToJLong(Z3_mk_set_intersect(asZ3Context(contextPtr), (unsigned)argCount, nargs));
        free(nargs);
        return result;
    }

    JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkSetDifference (JNIEnv * env, jclass cls, jlong contextPtr, jlong setPtr1, jlong setPtr2) {
        return astToJLong(Z3_mk_set_difference(asZ3Context(contextPtr), asZ3AST(setPtr1), asZ3AST(setPtr2)));
    }

    JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkSetComplement (JNIEnv * env, jclass cls, jlong contextPtr, jlong setPtr) {
        return astToJLong(Z3_mk_set_complement(asZ3Context(contextPtr), asZ3AST(setPtr)));
    }

    JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkSetMember (JNIEnv * env, jclass cls, jlong contextPtr, jlong elemPtr, jlong setPtr) {
        return astToJLong(Z3_mk_set_member(asZ3Context(contextPtr), asZ3AST(elemPtr), asZ3AST(setPtr)));
    }

    JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkSetSubset (JNIEnv * env, jclass cls, jlong contextPtr, jlong setPtr1, jlong setPtr2) {
        return astToJLong(Z3_mk_set_subset(asZ3Context(contextPtr), asZ3AST(setPtr1), asZ3AST(setPtr2)));
    }

    JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkInt (JNIEnv * env, jclass cls, jlong contextPtr, jint v, jlong sortPtr) {
        return astToJLong(Z3_mk_int(asZ3Context(contextPtr), (int)v, asZ3Sort(sortPtr)));
    }

    JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkReal (JNIEnv * env, jclass cls, jlong contextPtr, jint n, jint d) {
        return astToJLong(Z3_mk_real(asZ3Context(contextPtr), (int)n, (int)d));
    }

    JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkPattern (JNIEnv * env, jclass cls, jlong contextPtr, jint numPatterns, jlongArray args) {
        Z3_ast * nargs = (Z3_ast*)malloc(numPatterns * sizeof(Z3_ast));
        jlong * jargs = (*env)->GetLongArrayElements(env, args, NULL);
        int i = 0;
        jlong result;

        if(jargs == 0) return 0;
        for(i = 0; i < numPatterns; ++i) {
            nargs[i] = asZ3AST(jargs[i]);
        }
        (*env)->ReleaseLongArrayElements(env, args, jargs, 0);
        result = patternToJLong(Z3_mk_pattern(asZ3Context(contextPtr), (unsigned)numPatterns, nargs));
        free(nargs);
        return result;
    }

    JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkBound (JNIEnv * env, jclass cls, jlong contextPtr, jint index, jlong sortPtr) {
        return astToJLong(Z3_mk_bound(asZ3Context(contextPtr), (unsigned)index, asZ3Sort(sortPtr)));
    }

    JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkQuantifier (JNIEnv * env, jclass cls, jlong contextPtr, jboolean isForAll, jint weight, jint numPatterns, jlongArray patterns, jint numDecls, jlongArray declSorts, jlongArray declNames, jlong body) {
        Z3_pattern * npatterns = (Z3_pattern*)malloc(numPatterns * sizeof(Z3_pattern));
        Z3_sort * nsorts       = (Z3_sort*)malloc(numDecls * sizeof(Z3_sort));
        Z3_symbol * nnames     = (Z3_symbol*)malloc(numDecls * sizeof(Z3_symbol));

        jlong * jpatterns = (*env)->GetLongArrayElements(env, patterns, NULL);
        jlong * jsorts    = (*env)->GetLongArrayElements(env, declSorts, NULL);
        jlong * jnames    = (*env)->GetLongArrayElements(env, declNames, NULL);

        int i = 0;
        Z3_bool ifa;
        jlong result;

        if(jpatterns == 0 || jsorts == 0 || jnames == 0) return 0;

        for(i = 0; i < numPatterns; ++i) {
            npatterns[i] = asZ3Pattern(jpatterns[i]);
        }
        for(i = 0; i < numDecls; ++i) {
            nsorts[i] = asZ3Sort(jsorts[i]);
            nnames[i] = asZ3Symbol(jnames[i]);
        }
        (*env)->ReleaseLongArrayElements(env, patterns, jpatterns, 0);
        (*env)->ReleaseLongArrayElements(env, declSorts, jsorts, 0);
        (*env)->ReleaseLongArrayElements(env, declNames, jnames, 0);

        ifa = (isForAll == JNI_TRUE ? Z3_TRUE : Z3_FALSE);
        result = astToJLong(Z3_mk_quantifier(
                asZ3Context(contextPtr),
                ifa,
                (unsigned)weight,
                (unsigned)numPatterns,
                npatterns,
                (unsigned)numDecls,
                nsorts,
                nnames,
                asZ3AST(body)));

        free(npatterns);
        free(nsorts);
        free(nnames);
        return result;
    }


    ///////////////////////////////////////////////////////////////////


JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkBVSort (JNIEnv * env, jclass cls, jlong contextPtr, jint size) {
    return sortToJLong(Z3_mk_bv_sort(asZ3Context(contextPtr), (unsigned)size));
}

JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkBVNot (JNIEnv * env, jclass cls, jlong contextPtr, jlong astPtr) {
    return astToJLong(Z3_mk_bvnot(asZ3Context(contextPtr), asZ3AST(astPtr)));
}

JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkBVRedAnd (JNIEnv * env, jclass cls, jlong contextPtr, jlong astPtr) {
    return astToJLong(Z3_mk_bvredand(asZ3Context(contextPtr), asZ3AST(astPtr)));
}

JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkBVRedOr (JNIEnv * env, jclass cls, jlong contextPtr, jlong astPtr) {
    return astToJLong(Z3_mk_bvredor(asZ3Context(contextPtr), asZ3AST(astPtr)));
}

JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkBVAnd (JNIEnv * env, jclass cls, jlong contextPtr, jlong astPtr1, jlong astPtr2) {
    return astToJLong(Z3_mk_bvand(asZ3Context(contextPtr), asZ3AST(astPtr1), asZ3AST(astPtr2)));
}

JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkBVOr (JNIEnv * env, jclass cls, jlong contextPtr, jlong astPtr1, jlong astPtr2) {
    return astToJLong(Z3_mk_bvor(asZ3Context(contextPtr), asZ3AST(astPtr1), asZ3AST(astPtr2)));
}

JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkBVXor (JNIEnv * env, jclass cls, jlong contextPtr, jlong astPtr1, jlong astPtr2) {
    return astToJLong(Z3_mk_bvxor(asZ3Context(contextPtr), asZ3AST(astPtr1), asZ3AST(astPtr2)));
}

JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkBVNand (JNIEnv * env, jclass cls, jlong contextPtr, jlong astPtr1, jlong astPtr2) {
    return astToJLong(Z3_mk_bvnand(asZ3Context(contextPtr), asZ3AST(astPtr1), asZ3AST(astPtr2)));
}

JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkBVNor (JNIEnv * env, jclass cls, jlong contextPtr, jlong astPtr1, jlong astPtr2) {
    return astToJLong(Z3_mk_bvnor(asZ3Context(contextPtr), asZ3AST(astPtr1), asZ3AST(astPtr2)));
}

JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkBVXnor (JNIEnv * env, jclass cls, jlong contextPtr, jlong astPtr1, jlong astPtr2) {
    return astToJLong(Z3_mk_bvxnor(asZ3Context(contextPtr), asZ3AST(astPtr1), asZ3AST(astPtr2)));
}

JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkBVNeg (JNIEnv * env, jclass cls, jlong contextPtr, jlong astPtr) {
    return astToJLong(Z3_mk_bvneg(asZ3Context(contextPtr), asZ3AST(astPtr)));
}

JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkBVAdd (JNIEnv * env, jclass cls, jlong contextPtr, jlong astPtr1, jlong astPtr2) {
    return astToJLong(Z3_mk_bvadd(asZ3Context(contextPtr), asZ3AST(astPtr1), asZ3AST(astPtr2)));
}

JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkBVSub (JNIEnv * env, jclass cls, jlong contextPtr, jlong astPtr1, jlong astPtr2) {
    return astToJLong(Z3_mk_bvsub(asZ3Context(contextPtr), asZ3AST(astPtr1), asZ3AST(astPtr2)));
}

JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkBVMul (JNIEnv * env, jclass cls, jlong contextPtr, jlong astPtr1, jlong astPtr2) {
    return astToJLong(Z3_mk_bvmul(asZ3Context(contextPtr), asZ3AST(astPtr1), asZ3AST(astPtr2)));
}

JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkBVUdiv (JNIEnv * env, jclass cls, jlong contextPtr, jlong astPtr1, jlong astPtr2) {
    return astToJLong(Z3_mk_bvudiv(asZ3Context(contextPtr), asZ3AST(astPtr1), asZ3AST(astPtr2)));
}

JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkBVSdiv (JNIEnv * env, jclass cls, jlong contextPtr, jlong astPtr1, jlong astPtr2) {
    return astToJLong(Z3_mk_bvsdiv(asZ3Context(contextPtr), asZ3AST(astPtr1), asZ3AST(astPtr2)));
}

JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkBVUrem (JNIEnv * env, jclass cls, jlong contextPtr, jlong astPtr1, jlong astPtr2) {
    return astToJLong(Z3_mk_bvurem(asZ3Context(contextPtr), asZ3AST(astPtr1), asZ3AST(astPtr2)));
}

JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkBVSrem (JNIEnv * env, jclass cls, jlong contextPtr, jlong astPtr1, jlong astPtr2) {
    return astToJLong(Z3_mk_bvsrem(asZ3Context(contextPtr), asZ3AST(astPtr1), asZ3AST(astPtr2)));
}

JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkBVSmod (JNIEnv * env, jclass cls, jlong contextPtr, jlong astPtr1, jlong astPtr2) {
    return astToJLong(Z3_mk_bvsmod(asZ3Context(contextPtr), asZ3AST(astPtr1), asZ3AST(astPtr2)));
}

JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkBVUlt (JNIEnv * env, jclass cls, jlong contextPtr, jlong astPtr1, jlong astPtr2) {
    return astToJLong(Z3_mk_bvult(asZ3Context(contextPtr), asZ3AST(astPtr1), asZ3AST(astPtr2)));
}

JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkBVSlt (JNIEnv * env, jclass cls, jlong contextPtr, jlong astPtr1, jlong astPtr2) {
    return astToJLong(Z3_mk_bvslt(asZ3Context(contextPtr), asZ3AST(astPtr1), asZ3AST(astPtr2)));
}

JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkBVUle (JNIEnv * env, jclass cls, jlong contextPtr, jlong astPtr1, jlong astPtr2) {
    return astToJLong(Z3_mk_bvule(asZ3Context(contextPtr), asZ3AST(astPtr1), asZ3AST(astPtr2)));
}

JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkBVSle (JNIEnv * env, jclass cls, jlong contextPtr, jlong astPtr1, jlong astPtr2) {
    return astToJLong(Z3_mk_bvsle(asZ3Context(contextPtr), asZ3AST(astPtr1), asZ3AST(astPtr2)));
}

JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkBVUge (JNIEnv * env, jclass cls, jlong contextPtr, jlong astPtr1, jlong astPtr2) {
    return astToJLong(Z3_mk_bvuge(asZ3Context(contextPtr), asZ3AST(astPtr1), asZ3AST(astPtr2)));
}

JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkBVSge (JNIEnv * env, jclass cls, jlong contextPtr, jlong astPtr1, jlong astPtr2) {
    return astToJLong(Z3_mk_bvsge(asZ3Context(contextPtr), asZ3AST(astPtr1), asZ3AST(astPtr2)));
}

JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkBVUgt (JNIEnv * env, jclass cls, jlong contextPtr, jlong astPtr1, jlong astPtr2) {
    return astToJLong(Z3_mk_bvugt(asZ3Context(contextPtr), asZ3AST(astPtr1), asZ3AST(astPtr2)));
}

JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkBVSgt (JNIEnv * env, jclass cls, jlong contextPtr, jlong astPtr1, jlong astPtr2) {
    return astToJLong(Z3_mk_bvsgt(asZ3Context(contextPtr), asZ3AST(astPtr1), asZ3AST(astPtr2)));
}

JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkConcat (JNIEnv * env, jclass cls, jlong contextPtr, jlong astPtr1, jlong astPtr2) {
    return astToJLong(Z3_mk_concat(asZ3Context(contextPtr), asZ3AST(astPtr1), asZ3AST(astPtr2)));
}

JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkExtract (JNIEnv * env, jclass cls, jlong contextPtr, jint high, jint low, jlong astPtr) {
    return astToJLong(Z3_mk_extract(asZ3Context(contextPtr), (unsigned)high, (unsigned)low, asZ3AST(astPtr)));
}

JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkSignExt (JNIEnv * env, jclass cls, jlong contextPtr, jint i, jlong astPtr) {
    return astToJLong(Z3_mk_sign_ext(asZ3Context(contextPtr), (unsigned)i, asZ3AST(astPtr)));
}

JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkZeroExt (JNIEnv * env, jclass cls, jlong contextPtr, jint i, jlong astPtr) {
    return astToJLong(Z3_mk_zero_ext(asZ3Context(contextPtr), (unsigned)i, asZ3AST(astPtr)));
}

JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkRepeat (JNIEnv * env, jclass cls, jlong contextPtr, jint i, jlong astPtr) {
    return astToJLong(Z3_mk_repeat(asZ3Context(contextPtr), (unsigned)i, asZ3AST(astPtr)));
}

JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkBVShl (JNIEnv * env, jclass cls, jlong contextPtr, jlong astPtr1, jlong astPtr2) {
    return astToJLong(Z3_mk_bvshl(asZ3Context(contextPtr), asZ3AST(astPtr1), asZ3AST(astPtr2)));
}

JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkBVLshr (JNIEnv * env, jclass cls, jlong contextPtr, jlong astPtr1, jlong astPtr2) {
    return astToJLong(Z3_mk_bvlshr(asZ3Context(contextPtr), asZ3AST(astPtr1), asZ3AST(astPtr2)));
}

JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkBVAshr (JNIEnv * env, jclass cls, jlong contextPtr, jlong astPtr1, jlong astPtr2) {
    return astToJLong(Z3_mk_bvashr(asZ3Context(contextPtr), asZ3AST(astPtr1), asZ3AST(astPtr2)));
}

JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkRotateLeft (JNIEnv * env, jclass cls, jlong contextPtr, jint i, jlong astPtr) {
    return astToJLong(Z3_mk_rotate_left(asZ3Context(contextPtr), (unsigned)i, asZ3AST(astPtr)));
}

JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkRotateRight (JNIEnv * env, jclass cls, jlong contextPtr, jint i, jlong astPtr) {
    return astToJLong(Z3_mk_rotate_right(asZ3Context(contextPtr), (unsigned)i, asZ3AST(astPtr)));
}

JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkExtRotateLeft (JNIEnv * env, jclass cls, jlong contextPtr, jlong astPtr1, jlong astPtr2) {
    return astToJLong(Z3_mk_ext_rotate_left(asZ3Context(contextPtr), asZ3AST(astPtr1), asZ3AST(astPtr2)));
}

JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkExtRotateRight (JNIEnv * env, jclass cls, jlong contextPtr, jlong astPtr1, jlong astPtr2) {
    return astToJLong(Z3_mk_ext_rotate_right(asZ3Context(contextPtr), asZ3AST(astPtr1), asZ3AST(astPtr2)));
}

JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkInt2BV (JNIEnv * env, jclass cls, jlong contextPtr, jint size, jlong astPtr) {
    return astToJLong(Z3_mk_int2bv(asZ3Context(contextPtr), (unsigned)size, asZ3AST(astPtr)));
}

JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkBV2Int (JNIEnv * env, jclass cls, jlong contextPtr, jlong astPtr, jboolean isSigned) {
    return astToJLong(Z3_mk_bv2int(asZ3Context(contextPtr), asZ3AST(astPtr), (isSigned == JNI_TRUE ? Z3_TRUE : Z3_FALSE)));
}

JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkBVAddNoOverflow (JNIEnv * env, jclass cls, jlong contextPtr, jlong astPtr1, jlong astPtr2, jboolean isSigned) {
    return astToJLong(Z3_mk_bvadd_no_overflow(asZ3Context(contextPtr), asZ3AST(astPtr1), asZ3AST(astPtr2), (isSigned == JNI_TRUE ? Z3_TRUE : Z3_FALSE)));
}

JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkBVAddNoUnderflow (JNIEnv * env, jclass cls, jlong contextPtr, jlong astPtr1, jlong astPtr2) {
    return astToJLong(Z3_mk_bvadd_no_underflow(asZ3Context(contextPtr), asZ3AST(astPtr1), asZ3AST(astPtr2)));
}

JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkBVSubNoUnderflow (JNIEnv * env, jclass cls, jlong contextPtr, jlong astPtr1, jlong astPtr2, jboolean isSigned) {
    return astToJLong(Z3_mk_bvsub_no_underflow(asZ3Context(contextPtr), asZ3AST(astPtr1), asZ3AST(astPtr2), (isSigned == JNI_TRUE ? Z3_TRUE : Z3_FALSE)));
}

JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkBVSubNoOverflow (JNIEnv * env, jclass cls, jlong contextPtr, jlong astPtr1, jlong astPtr2) {
    return astToJLong(Z3_mk_bvsub_no_overflow(asZ3Context(contextPtr), asZ3AST(astPtr1), asZ3AST(astPtr2)));
}

JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkBVSdivNoOverflow (JNIEnv * env, jclass cls, jlong contextPtr, jlong astPtr1, jlong astPtr2) {
    return astToJLong(Z3_mk_bvsdiv_no_overflow(asZ3Context(contextPtr), asZ3AST(astPtr1), asZ3AST(astPtr2)));
}

JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkBVNegNoOverflow (JNIEnv * env, jclass cls, jlong contextPtr, jlong astPtr) {
    return astToJLong(Z3_mk_bvneg_no_overflow(asZ3Context(contextPtr), asZ3AST(astPtr)));
}

JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkBVMulNoOverflow (JNIEnv * env, jclass cls, jlong contextPtr, jlong astPtr1, jlong astPtr2, jboolean isSigned) {
    return astToJLong(Z3_mk_bvmul_no_overflow(asZ3Context(contextPtr), asZ3AST(astPtr1), asZ3AST(astPtr2), (isSigned == JNI_TRUE ? Z3_TRUE : Z3_FALSE)));
}

JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkBVMulNoUnderflow (JNIEnv * env, jclass cls, jlong contextPtr, jlong astPtr1, jlong astPtr2) {
    return astToJLong(Z3_mk_bvmul_no_underflow(asZ3Context(contextPtr), asZ3AST(astPtr1), asZ3AST(astPtr2)));
}













    ///////////////////////////////////////////////////////////////////

    JNIEXPORT jint JNICALL Java_z3_Z3Wrapper_getSymbolKind (JNIEnv * env, jclass cls, jlong contextPtr, jlong symbolPtr) {
        Z3_symbol_kind k = Z3_get_symbol_kind(asZ3Context(contextPtr), asZ3Symbol(symbolPtr));
        switch (k) {
            case Z3_INT_SYMBOL: return (jint)0;
            case Z3_STRING_SYMBOL: return (jint)1;
        }
        return (jint)-1;
    }

    JNIEXPORT jint JNICALL Java_z3_Z3Wrapper_getSymbolInt (JNIEnv * env, jclass cls, jlong contextPtr, jlong symbolPtr) {
        return (jint)Z3_get_symbol_int(asZ3Context(contextPtr), asZ3Symbol(symbolPtr));
    }

    JNIEXPORT jstring JNICALL Java_z3_Z3Wrapper_getSymbolString (JNIEnv * env, jclass cls, jlong contextPtr, jlong symbolPtr) {
        const char * str = (const char *)Z3_get_symbol_string(asZ3Context(contextPtr), asZ3Symbol(symbolPtr));
        return (*env)->NewStringUTF(env, str);
    }

    JNIEXPORT jint JNICALL Java_z3_Z3Wrapper_getASTKind(JNIEnv * env, jclass cls, jlong contextPtr, jlong astPtr) {
        Z3_ast_kind k = Z3_get_ast_kind(asZ3Context(contextPtr), asZ3AST(astPtr));
        switch (k) {
            case Z3_NUMERAL_AST:    return (jint)0;
            case Z3_APP_AST:        return (jint)1;
            case Z3_VAR_AST:        return (jint)2;
            case Z3_QUANTIFIER_AST: return (jint)3;
            case Z3_UNKNOWN_AST:    return (jint)4;
        }
        return (jint)-1;
    }

    JNIEXPORT jint JNICALL Java_z3_Z3Wrapper_getDeclKind(JNIEnv * env, jclass cls, jlong contextPtr, jlong funcDeclPtr) {
        Z3_decl_kind k = Z3_get_decl_kind(asZ3Context(contextPtr), asZ3FuncDecl(funcDeclPtr));

        switch (k) {
            case Z3_OP_TRUE:           return (jint)0; 
            case Z3_OP_FALSE:          return (jint)1; 
            case Z3_OP_EQ:             return (jint)2;
            case Z3_OP_DISTINCT:       return (jint)3;
            case Z3_OP_ITE:            return (jint)4;
            case Z3_OP_AND:            return (jint)5;
            case Z3_OP_OR:             return (jint)6; 
            case Z3_OP_IFF:            return (jint)7;
            case Z3_OP_XOR:            return (jint)8;
            case Z3_OP_NOT:            return (jint)9;
            case Z3_OP_IMPLIES:        return (jint)10;
            case Z3_OP_ANUM:           return (jint)11;
            case Z3_OP_LE:             return (jint)12;
            case Z3_OP_GE:             return (jint)13;
            case Z3_OP_LT:             return (jint)14;
            case Z3_OP_GT:             return (jint)15;
            case Z3_OP_ADD:            return (jint)16;
            case Z3_OP_SUB:            return (jint)17;
            case Z3_OP_UMINUS:         return (jint)18;
            case Z3_OP_MUL:            return (jint)19;
            case Z3_OP_DIV:            return (jint)20;
            case Z3_OP_IDIV:           return (jint)21;
            case Z3_OP_REM:            return (jint)22; 
            case Z3_OP_MOD:            return (jint)23;
            case Z3_OP_TO_REAL:        return (jint)24; 
            case Z3_OP_TO_INT:         return (jint)25;
            case Z3_OP_IS_INT:         return (jint)26;
            case Z3_OP_STORE:          return (jint)27;
            case Z3_OP_SELECT:         return (jint)28;
            case Z3_OP_CONST_ARRAY:    return (jint)29;
            case Z3_OP_ARRAY_DEFAULT:  return (jint)30;
            case Z3_OP_ARRAY_MAP:      return (jint)31;
            case Z3_OP_SET_UNION:      return (jint)32;
            case Z3_OP_SET_INTERSECT:  return (jint)33;
            case Z3_OP_SET_DIFFERENCE: return (jint)34;
            case Z3_OP_SET_COMPLEMENT: return (jint)35;
            case Z3_OP_SET_SUBSET:     return (jint)36;
            case Z3_OP_AS_ARRAY:       return (jint)37;
            case Z3_OP_UNINTERPRETED:  return (jint)1000;
        }
        return (jint)9999;
    }

    JNIEXPORT jint JNICALL Java_z3_Z3Wrapper_getAppNumArgs (JNIEnv * env, jclass cls, jlong contextPtr, jlong astPtr) {
        Z3_app app = Z3_to_app(asZ3Context(contextPtr), asZ3AST(astPtr));
        return (jint)Z3_get_app_num_args(asZ3Context(contextPtr), app);
    }

    JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_getAppArg (JNIEnv * env, jclass cls, jlong contextPtr, jlong astPtr, jint i) {
        Z3_app app = Z3_to_app(asZ3Context(contextPtr), asZ3AST(astPtr));
        return astToJLong(Z3_get_app_arg(asZ3Context(contextPtr), app, (unsigned)i));
    }

    JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_getAppDecl (JNIEnv * env, jclass cls, jlong contextPtr, jlong astPtr) {
        Z3_app app = Z3_to_app(asZ3Context(contextPtr), asZ3AST(astPtr));
        return funcDeclToJLong(Z3_get_app_decl(asZ3Context(contextPtr), app));
    }

    JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_getDeclName (JNIEnv * env, jclass cls, jlong contextPtr, jlong funcDeclPtr) {
        return symbolToJLong(Z3_get_decl_name(asZ3Context(contextPtr), asZ3FuncDecl(funcDeclPtr)));
    }

    JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_getDeclFuncDeclParameter (JNIEnv * env, jclass cls, jlong contextPtr, jlong funcDeclPtr, jint i) {
        return funcDeclToJLong(Z3_get_decl_func_decl_parameter(asZ3Context(contextPtr), asZ3FuncDecl(funcDeclPtr), (unsigned)i));
    }

    JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_getSort (JNIEnv * env, jclass cls, jlong contextPtr, jlong astPtr) {
        return sortToJLong(Z3_get_sort(asZ3Context(contextPtr), asZ3AST(astPtr)));
    }

    JNIEXPORT jint JNICALL Java_z3_Z3Wrapper_getDomainSize (JNIEnv * env, jclass cls, jlong contextPtr, jlong funcDeclPtr) {
        return (jint)Z3_get_domain_size(asZ3Context(contextPtr), asZ3FuncDecl(funcDeclPtr));
    }

    JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_getDomain (JNIEnv * env, jclass cls, jlong contextPtr, jlong funcDeclPtr, jint i) {
        return sortToJLong(Z3_get_domain(asZ3Context(contextPtr), asZ3FuncDecl(funcDeclPtr), (unsigned)i));
    }

    JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_getRange (JNIEnv * env, jclass cls, jlong contextPtr, jlong funcDeclPtr) {
        return sortToJLong(Z3_get_range(asZ3Context(contextPtr), asZ3FuncDecl(funcDeclPtr)));
    }

    JNIEXPORT jboolean JNICALL Java_z3_Z3Wrapper_getNumeralInt (JNIEnv * env, jclass cls, jlong contextPtr, jlong astPtr, jobject intPtr) {
        int val;
        Z3_bool result = Z3_get_numeral_int(asZ3Context(contextPtr), asZ3AST(astPtr), &val);
        jclass ipc = (*env)->GetObjectClass(env, intPtr);
        jfieldID fid = (*env)->GetFieldID(env, ipc, "value", "I");
        (*env)->SetIntField(env, intPtr, fid, (jint)val);
        return (result == 0 ? JNI_FALSE : JNI_TRUE);
    }

    JNIEXPORT jint JNICALL Java_z3_Z3Wrapper_getBoolValue (JNIEnv * env, jclass cls, jlong contextPtr, jlong astPtr) {
        return (jint)Z3_get_bool_value(asZ3Context(contextPtr), asZ3AST(astPtr));
    }

    JNIEXPORT void JNICALL Java_z3_Z3Wrapper_push (JNIEnv * env, jclass cls, jlong contextPtr) {
        Z3_push(asZ3Context(contextPtr));
    }

    JNIEXPORT void JNICALL Java_z3_Z3Wrapper_pop (JNIEnv * env, jclass cls, jlong contextPtr, jint numScopes) {
        Z3_pop(asZ3Context(contextPtr), (int)numScopes);
    }

    JNIEXPORT jint JNICALL Java_z3_Z3Wrapper_getNumScopes (JNIEnv * env, jclass cls, jlong contextPtr) {
        return (jint)Z3_get_num_scopes(asZ3Context(contextPtr));
    }

    JNIEXPORT void JNICALL Java_z3_Z3Wrapper_assertCnstr (JNIEnv * env, jclass cls, jlong contextPtr, jlong astPtr) {
        Z3_assert_cnstr(asZ3Context(contextPtr), asZ3AST(astPtr));
    }

    JNIEXPORT jint JNICALL Java_z3_Z3Wrapper_check (JNIEnv * env, jclass cls, jlong contextPtr) {
        return (jint)Z3_check(asZ3Context(contextPtr));
    }

    JNIEXPORT jint JNICALL Java_z3_Z3Wrapper_checkAndGetModel (JNIEnv * env, jclass cls, jlong contextPtr, jobject model) {
        Z3_model newModel;
        Z3_lbool result = Z3_check_and_get_model(asZ3Context(contextPtr), &newModel);
        jclass mc = (*env)->GetObjectClass(env, model);
        jfieldID fid = (*env)->GetFieldID(env, mc, "ptr", "J");
        (*env)->SetLongField(env, model, fid, modelToJLong(newModel));
        return (jint)result;
    }

    JNIEXPORT jint JNICALL Java_z3_Z3Wrapper_checkAssumptions (JNIEnv * env, jclass cls, jlong contextPtr, jint numAssumptions, jlongArray assumptions, jobject model, jint coreSizeIn, jobject coreSizeOut, jlongArray core) {
        Z3_model newModel;
        Z3_ast * c_assumptions = (Z3_ast*)malloc((unsigned)numAssumptions * sizeof(Z3_ast));
        jlong * j_assumptions = (*env)->GetLongArrayElements(env, assumptions, NULL);

        jclass model_oc = (*env)->GetObjectClass(env, model);
        jfieldID model_fid = (*env)->GetFieldID(env, model_oc, "ptr", "J");

        jclass core_oc = (*env)->GetObjectClass(env, coreSizeOut);
        jfieldID core_fid = (*env)->GetFieldID(env, core_oc, "value", "I");

        Z3_lbool result;
        unsigned c_coreSize = (unsigned)coreSizeIn;
        Z3_ast * c_core = (Z3_ast*)malloc((unsigned)numAssumptions * sizeof(Z3_ast));
        jlong * j_core = (jlong*)malloc((unsigned)numAssumptions * sizeof(jlong));

        unsigned i = 0;

	if(j_assumptions == 0) return 0;
        for (i = 0; i < (unsigned)numAssumptions; ++i) {
            c_assumptions[i] = asZ3AST(j_assumptions[i]);
        }

        (*env)->ReleaseLongArrayElements(env, assumptions, j_assumptions, 0);

        result = Z3_check_assumptions(asZ3Context(contextPtr), (unsigned)numAssumptions, c_assumptions, &newModel, NULL, &c_coreSize, c_core);

        for (i = 0; i < c_coreSize; ++i) {
            j_core[i] = astToJLong(c_core[i]);
        }

        (*env)->SetLongField(env, model, model_fid, modelToJLong(newModel));
        (*env)->SetLongArrayRegion(env, core, 0, (jsize)c_coreSize, j_core);
        (*env)->SetIntField(env, coreSizeOut, core_fid, (jint)c_coreSize);

        free(c_assumptions);
        free(c_core);
        free(j_core);

        return (jint)result;
    }

    JNIEXPORT jint JNICALL Java_z3_Z3Wrapper_getSearchFailure (JNIEnv * env, jclass cls, jlong contextPtr) {
        Z3_search_failure f = Z3_get_search_failure(asZ3Context(contextPtr));
        switch (f) {
            case Z3_NO_FAILURE:       return (jint)0;
            case Z3_UNKNOWN:          return (jint)1;
            case Z3_TIMEOUT:          return (jint)2;
            case Z3_MEMOUT_WATERMARK: return (jint)3;
            case Z3_CANCELED:         return (jint)4;
            case Z3_NUM_CONFLICTS:    return (jint)5;
            case Z3_THEORY:           return (jint)6;
            case Z3_QUANTIFIERS:      return (jint)7;
        }
        return (jint)-1;
    }

    JNIEXPORT void JNICALL Java_z3_Z3Wrapper_delModel (JNIEnv * env, jclass cls, jlong contextPtr, jlong modelPtr) {
        Z3_del_model(asZ3Context(contextPtr), asZ3Model(modelPtr));
    }

    JNIEXPORT jboolean JNICALL Java_z3_Z3Wrapper_eval (JNIEnv * env, jclass cls, jlong contextPtr, jlong modelPtr, jlong astPtr, jobject ast) {
        Z3_ast newAST;
        Z3_bool result = Z3_eval(asZ3Context(contextPtr), asZ3Model(modelPtr), asZ3AST(astPtr), &newAST);
        jclass ac = (*env)->GetObjectClass(env, ast);
        jfieldID fid = (*env)->GetFieldID(env, ac, "ptr", "J");
        (*env)->SetLongField(env, ast, fid, astToJLong(newAST));
        return (result == 0 ? JNI_FALSE : JNI_TRUE);
    }

    JNIEXPORT jint JNICALL Java_z3_Z3Wrapper_getModelNumConstants (JNIEnv * env, jclass cls, jlong contextPtr, jlong modelPtr) {
        return (jint)Z3_get_model_num_constants(asZ3Context(contextPtr), asZ3Model(modelPtr));
    }

    JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_getModelConstant (JNIEnv * env, jclass cls, jlong contextPtr, jlong modelPtr, jint i) {
        return funcDeclToJLong(Z3_get_model_constant(asZ3Context(contextPtr), asZ3Model(modelPtr), (unsigned)i));
    }

    JNIEXPORT jboolean JNICALL Java_z3_Z3Wrapper_isArrayValue (JNIEnv * env, jclass cls, jlong contextPtr, jlong modelPtr, jlong astPtr, jobject numEntries) {
        unsigned int cNumEntries;
        Z3_bool result = Z3_is_array_value(asZ3Context(contextPtr), asZ3Model(modelPtr), asZ3AST(astPtr), &cNumEntries);
        jclass ac = (*env)->GetObjectClass(env, numEntries);
        jfieldID fid = (*env)->GetFieldID(env, ac, "value", "I");
        (*env)->SetIntField(env, numEntries, fid, (jint)cNumEntries);
        return (result == 0 ? JNI_FALSE : JNI_TRUE);
    }

    JNIEXPORT void JNICALL Java_z3_Z3Wrapper_getArrayValue (JNIEnv * env, jclass cls, jlong contextPtr, jlong modelPtr, jlong astPtr, jint numEntries, jlongArray indices, jlongArray values, jobject elseValue) {
        Z3_ast * cIndices = (Z3_ast*)malloc(numEntries * sizeof(Z3_ast));
        Z3_ast * cValues  = (Z3_ast*)malloc(numEntries * sizeof(Z3_ast));
        Z3_ast cElseValue;
        jclass ac;
        jfieldID fid;

        //jlong * jIndices = (*env)->GetLongArrayElements(env, indices, NULL);
        //jlong * jValues  = (*env)->GetLongArrayElements(env, values, NULL);
        jlong * jIndices = (jlong*)malloc(numEntries * sizeof(jlong));
        jlong * jValues  = (jlong*)malloc(numEntries * sizeof(jlong));
        int i = 0;

        // if(jIndices == 0 || jValues == 0) {
        //     fprintf(stderr, "Could not access java arrays in getArrayValue.\n");
        //     // TODO take care of elseValue?
        //     return;
        // }

        // for(i = 0; i < numEntries; ++i) {
        //     cIndices[i] = (Z3_ast)jIndices[i];
        //     cValues[i]  = (Z3_ast)jValues[i];
        // }

        // The Z3 call...
        Z3_get_array_value(asZ3Context(contextPtr), asZ3Model(modelPtr), asZ3AST(astPtr), (unsigned)numEntries, cIndices, cValues, &cElseValue);

        // set the else value
        ac = (*env)->GetObjectClass(env, elseValue);
        fid = (*env)->GetFieldID(env, ac, "ptr", "J");
        (*env)->SetLongField(env, elseValue, fid, astToJLong(cElseValue));

        // build indices and values arrays
        for(i = 0; i < numEntries; ++i) {
            jIndices[i] = astToJLong(cIndices[i]);
            jValues[i] = astToJLong(cValues[i]);
            //    printf("%d : ind: %d val: %d\n", i, jIndices[i], jValues[i]);
        }
        (*env)->SetLongArrayRegion(env, indices, 0, (jsize)numEntries, jIndices);
        (*env)->SetLongArrayRegion(env, values, 0, (jsize)numEntries, jValues);
        free(cIndices);
        free(cValues);
        free(jIndices);
        free(jValues);

        //(*env)->ReleaseLongArrayElements(env, indices, jIndices, JNI_ABORT);
        //(*env)->ReleaseLongArrayElements(env, values, jValues, JNI_ABORT);
    }

    JNIEXPORT jint JNICALL Java_z3_Z3Wrapper_getModelNumFuncs (JNIEnv * env, jclass cls, jlong contextPtr, jlong modelPtr) {
        return (jint)Z3_get_model_num_funcs(asZ3Context(contextPtr), asZ3Model(modelPtr));
    }

    JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_getModelFuncDecl (JNIEnv * env, jclass cls, jlong contextPtr, jlong modelPtr, jint i) {
        return funcDeclToJLong(Z3_get_model_func_decl(asZ3Context(contextPtr), asZ3Model(modelPtr), (unsigned)i));
    }

    JNIEXPORT jint JNICALL Java_z3_Z3Wrapper_getModelFuncNumEntries (JNIEnv * env, jclass cls, jlong contextPtr, jlong modelPtr, jint i) {
        return (jint)Z3_get_model_func_num_entries(asZ3Context(contextPtr), asZ3Model(modelPtr), (unsigned)i);
    }

    JNIEXPORT jint JNICALL Java_z3_Z3Wrapper_getModelFuncEntryNumArgs (JNIEnv * env, jclass cls, jlong contextPtr, jlong modelPtr, jint i, jint j) {
        return (jint)Z3_get_model_func_entry_num_args(asZ3Context(contextPtr), asZ3Model(modelPtr), (unsigned)i, (unsigned)j);
    }

    JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_getModelFuncEntryArg (JNIEnv * env, jclass cls, jlong contextPtr, jlong modelPtr, jint i, jint j, jint k) {
        return astToJLong(Z3_get_model_func_entry_arg(asZ3Context(contextPtr), asZ3Model(modelPtr), (unsigned)i, (unsigned)j, (unsigned)k));
    }

    JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_getModelFuncEntryValue (JNIEnv * env, jclass cls, jlong contextPtr, jlong modelPtr, jint i, jint k) {
        return astToJLong(Z3_get_model_func_entry_value(asZ3Context(contextPtr), asZ3Model(modelPtr), (unsigned)i, (unsigned)k));
    }

    JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_getModelFuncElse (JNIEnv * env, jclass cls, jlong contextPtr, jlong modelPtr, jint i) {
        return astToJLong(Z3_get_model_func_else(asZ3Context(contextPtr), asZ3Model(modelPtr), (unsigned)i));
    }

    JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkLabel (JNIEnv * env, jclass cls, jlong contextPtr, jlong symbolPtr, jboolean polarity, jlong astPtr) {
        Z3_bool ifa = (polarity == JNI_TRUE ? Z3_TRUE : Z3_FALSE);
        return astToJLong(Z3_mk_label(asZ3Context(contextPtr), asZ3Symbol(symbolPtr), ifa, asZ3AST(astPtr)));
    }

    JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_getRelevantLabels (JNIEnv * env, jclass cls, jlong contextPtr) {
        return literalsToJLong(Z3_get_relevant_labels(asZ3Context(contextPtr)));
    }

    JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_getRelevantLiterals (JNIEnv * env, jclass cls, jlong contextPtr) {
        return literalsToJLong(Z3_get_relevant_literals(asZ3Context(contextPtr)));
    }

    JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_getGuessedLiterals (JNIEnv * env, jclass cls, jlong contextPtr) {
        return literalsToJLong(Z3_get_guessed_literals(asZ3Context(contextPtr)));
    }

    JNIEXPORT void JNICALL Java_z3_Z3Wrapper_delLiterals (JNIEnv * env, jclass cls, jlong contextPtr, jlong lbls) {
        Z3_del_literals(asZ3Context(contextPtr), asZ3Literals(lbls));
    }

    JNIEXPORT jint JNICALL Java_z3_Z3Wrapper_getNumLiterals (JNIEnv * env, jclass cls, jlong contextPtr, jlong lbls) {
        return (jint)Z3_get_num_literals(asZ3Context(contextPtr), asZ3Literals(lbls));
    }

    JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_getLabelSymbol (JNIEnv * env, jclass cls, jlong contextPtr, jlong lbls, jint idx) {
        return symbolToJLong(Z3_get_label_symbol(asZ3Context(contextPtr), asZ3Literals(lbls), (unsigned)idx));
    }

    JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_getLiteral (JNIEnv * env, jclass cls, jlong contextPtr, jlong lbls, jint idx) {
        return astToJLong(Z3_get_literal(asZ3Context(contextPtr), asZ3Literals(lbls), (unsigned)idx));
    }

    JNIEXPORT void JNICALL Java_z3_Z3Wrapper_disableLiteral (JNIEnv * env, jclass cls, jlong contextPtr, jlong lbls, jint idx) {
        Z3_disable_literal(asZ3Context(contextPtr), asZ3Literals(lbls), (unsigned)idx);
    }

    JNIEXPORT void JNICALL Java_z3_Z3Wrapper_blockLiterals (JNIEnv * env, jclass cls, jlong contextPtr, jlong lbls) {
        Z3_block_literals(asZ3Context(contextPtr), asZ3Literals(lbls));
    }

    JNIEXPORT void JNICALL Java_z3_Z3Wrapper_printAST (JNIEnv * env, jclass cls, jlong contextPtr, jlong astPtr) {
        printf("%s\n", Z3_ast_to_string(asZ3Context(contextPtr), asZ3AST(astPtr)));
    }

    JNIEXPORT void JNICALL Java_z3_Z3Wrapper_printModel (JNIEnv * env, jclass cls, jlong contextPtr, jlong modelPtr) {
        printf("%s\n", Z3_model_to_string(asZ3Context(contextPtr), asZ3Model(modelPtr)));
    }

    JNIEXPORT void JNICALL Java_z3_Z3Wrapper_printContext (JNIEnv * env, jclass cls, jlong contextPtr) {
        printf("%s\n", Z3_context_to_string(asZ3Context(contextPtr)));
    }

    JNIEXPORT jstring JNICALL Java_z3_Z3Wrapper_astToString (JNIEnv * env, jclass cls, jlong contextPtr, jlong astPtr) {
        const char * str = (const char *)Z3_ast_to_string(asZ3Context(contextPtr), asZ3AST(astPtr));
        return (*env)->NewStringUTF(env, str);
    }

    JNIEXPORT jstring JNICALL Java_z3_Z3Wrapper_funcDeclToString (JNIEnv * env, jclass cls, jlong contextPtr, jlong funcDeclPtr) {
        const char * str = (const char *)Z3_func_decl_to_string(asZ3Context(contextPtr), asZ3FuncDecl(funcDeclPtr));
        return (*env)->NewStringUTF(env, str);
    }

    JNIEXPORT jstring JNICALL Java_z3_Z3Wrapper_sortToString (JNIEnv * env, jclass cls, jlong contextPtr, jlong sortPtr) {
        const char * str = (const char *)Z3_sort_to_string(asZ3Context(contextPtr), asZ3Sort(sortPtr));
        return (*env)->NewStringUTF(env, str);
    }

    JNIEXPORT jstring JNICALL Java_z3_Z3Wrapper_patternToString (JNIEnv * env, jclass cls, jlong contextPtr, jlong patternPtr) {
        const char * str = (const char *)Z3_pattern_to_string(asZ3Context(contextPtr), asZ3Pattern(patternPtr));
        return (*env)->NewStringUTF(env, str);
    }

    JNIEXPORT jstring JNICALL Java_z3_Z3Wrapper_modelToString (JNIEnv * env, jclass cls, jlong contextPtr, jlong modelPtr) {
        const char * str = (const char *)Z3_model_to_string(asZ3Context(contextPtr), asZ3Model(modelPtr));
        return (*env)->NewStringUTF(env, str);
    }

    JNIEXPORT jstring JNICALL Java_z3_Z3Wrapper_contextToString (JNIEnv * env, jclass cls, jlong contextPtr) {
        const char * str = (const char *)Z3_context_to_string(asZ3Context(contextPtr));
        return (*env)->NewStringUTF(env, str);
    }

    JNIEXPORT jstring JNICALL Java_z3_Z3Wrapper_benchmarkToSMTLIBString (JNIEnv * env, jclass cls, jlong contextPtr, jstring nameStr, jstring logicStr, jstring statusStr, jstring attrStr, jint numAss, jlongArray ass, jlong formulaPtr) {
        Z3_context cont = asZ3Context(contextPtr);
        const jbyte *nameStrBytes, *logicStrBytes, *statusStrBytes, *attrStrBytes;
        const char * result;
        Z3_ast * cAss = (Z3_ast*)malloc(numAss * sizeof(Z3_ast*));
        jlong  * jAss = (*env)->GetLongArrayElements(env, ass, NULL);
        int i = 0;

        if(jAss == NULL)
            return NULL;

        nameStrBytes   = (*env)->GetStringUTFChars(env, nameStr, NULL);
        logicStrBytes  = (*env)->GetStringUTFChars(env, logicStr, NULL);
        statusStrBytes = (*env)->GetStringUTFChars(env, statusStr, NULL);
        attrStrBytes   = (*env)->GetStringUTFChars(env, attrStr, NULL);
        if (nameStrBytes == NULL || logicStrBytes == NULL || statusStrBytes == NULL || attrStrBytes == NULL)
            return NULL;

        for(i = 0; i < numAss; ++i) {
           cAss[i] = asZ3AST(jAss[i]);
        }
        (*env)->ReleaseLongArrayElements(env, ass, jAss, 0); 

        result = Z3_benchmark_to_smtlib_string(
                cont, 
                (const char*)nameStrBytes,
                (const char*)logicStrBytes,
                (const char*)statusStrBytes,
                (const char*)attrStrBytes,
                (int)numAss,
                cAss,
                asZ3AST(formulaPtr)
                );

        free(cAss);
        return (*env)->NewStringUTF(env, result);
    }

    JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkTheory (JNIEnv * env, jclass cls, jlong contextPtr, jstring name) {
        const jbyte * nameStr;
        Z3_theory toReturn;

        nameStr = (*env)->GetStringUTFChars(env, name, NULL);
        if (nameStr == NULL) return JLONG_MY_NULL;
        toReturn = Z3_mk_theory(asZ3Context(contextPtr), (const char*)nameStr, NULL);
        (*env)->ReleaseStringUTFChars(env, name, nameStr);
        //printf("Making theory. Pointer will be %d.\n", toReturn);
        return theoryToJLong(toReturn);
    }

    JNIEXPORT void JNICALL Java_z3_Z3Wrapper_setTheoryCallbacks (JNIEnv * env, jclass cls, jlong thyPtr, jobject theoryProxy, jboolean setDelete, jboolean setReduceEq, jboolean setReduceApp, jboolean setReduceDistinct, jboolean setNewApp, jboolean setNewElem, jboolean setInitSearch, jboolean setPush, jboolean setPop, jboolean setRestart, jboolean setReset, jboolean setFinalCheck, jboolean setNewEq, jboolean setNewDiseq, jboolean setNewAssignment, jboolean setNewRelevant) {
        Z3_theory thy = asZ3Theory(thyPtr);
        if(setDelete == JNI_TRUE)
            Z3_set_delete_callback(thy, &delete_default_callback);
        if(setReduceApp == JNI_TRUE)
            Z3_set_reduce_app_callback(thy, &reduce_app_default_callback);
        if(setReduceEq == JNI_TRUE)
            Z3_set_reduce_eq_callback(thy, &reduce_eq_default_callback);
        if(setReduceDistinct == JNI_TRUE)
            Z3_set_reduce_distinct_callback(thy, &reduce_distinct_default_callback);
        if(setNewApp == JNI_TRUE)
            Z3_set_new_app_callback(thy, &new_app_default_callback);
        if(setNewElem == JNI_TRUE)
            Z3_set_new_elem_callback(thy, &new_elem_default_callback);
        if(setInitSearch == JNI_TRUE)
            Z3_set_init_search_callback(thy, &init_search_default_callback);
        if(setPush == JNI_TRUE)
            Z3_set_push_callback(thy, &push_default_callback);
        if(setPop == JNI_TRUE)
            Z3_set_pop_callback(thy, &pop_default_callback);
        if(setRestart == JNI_TRUE)
            Z3_set_restart_callback(thy, &restart_default_callback);
        if(setReset == JNI_TRUE)
            Z3_set_reset_callback(thy, &reset_default_callback);
        if(setFinalCheck == JNI_TRUE)
            Z3_set_final_check_callback(thy, &final_check_default_callback_fptr);
        if(setNewEq == JNI_TRUE)
            Z3_set_new_eq_callback(thy, &new_eq_default_callback);
        if(setNewDiseq == JNI_TRUE)
            Z3_set_new_diseq_callback(thy, &new_diseq_default_callback);
        if(setNewAssignment == JNI_TRUE)
            Z3_set_new_assignment_callback(thy, &new_assignment_default_callback);
        if(setNewRelevant == JNI_TRUE)
            Z3_set_new_relevant_callback(thy, &new_relevant_default_callback);

        set_callback_jni_env(env, thyPtr, theoryProxy);
    }

    JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_theoryMkSort (JNIEnv * env, jclass cls, jlong contextPtr, jlong thyPtr, jlong symPtr) {
        return sortToJLong(Z3_theory_mk_sort(asZ3Context(contextPtr), asZ3Theory(thyPtr), asZ3Symbol(symPtr)));
    }

    JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_theoryMkValue (JNIEnv * env, jclass cls, jlong contextPtr, jlong thyPtr, jlong symPtr, jlong sortPtr) {
        return astToJLong(Z3_theory_mk_value(asZ3Context(contextPtr), asZ3Theory(thyPtr), asZ3Symbol(symPtr), asZ3Sort(sortPtr)));
    }

    JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_theoryMkConstant (JNIEnv * env, jclass cls, jlong contextPtr, jlong thyPtr, jlong symPtr, jlong sortPtr) {
        return astToJLong(Z3_theory_mk_constant(asZ3Context(contextPtr), asZ3Theory(thyPtr), asZ3Symbol(symPtr), asZ3Sort(sortPtr)));
    }

    JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_theoryMkFuncDecl (JNIEnv * env, jclass cls, jlong contextPtr, jlong thyPtr, jlong symPtr, jint domainSize, jlongArray domainSortPtrs, jlong rangeSortPtr) {
        Z3_sort * nargs = (Z3_sort*)malloc(domainSize * sizeof(Z3_sort));
        jlong   * jargs = (*env)->GetLongArrayElements(env, domainSortPtrs, NULL);
        int i = 0;
        jlong result;

        if(jargs == 0) return 0;

        for(i = 0; i < domainSize; ++i) {
            nargs[i] = asZ3Sort(jargs[i]);
        }
        (*env)->ReleaseLongArrayElements(env, domainSortPtrs, jargs, 0);
        result = funcDeclToJLong(Z3_theory_mk_func_decl(asZ3Context(contextPtr), asZ3Theory(thyPtr), asZ3Symbol(symPtr), (unsigned)domainSize, nargs, asZ3Sort(rangeSortPtr)));
        free(nargs);
        return result;
    }

    JNIEXPORT void JNICALL Java_z3_Z3Wrapper_theoryAssertAxiom (JNIEnv * env, jclass cls, jlong thyPtr, jlong astPtr) {
        Z3_theory_assert_axiom(asZ3Theory(thyPtr), asZ3AST(astPtr));
    }

    JNIEXPORT void JNICALL Java_z3_Z3Wrapper_theoryAssumeEq (JNIEnv * env, jclass cls, jlong thyPtr, jlong ast1Ptr, jlong ast2Ptr) {
        Z3_theory_assume_eq(asZ3Theory(thyPtr), asZ3AST(ast1Ptr), asZ3AST(ast2Ptr));
    }

    JNIEXPORT void JNICALL Java_z3_Z3Wrapper_theoryEnableAxiomSimplification (JNIEnv * env, jclass cls, jlong thyPtr, jboolean flag) {
        Z3_theory_enable_axiom_simplification(asZ3Theory(thyPtr), (flag == JNI_TRUE ? Z3_TRUE : Z3_FALSE));
    }

    JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_theoryGetEqCRoot (JNIEnv * env, jclass cls, jlong thyPtr, jlong astPtr) {
        return astToJLong(Z3_theory_get_eqc_root(asZ3Theory(thyPtr), asZ3AST(astPtr)));
    }

    JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_theoryGetEqCNext (JNIEnv * env, jclass cls, jlong thyPtr, jlong astPtr) {
        return astToJLong(Z3_theory_get_eqc_next(asZ3Theory(thyPtr), asZ3AST(astPtr)));
    }

    JNIEXPORT jint JNICALL Java_z3_Z3Wrapper_theoryGetNumParents (JNIEnv * env, jclass cls, jlong thyPtr, jlong astPtr) {
        return (jint)Z3_theory_get_num_parents(asZ3Theory(thyPtr), asZ3AST(astPtr));
    }

    JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_theoryGetParent (JNIEnv * env, jclass cls, jlong thyPtr, jlong astPtr, jint i) {
        return astToJLong(Z3_theory_get_parent(asZ3Theory(thyPtr), asZ3AST(astPtr), (unsigned)i));
    }

    JNIEXPORT jboolean JNICALL Java_z3_Z3Wrapper_theoryIsValue (JNIEnv * env, jclass cls, jlong thyPtr, jlong astPtr) {
        Z3_bool result = Z3_theory_is_value(asZ3Theory(thyPtr), asZ3AST(astPtr));
        return (result == Z3_TRUE ? JNI_TRUE : JNI_FALSE);
    }

    JNIEXPORT jboolean JNICALL Java_z3_Z3Wrapper_theoryIsDecl (JNIEnv * env, jclass cls, jlong thyPtr, jlong declPtr) {
        Z3_bool result = Z3_theory_is_decl(asZ3Theory(thyPtr), asZ3FuncDecl(declPtr));
        return (result == Z3_TRUE ? JNI_TRUE : JNI_FALSE);
    }

    JNIEXPORT jint JNICALL Java_z3_Z3Wrapper_theoryGetNumElems (JNIEnv * env, jclass cls, jlong thyPtr) {
        return (jint)Z3_theory_get_num_elems(asZ3Theory(thyPtr));
    }

    JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_theoryGetElem (JNIEnv * env, jclass cls, jlong thyPtr, jint i) {
        return astToJLong(Z3_theory_get_elem(asZ3Theory(thyPtr), (unsigned)i)); 
    }

    JNIEXPORT jint JNICALL Java_z3_Z3Wrapper_theoryGetNumApps (JNIEnv * env, jclass cls, jlong thyPtr) {
        return (jint)Z3_theory_get_num_apps(asZ3Theory(thyPtr));
    }

    JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_theoryGetApp (JNIEnv * env, jclass cls, jlong thyPtr, jint i) {
        return astToJLong(Z3_theory_get_app(asZ3Theory(thyPtr), (unsigned)i)); 
    }

    JNIEXPORT void JNICALL Java_z3_Z3Wrapper_parseSMTLIBString (JNIEnv * env, jclass cls, jlong contextPtr, jstring str, jint numSorts, jlongArray sortNames, jlongArray sorts, jint numDecls, jlongArray declNames, jlongArray decls) {
        jlong result = parseSMTLIBStringCommon(env, cls, JNI_FALSE, contextPtr, str, numSorts, sortNames, sorts, numDecls, declNames, decls);
        return;
    }

    JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_parseSMTLIB2String (JNIEnv * env, jclass cls, jlong contextPtr, jstring str, jint numSorts, jlongArray sortNames, jlongArray sorts, jint numDecls, jlongArray declNames, jlongArray decls) {
        jlong result = parseSMTLIBStringCommon(env, cls, JNI_TRUE, contextPtr, str, numSorts, sortNames, sorts, numDecls, declNames, decls);
        return result;
    }

#if 0
    /* This functions is not accessible from Java, but used from two other functions here. */
    jlong parseSMTLIBStringCommon (JNIEnv * env, jclass cls, jboolean isSMTLIB2, jlong contextPtr, jstring str, jint numSorts, jlongArray sortNames, jlongArray sorts, jint numDecls, jlongArray declNames, jlongArray decls) {
        Z3_sort * nsorts       = (numSorts > 0 ? (Z3_sort*)malloc(numSorts * sizeof(Z3_sort)) : NULL);
        Z3_symbol * nsortsN    = (numSorts > 0 ? (Z3_symbol*)malloc(numSorts * sizeof(Z3_symbol)) : NULL);
        Z3_func_decl * ndecls  = (numDecls > 0 ? (Z3_func_decl*)malloc(numDecls * sizeof(Z3_func_decl)) : NULL);
        Z3_symbol * ndeclsN    = (numDecls > 0 ? (Z3_symbol*)malloc(numDecls * sizeof(Z3_symbol)) : NULL);
        jlong * jsorts;
        jlong * jsortsN;
        jlong * jdecls;
        jlong * jdeclsN;
        int i = 0;
        const jbyte * z3str;
        jlong resultingAST = 0;

        if(numSorts > 0) {
            jsorts = (*env)->GetLongArrayElements(env, sorts, NULL); if(jsorts == 0) return;
            jsortsN = (*env)->GetLongArrayElements(env, sortNames, NULL); if(jsortsN == 0) return;
        }
        if(numDecls > 0) {
            jdecls = (*env)->GetLongArrayElements(env, decls, NULL); if(jdecls == 0) return;
            jdeclsN = (*env)->GetLongArrayElements(env, declNames, NULL); if(jdeclsN == 0) return;
        }
        for(i = 0; i < numSorts; ++i) {
            nsorts[i] = asZ3Sort(jsorts[i]);
            nsortsN[i] = asZ3Symbol(jsortsN[i]);
        }
        for(i = 0; i < numDecls; ++i) {
            ndecls[i] = asZ3FuncDecl(jdecls[i]);
            ndeclsN[i] = asZ3Symbol(jdeclsN[i]);
        }

        z3str = (*env)->GetStringUTFChars(env, str, NULL); if (z3str == NULL) return;

        if(isSMTLIB2 == JNI_TRUE) {
          resultingAST = Z3_parse_smtlib2_string(
                  asZ3Context(contextPtr),
                  (const char*)z3str,
                  (unsigned)numSorts,
                  nsortsN,
                  nsorts,
                  (unsigned)numDecls,
                  ndeclsN,
                  ndecls);
        } else {
          Z3_parse_smtlib_string(
                  asZ3Context(contextPtr),
                  (const char*)z3str,
                  (unsigned)numSorts,
                  nsortsN,
                  nsorts,
                  (unsigned)numDecls,
                  ndeclsN,
                  ndecls);
        }

        if(numSorts > 0) {
            (*env)->ReleaseLongArrayElements(env, sorts, jsorts, 0);
            (*env)->ReleaseLongArrayElements(env, sortNames, jsortsN, 0);
            free(nsorts);
            free(nsortsN);
        }
        if(numDecls > 0) { 
            (*env)->ReleaseLongArrayElements(env, decls, jdecls, 0);
            (*env)->ReleaseLongArrayElements(env, declNames, jdeclsN, 0);
            free(ndecls);
            free(ndeclsN);
        }
        return resultingAST;
    }
#endif

    JNIEXPORT void JNICALL Java_z3_Z3Wrapper_parseSMTLIBFile (JNIEnv * env, jclass cls, jlong contextPtr, jstring str, jint numSorts, jlongArray sortNames, jlongArray sorts, jint numDecls, jlongArray declNames, jlongArray decls) {
        jlong result = parseSMTLIBFileCommon(env, cls, JNI_FALSE, contextPtr, str, numSorts, sortNames, sorts, numDecls, declNames, decls);
        return;
    }

    JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_parseSMTLIB2File (JNIEnv * env, jclass cls, jlong contextPtr, jstring str, jint numSorts, jlongArray sortNames, jlongArray sorts, jint numDecls, jlongArray declNames, jlongArray decls) {
        jlong result = parseSMTLIBFileCommon(env, cls, JNI_TRUE, contextPtr, str, numSorts, sortNames, sorts, numDecls, declNames, decls);
        return result;
    }

#if 0
    /* This functions is not accessible from Java, but used from two other functions here. */
    jlong parseSMTLIBFileCommon (JNIEnv * env, jclass cls, jboolean isSMTLIB2, jlong contextPtr, jstring str, jint numSorts, jlongArray sortNames, jlongArray sorts, jint numDecls, jlongArray declNames, jlongArray decls) {
        Z3_sort * nsorts       = (numSorts > 0 ? (Z3_sort*)malloc(numSorts * sizeof(Z3_sort)) : NULL);
        Z3_symbol * nsortsN    = (numSorts > 0 ? (Z3_symbol*)malloc(numSorts * sizeof(Z3_symbol)) : NULL);
        Z3_func_decl * ndecls  = (numDecls > 0 ? (Z3_func_decl*)malloc(numDecls * sizeof(Z3_func_decl)) : NULL);
        Z3_symbol * ndeclsN    = (numDecls > 0 ? (Z3_symbol*)malloc(numDecls * sizeof(Z3_symbol)) : NULL);
        jlong * jsorts;
        jlong * jsortsN;
        jlong * jdecls;
        jlong * jdeclsN;
        int i = 0;
        const jbyte * z3str;

        if(numSorts > 0) {
            jsorts = (*env)->GetLongArrayElements(env, sorts, NULL); if(jsorts == 0) return;
            jsortsN = (*env)->GetLongArrayElements(env, sortNames, NULL); if(jsortsN == 0) return;
        }
        if(numDecls > 0) {
            jdecls = (*env)->GetLongArrayElements(env, decls, NULL); if(jdecls == 0) return;
            jdeclsN = (*env)->GetLongArrayElements(env, declNames, NULL); if(jdeclsN == 0) return;
        }
        for(i = 0; i < numSorts; ++i) {
            nsorts[i] = asZ3Sort(jsorts[i]);
            nsortsN[i] = asZ3Symbol(jsortsN[i]);
        }
        for(i = 0; i < numDecls; ++i) {
            ndecls[i] = asZ3FuncDecl(jdecls[i]);
            ndeclsN[i] = asZ3Symbol(jdeclsN[i]);
        }

        z3str = (*env)->GetStringUTFChars(env, str, NULL); if (z3str == NULL) return;

        if(isSMTLIB2 == JNI_TRUE) {
          Z3_parse_smtlib2_file(
                  asZ3Context(contextPtr),
                  (const char*)z3str,
                  (unsigned)numSorts,
                  nsortsN,
                  nsorts,
                  (unsigned)numDecls,
                  ndeclsN,
                  ndecls);
        } else {
          Z3_parse_smtlib_file(
                  asZ3Context(contextPtr),
                  (const char*)z3str,
                  (unsigned)numSorts,
                  nsortsN,
                  nsorts,
                  (unsigned)numDecls,
                  ndeclsN,
                  ndecls);
        }

        if(numSorts > 0) {
            (*env)->ReleaseLongArrayElements(env, sorts, jsorts, 0);
            (*env)->ReleaseLongArrayElements(env, sortNames, jsortsN, 0);
            free(nsorts);
            free(nsortsN);
        }
        if(numDecls > 0) { 
            (*env)->ReleaseLongArrayElements(env, decls, jdecls, 0);
            (*env)->ReleaseLongArrayElements(env, declNames, jdeclsN, 0);
            free(ndecls);
            free(ndeclsN);
        }
    }
#endif

    JNIEXPORT jint JNICALL Java_z3_Z3Wrapper_getSMTLIBNumFormulas (JNIEnv * env, jclass cls, jlong contextPtr) {
        return (jint)Z3_get_smtlib_num_formulas(asZ3Context(contextPtr));
    }

    JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_getSMTLIBFormula (JNIEnv * env, jclass cls, jlong contextPtr, jint i) {
        return astToJLong(Z3_get_smtlib_formula(asZ3Context(contextPtr), (unsigned)i));
    }

    JNIEXPORT jint JNICALL Java_z3_Z3Wrapper_getSMTLIBNumAssumptions (JNIEnv * env, jclass cls, jlong contextPtr) {
        return (jint)Z3_get_smtlib_num_assumptions(asZ3Context(contextPtr));
    }

    JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_getSMTLIBAssumption (JNIEnv * env, jclass cls, jlong contextPtr, jint i) {
        return astToJLong(Z3_get_smtlib_assumption(asZ3Context(contextPtr), (unsigned)i));
    }

    JNIEXPORT jint JNICALL Java_z3_Z3Wrapper_getSMTLIBNumDecls (JNIEnv * env, jclass cls, jlong contextPtr) {
        return (jint)Z3_get_smtlib_num_decls(asZ3Context(contextPtr));
    }

    JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_getSMTLIBDecl (JNIEnv * env, jclass cls, jlong contextPtr, jint i) {
        return funcDeclToJLong(Z3_get_smtlib_decl(asZ3Context(contextPtr), (unsigned)i));
    }

    JNIEXPORT jint JNICALL Java_z3_Z3Wrapper_getSMTLIBNumSorts (JNIEnv * env, jclass cls, jlong contextPtr) {
        return (jint)Z3_get_smtlib_num_sorts(asZ3Context(contextPtr));
    }

    JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_getSMTLIBSort (JNIEnv * env, jclass cls, jlong contextPtr, jint i) {
        return sortToJLong(Z3_get_smtlib_sort(asZ3Context(contextPtr), (unsigned)i));
    }

    JNIEXPORT jstring JNICALL Java_z3_Z3Wrapper_getSMTLIBError (JNIEnv * env, jclass cls, jlong contextPtr) {
        const char * str = (const char *)Z3_get_smtlib_error(asZ3Context(contextPtr));
        return (*env)->NewStringUTF(env, str);
    }

    JNIEXPORT void JNICALL Java_z3_Z3Wrapper_getVersion (JNIEnv * env, jclass cls, jobject major, jobject minor, jobject buildNumber, jobject revisionNumber) {
        unsigned int cmaj, cmin, bn, rv;
        jclass ipc;
        jfieldID fid;

        Z3_get_version(&cmaj, &cmin, &bn, &rv);

        ipc = (*env)->GetObjectClass(env, major);
        fid = (*env)->GetFieldID(env, ipc, "value", "I");
        (*env)->SetIntField(env, major, fid, (jint)cmaj);
        ipc = (*env)->GetObjectClass(env, minor);
        fid = (*env)->GetFieldID(env, ipc, "value", "I");
        (*env)->SetIntField(env, minor, fid, (jint)cmin);
        ipc = (*env)->GetObjectClass(env, buildNumber);
        fid = (*env)->GetFieldID(env, ipc, "value", "I");
        (*env)->SetIntField(env, buildNumber, fid, (jint)bn);
        ipc = (*env)->GetObjectClass(env, revisionNumber);
        fid = (*env)->GetFieldID(env, ipc, "value", "I");
        (*env)->SetIntField(env, revisionNumber, fid, (jint)rv);
    }

    JNIEXPORT void JNICALL Java_z3_Z3Wrapper_resetMemory (JNIEnv * env, jclass cls) {
        Z3_reset_memory();
    }

    JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_substitute
      (JNIEnv * env, jclass cls, jlong contextPtr, jlong astPtr, jint numExprs, jlongArray fromPtr, jlongArray toPtr) {
          int i = 0;
          jlong * fromst = (*env)->GetLongArrayElements(env, fromPtr, NULL);
          jlong * tost = (*env)->GetLongArrayElements(env, toPtr, NULL);
          Z3_context ctx = asZ3Context(contextPtr);
          Z3_ast ast = asZ3AST(astPtr);
          Z3_ast * from = (Z3_ast*)malloc(numExprs * sizeof(Z3_ast));
          Z3_ast * to = (Z3_ast*)malloc(numExprs * sizeof(Z3_ast));
          for(i = 0; i < numExprs; ++i) {
            from[i] = asZ3AST(fromst[i]);
            to[i] = asZ3AST(tost[i]);

          }

          jlong res = astToJLong(Z3_substitute(ctx, ast, (unsigned)numExprs, from, to));

          (*env)->ReleaseLongArrayElements(env, fromPtr, fromst, JNI_ABORT);
          (*env)->ReleaseLongArrayElements(env, toPtr, tost, JNI_ABORT);
          free(from);
          free(to);
          return res;
    }

    JNIEXPORT void JNICALL Java_z3_Z3Wrapper_setAstPrintMode(JNIEnv * env, jclass cls, jlong contextPtr, jint mode)
    {
        Z3_context ctx = asZ3Context(contextPtr);
        Z3_ast_print_mode modeCast = (Z3_ast_print_mode)mode;
        Z3_set_ast_print_mode(ctx, modeCast);
    }

    JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_simplify
      (JNIEnv * env, jclass cls, jlong contextPtr, jlong astPtr)
    {
        Z3_context ctx = asZ3Context(contextPtr);
        Z3_ast ast = asZ3AST(astPtr);
        jlong res = astToJLong(Z3_simplify(ctx, ast));
        return res;
    }


    JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_getQuantifierBody
      (JNIEnv * env, jclass cls, jlong contextPtr, jlong astPtr){
        Z3_context ctx = asZ3Context(contextPtr);
        Z3_ast ast = asZ3AST(astPtr);
        jlong res = astToJLong(Z3_get_quantifier_body(ctx, ast));
        return res;
      }

    JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_getQuantifierBoundName
      (JNIEnv * env, jclass cls, jlong contextPtr, jlong astPtr, jint n){
        Z3_context ctx = asZ3Context(contextPtr);
        Z3_ast ast = asZ3AST(astPtr);
        jlong res = symbolToJLong(Z3_get_quantifier_bound_name(ctx, ast, (unsigned)n));
        return res;
      }

    JNIEXPORT jint JNICALL Java_z3_Z3Wrapper_getQuantifierNumBound
      (JNIEnv * env, jclass cls, jlong contextPtr, jlong astPtr){
          Z3_context ctx = asZ3Context(contextPtr);
          Z3_ast ast = asZ3AST(astPtr);
          unsigned res = Z3_get_quantifier_num_bound(ctx, ast);
          return (jint)res;
        }

    JNIEXPORT jboolean JNICALL Java_z3_Z3Wrapper_isQuantifierForall
      (JNIEnv * env, jclass cls, jlong contextPtr, jlong astPtr){
        Z3_context ctx = asZ3Context(contextPtr);
        Z3_ast ast = asZ3AST(astPtr);
        Z3_bool res = Z3_is_quantifier_forall(ctx, ast);
        return (jboolean)res;
      }

    JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkQuantifierConst
      (JNIEnv * env, jclass cls, jlong contextPtr, jboolean isForAll, jint weight, jint numBounds, jlongArray bounds, jint numPatterns, jlongArray patterns, jlong bodyPtr) {
            Z3_pattern * npatterns = (Z3_pattern*)malloc(numPatterns * sizeof(Z3_pattern));
            Z3_app * nbounds       = (Z3_app*)malloc(numBounds * sizeof(Z3_app));

            jlong * jpatterns = (*env)->GetLongArrayElements(env, patterns, NULL);
            jlong * jbounds    = (*env)->GetLongArrayElements(env, bounds, NULL);

            Z3_context ctx = asZ3Context(contextPtr);
            Z3_ast body = asZ3AST(bodyPtr);

            int i = 0;
            Z3_bool ifa;
            jlong result;

            if(jpatterns == 0 || jbounds == 0) return 0;

            for(i = 0; i < numPatterns; ++i) {
                npatterns[i] = asZ3Pattern(jpatterns[i]);
            }
            for(i = 0; i < numBounds; ++i) {
                nbounds[i] = Z3_to_app(ctx, asZ3AST(jbounds[i]));
            }
            (*env)->ReleaseLongArrayElements(env, patterns, jpatterns, 0);
            (*env)->ReleaseLongArrayElements(env, bounds, jbounds, 0);

            ifa = (isForAll == JNI_TRUE ? Z3_TRUE : Z3_FALSE);
            result = astToJLong(Z3_mk_quantifier_const(
                    ctx,
                    ifa,
                    (unsigned)weight,
                    (unsigned)numBounds,
                    nbounds,
                    (unsigned)numPatterns,
                    npatterns,
                    body));

            free(npatterns);
            free(nbounds);
            return result;
        }

    JNIEXPORT jint JNICALL Java_z3_Z3Wrapper_getIndexValue
      (JNIEnv * env, jclass cls, jlong contextPtr, jlong astPtr) {
      Z3_context ctx = asZ3Context(contextPtr);
      Z3_ast ast = asZ3AST(astPtr);

      return (int)Z3_get_index_value(ctx, ast);
      }

    JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkTactic
      (JNIEnv * env, jclass cls, jlong contextPtr, jstring name){
        Z3_context ctx = asZ3Context(contextPtr);
        const jbyte * str;
        str = (*env)->GetStringUTFChars(env, name, NULL);
        if (str == NULL) return JLONG_MY_NULL;
        Z3_tactic tactic = Z3_mk_tactic(ctx, (const char *)str);
        Z3_tactic_inc_ref(ctx, tactic);
        return tacticToJLong(tactic);
      }

    JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_tacticAndThen
      (JNIEnv * env, jclass cls, jlong contextPtr, jlong tactic1Ptr, jlong tactic2Ptr)
      {
        Z3_context ctx = asZ3Context(contextPtr);
        Z3_tactic tactic = Z3_tactic_and_then(ctx, asZ3Tactic(tactic1Ptr), asZ3Tactic(tactic2Ptr));
        Z3_tactic_inc_ref(ctx, tactic);
        return tacticToJLong(tactic);
      }

    JNIEXPORT void JNICALL Java_z3_Z3Wrapper_tacticDelete
      (JNIEnv * env, jclass cls, jlong contextPtr, jlong tacticPtr)
      {
        Z3_tactic_dec_ref(asZ3Context(contextPtr), asZ3Tactic(tacticPtr));
      }

    JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_mkSolverFromTactic
      (JNIEnv * env, jclass cls, jlong contextPtr, jlong tacticPtr)
      {
        Z3_context ctx = asZ3Context(contextPtr);
        Z3_solver solver = Z3_mk_solver_from_tactic(ctx, asZ3Tactic(tacticPtr));
        Z3_solver_inc_ref(ctx, solver);
        return solverToJLong(solver);
      }

    JNIEXPORT void JNICALL Java_z3_Z3Wrapper_solverPush
      (JNIEnv * env, jclass cls, jlong contextPtr, jlong solverPtr)
      {
        Z3_context ctx = asZ3Context(contextPtr);
        Z3_solver_push(ctx, asZ3Solver(solverPtr));
      }

    JNIEXPORT void JNICALL Java_z3_Z3Wrapper_solverPop
      (JNIEnv * env, jclass cls, jlong contextPtr, jlong solverPtr, jint num)
      {
        Z3_context ctx = asZ3Context(contextPtr);
        Z3_solver_pop(ctx, asZ3Solver(solverPtr), (unsigned)num);
      }

    JNIEXPORT void JNICALL Java_z3_Z3Wrapper_solverAssertCnstr
      (JNIEnv * env, jclass cls, jlong contextPtr, jlong solverPtr, jlong astPtr)
      {
        Z3_context ctx = asZ3Context(contextPtr);
        Z3_solver_assert(ctx, asZ3Solver(solverPtr), asZ3AST(astPtr));
      }

    JNIEXPORT jint JNICALL Java_z3_Z3Wrapper_solverCheck
      (JNIEnv * env, jclass cls, jlong contextPtr, jlong solverPtr)
      {
        Z3_context ctx = asZ3Context(contextPtr);
        return (int)Z3_solver_check(ctx, asZ3Solver(solverPtr));
      }

    JNIEXPORT jlong JNICALL Java_z3_Z3Wrapper_solverGetModel
      (JNIEnv * env, jclass cls, jlong contextPtr, jlong solverPtr)
      {
        Z3_context ctx = asZ3Context(contextPtr);
        return modelToJLong(Z3_solver_get_model(ctx, asZ3Solver(solverPtr)));
      }

    JNIEXPORT void JNICALL Java_z3_Z3Wrapper_solverReset
      (JNIEnv * env, jclass cls, jlong contextPtr, jlong solverPtr)
      {
        Z3_solver_reset(asZ3Context(contextPtr), asZ3Solver(solverPtr));
      }

    JNIEXPORT void JNICALL Java_z3_Z3Wrapper_solverDelete
      (JNIEnv * env, jclass cls, jlong contextPtr, jlong solverPtr)
      {
        Z3_solver_dec_ref(asZ3Context(contextPtr), asZ3Solver(solverPtr));
      }

#ifdef __cplusplus
}
#endif
