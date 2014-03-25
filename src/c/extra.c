#include "extra.h"
#include "casts.h"
#include <stdlib.h>

#ifdef __cplusplus
extern "C" {
#endif

    /* This functions is not accessible from Java, but used from two other functions here. */
    jlong parseSMTLIBStringCommon (JNIEnv * env, jclass cls, jboolean isSMTLIB2, jlong contextPtr, jstring str, jint numSorts, jlongArray sortNames, jlongArray sorts, jint numDecls, jlongArray declNames, jlongArray decls) {
        Z3_sort * nsorts       = (numSorts > 0 ? (Z3_sort*)malloc(numSorts * sizeof(Z3_sort)) : NULL);
        Z3_symbol * nsortsN    = (numSorts > 0 ? (Z3_symbol*)malloc(numSorts * sizeof(Z3_symbol)) : NULL);
        Z3_func_decl * ndecls  = (numDecls > 0 ? (Z3_func_decl*)malloc(numDecls * sizeof(Z3_func_decl)) : NULL);
        Z3_symbol * ndeclsN    = (numDecls > 0 ? (Z3_symbol*)malloc(numDecls * sizeof(Z3_symbol)) : NULL);
        jlong * jsorts = NULL;
        jlong * jsortsN = NULL;
        jlong * jdecls = NULL;
        jlong * jdeclsN = NULL;
        int i = 0;
        const char * z3str;
        jlong resultingAST = 0;

        if(numSorts > 0) {
            jsorts = (*env)->GetLongArrayElements(env, sorts, NULL); if(jsorts == 0) return 0;
            jsortsN = (*env)->GetLongArrayElements(env, sortNames, NULL); if(jsortsN == 0) return 0;

            for(i = 0; i < numSorts; ++i) {
                nsorts[i] = asZ3Sort(jsorts[i]);
                nsortsN[i] = asZ3Symbol(jsortsN[i]);
            }
        }
        if(numDecls > 0) {
            jdecls = (*env)->GetLongArrayElements(env, decls, NULL); if(jdecls == 0) return 0;
            jdeclsN = (*env)->GetLongArrayElements(env, declNames, NULL); if(jdeclsN == 0) return 0;

            for(i = 0; i < numDecls; ++i) {
                ndecls[i] = asZ3FuncDecl(jdecls[i]);
                ndeclsN[i] = asZ3Symbol(jdeclsN[i]);
            }
        }

        z3str = (*env)->GetStringUTFChars(env, str, NULL); if (z3str == NULL) return 0;

        if(isSMTLIB2 == JNI_TRUE) {
          resultingAST = astToJLong(Z3_parse_smtlib2_string(
                  asZ3Context(contextPtr),
                  z3str,
                  (unsigned)numSorts,
                  nsortsN,
                  nsorts,
                  (unsigned)numDecls,
                  ndeclsN,
                  ndecls));
        } else {
          Z3_parse_smtlib_string(
                  asZ3Context(contextPtr),
                  z3str,
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
    
    /* This functions is not accessible from Java, but used from two other functions here. */
    jlong parseSMTLIBFileCommon (JNIEnv * env, jclass cls, jboolean isSMTLIB2, jlong contextPtr, jstring str, jint numSorts, jlongArray sortNames, jlongArray sorts, jint numDecls, jlongArray declNames, jlongArray decls) {
        Z3_sort * nsorts       = (numSorts > 0 ? (Z3_sort*)malloc(numSorts * sizeof(Z3_sort)) : NULL);
        Z3_symbol * nsortsN    = (numSorts > 0 ? (Z3_symbol*)malloc(numSorts * sizeof(Z3_symbol)) : NULL);
        Z3_func_decl * ndecls  = (numDecls > 0 ? (Z3_func_decl*)malloc(numDecls * sizeof(Z3_func_decl)) : NULL);
        Z3_symbol * ndeclsN    = (numDecls > 0 ? (Z3_symbol*)malloc(numDecls * sizeof(Z3_symbol)) : NULL);
        jlong * jsorts = NULL;
        jlong * jsortsN = NULL;
        jlong * jdecls = NULL;
        jlong * jdeclsN = NULL;
        int i = 0;
        const char * z3str;

        if(numSorts > 0) {
            jsorts = (*env)->GetLongArrayElements(env, sorts, NULL); if(jsorts == 0) return 0;
            jsortsN = (*env)->GetLongArrayElements(env, sortNames, NULL); if(jsortsN == 0) return 0;
        }
        if(numDecls > 0) {
            jdecls = (*env)->GetLongArrayElements(env, decls, NULL); if(jdecls == 0) return 0;
            jdeclsN = (*env)->GetLongArrayElements(env, declNames, NULL); if(jdeclsN == 0) return 0;
        }
        for(i = 0; i < numSorts; ++i) {
            nsorts[i] = asZ3Sort(jsorts[i]);
            nsortsN[i] = asZ3Symbol(jsortsN[i]);
        }
        for(i = 0; i < numDecls; ++i) {
            ndecls[i] = asZ3FuncDecl(jdecls[i]);
            ndeclsN[i] = asZ3Symbol(jdeclsN[i]);
        }

        z3str = (*env)->GetStringUTFChars(env, str, NULL); if (z3str == NULL) return 0;

        if(isSMTLIB2 == JNI_TRUE) {
          Z3_parse_smtlib2_file(
                  asZ3Context(contextPtr),
                  z3str,
                  (unsigned)numSorts,
                  nsortsN,
                  nsorts,
                  (unsigned)numDecls,
                  ndeclsN,
                  ndecls);
        } else {
          Z3_parse_smtlib_file(
                  asZ3Context(contextPtr),
                  z3str,
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
        return 0;
    }

#ifdef __cplusplus
}
#endif

