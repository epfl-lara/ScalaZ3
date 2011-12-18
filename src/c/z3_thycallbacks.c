#include <jni.h>
#include <z3.h>
#include <stdio.h>
#include <stdlib.h>
#include "z3_thycallbacks.h"
#include "casts.h"

#ifdef __cplusplus
extern "C" {
#endif

    JNIEnv * jniEnv = (JNIEnv *)NULL;
    unsigned registeredProxies = 0;
    jlong theoryPointers[MAXSUPPORTEDZ3THEORIES];
    jclass theoryProxyClasses[MAXSUPPORTEDZ3THEORIES];
    jobject theoryProxies[MAXSUPPORTEDZ3THEORIES];
    jlong lastTheoryPointer = JLONG_MY_NULL;
    unsigned lastTheoryID = 0;

    void set_callback_jni_env(JNIEnv * env, jlong thyPtr, jobject theoryProxy) { 
        unsigned thyID = registeredProxies;
        jclass pc = (*env)->GetObjectClass(env, theoryProxy);
        jobject gr = (*env)->NewGlobalRef(env, theoryProxy);

        theoryPointers[thyID] = thyPtr;
        theoryProxyClasses[thyID] = (*env)->NewGlobalRef(env, pc);
        jniEnv = env;
        theoryProxies[thyID] = gr;
        lastTheoryPointer = thyPtr;
        lastTheoryID = thyID;
        registeredProxies++;
    }

    unsigned get_theory_id(jlong thyPtr) {
        if(lastTheoryPointer == thyPtr) {
            return lastTheoryID;
        } else {
            unsigned i = 0;
            for(; i < registeredProxies; ++i) {
                if(theoryPointers[i] == thyPtr) {
                    jlong ptr = theoryPointers[i];
                    lastTheoryPointer = ptr;
                    lastTheoryID = i;
                    return i;
                }
            }
            fprintf(stderr, "Fatal error: no theory proxy register for theory.\n");
            exit(-1);
            return -1;
        }
    }

    jobject mk_fresh_pointer(JNIEnv * env) {
        jclass pointerClass = (*jniEnv)->FindClass(jniEnv, "z3/Pointer");
        jmethodID pointerCons = (*jniEnv)->GetMethodID(jniEnv, pointerClass, "<init>", "(J)V");
        jobject pointer = (*jniEnv)->NewLocalRef(jniEnv,
                (*jniEnv)->NewObject(jniEnv, pointerClass, pointerCons, JLONG_MY_NULL));
        return pointer;
    }

    jlong read_pointer_value(JNIEnv * env, jobject pointer) {
        jclass pointerClass = (*jniEnv)->FindClass(jniEnv, "z3/Pointer");
        jfieldID fid = (*jniEnv)->GetFieldID(jniEnv, pointerClass, "ptr", "J");
        return (*jniEnv)->GetLongField(jniEnv, pointer, fid);
    }

    void delete_default_callback(__in Z3_theory t) {
        jlong thyPtr = theoryToJLong(t);
        unsigned tid = get_theory_id(thyPtr);
        jclass proxyCls = theoryProxyClasses[tid];
        jobject proxy   = theoryProxies[tid];
        jclass proxyCls2 = (*jniEnv)->GetObjectClass(jniEnv, proxy);
        jmethodID mid   = (*jniEnv)->GetMethodID(jniEnv, proxyCls, "delete", "(J)V");
        (*jniEnv)->CallVoidMethod(jniEnv, proxy, mid, thyPtr);
    }

    Z3_bool reduce_app_default_callback(__in Z3_theory t, __in Z3_func_decl fd, __in unsigned argc, __in Z3_ast const args[], __out Z3_ast * r) {
        jlong thyPtr = theoryToJLong(t);
        unsigned tid = get_theory_id(thyPtr);
        jclass proxyCls = theoryProxyClasses[tid];
        jobject proxy   = theoryProxies[tid];
        jclass proxyCls2 = (*jniEnv)->GetObjectClass(jniEnv, proxy);
        jmethodID mid   = (*jniEnv)->GetMethodID(jniEnv, proxyCls, "reduceApp", "(JJI[JLz3/Pointer;)Z");
        // we create the array
        jlongArray newArgs = (*jniEnv)->NewLongArray(jniEnv, (jsize)argc);
        jlong * newArgsE = (jlong*)malloc(argc * sizeof(jlong));
        jobject pointer;
        jboolean result;

        unsigned i = 0;
        for(i = 0; i < argc; ++i) {
            newArgsE[i] = astToJLong(args[i]);
        }
        (*jniEnv)->SetLongArrayRegion(jniEnv, newArgs, 0, (jsize)argc, newArgsE);
        pointer = mk_fresh_pointer(jniEnv);
        result = (*jniEnv)->CallBooleanMethod(jniEnv, proxy, mid, thyPtr, funcDeclToJLong(fd), (jint)argc, newArgs, pointer);

        if(result == JNI_TRUE) {
            *r = asZ3AST(read_pointer_value(jniEnv, pointer));
            return Z3_TRUE;
        } else {
            return Z3_FALSE;
        }
    }

    Z3_bool reduce_eq_default_callback(__in Z3_theory t, __in Z3_ast a, __in Z3_ast b, __out Z3_ast * r) {
        jlong thyPtr = theoryToJLong(t);
        unsigned tid = get_theory_id(thyPtr);
        jclass proxyCls = theoryProxyClasses[tid];
        jobject proxy   = theoryProxies[tid];
        jclass proxyCls2 = (*jniEnv)->GetObjectClass(jniEnv, proxy);
        jmethodID mid   = (*jniEnv)->GetMethodID(jniEnv, proxyCls, "reduceEq", "(JJJLz3/Pointer;)Z");
        jobject pointer = mk_fresh_pointer(jniEnv);
        jboolean result = (*jniEnv)->CallBooleanMethod(jniEnv, proxy, mid, thyPtr, astToJLong(a), astToJLong(b), pointer);

        if(result == JNI_TRUE) {
            *r = asZ3AST(read_pointer_value(jniEnv, pointer));
            return Z3_TRUE;
        } else {
            return Z3_FALSE;
        }
    }

    Z3_bool reduce_distinct_default_callback(__in Z3_theory t, __in unsigned argc, __in Z3_ast const args[], __out Z3_ast * r) {
        jlong thyPtr = theoryToJLong(t);
        unsigned tid = get_theory_id(thyPtr);
        jclass proxyCls = theoryProxyClasses[tid];
        jobject proxy   = theoryProxies[tid];
        jclass proxyCls2 = (*jniEnv)->GetObjectClass(jniEnv, proxy);
        jmethodID mid   = (*jniEnv)->GetMethodID(jniEnv, proxyCls, "reduceDistinct", "(J[JLz3/Pointer;)Z");
        // we create the array
        jlongArray newArgs = (*jniEnv)->NewLongArray(jniEnv, (jsize)argc);
        jlong * newArgsE = (jlong*)malloc(argc * sizeof(jlong));
        jobject pointer;
        jboolean result;
        unsigned i = 0;

        for(i = 0; i < argc; ++i) {
            newArgsE[i] = astToJLong(args[i]);
        }
        (*jniEnv)->SetLongArrayRegion(jniEnv, newArgs, 0, (jsize)argc, newArgsE);
        pointer = mk_fresh_pointer(jniEnv);
        result = (*jniEnv)->CallBooleanMethod(jniEnv, proxy, mid, thyPtr, (jint)argc, newArgs, pointer);

        if(result == JNI_TRUE) {
            *r = asZ3AST(read_pointer_value(jniEnv, pointer));
            return Z3_TRUE;
        } else {
            return Z3_FALSE;
        }
    }

    void new_app_default_callback(__in Z3_theory t, __in Z3_ast a) {
        jlong thyPtr = theoryToJLong(t);
        unsigned tid = get_theory_id(thyPtr);
        jclass proxyCls = theoryProxyClasses[tid];
        jobject proxy   = theoryProxies[tid];
        jclass proxyCls2 = (*jniEnv)->GetObjectClass(jniEnv, proxy);
        jmethodID mid   = (*jniEnv)->GetMethodID(jniEnv, proxyCls, "newApp", "(JJ)V");
        (*jniEnv)->CallVoidMethod(jniEnv, proxy, mid, thyPtr, astToJLong(a));
    }

    void new_elem_default_callback(__in Z3_theory t, __in Z3_ast a) {
        jlong thyPtr = theoryToJLong(t);
        unsigned tid = get_theory_id(thyPtr);
        jclass proxyCls = theoryProxyClasses[tid];
        jobject proxy   = theoryProxies[tid];
        jclass proxyCls2 = (*jniEnv)->GetObjectClass(jniEnv, proxy);
        jmethodID mid   = (*jniEnv)->GetMethodID(jniEnv, proxyCls, "newElem", "(JJ)V");
        (*jniEnv)->CallVoidMethod(jniEnv, proxy, mid, thyPtr, astToJLong(a));
    }

    void init_search_default_callback(__in Z3_theory t) {
        jlong thyPtr = theoryToJLong(t);
        unsigned tid = get_theory_id(thyPtr);
        jclass proxyCls = theoryProxyClasses[tid];
        jobject proxy   = theoryProxies[tid];
        jclass proxyCls2 = (*jniEnv)->GetObjectClass(jniEnv, proxy);
        jmethodID mid   = (*jniEnv)->GetMethodID(jniEnv, proxyCls, "initSearch", "(J)V");
        (*jniEnv)->CallVoidMethod(jniEnv, proxy, mid, thyPtr);
    }

    void push_default_callback(__in Z3_theory t) {
        jlong thyPtr = theoryToJLong(t);
        unsigned tid = get_theory_id(thyPtr);
        jclass proxyCls = theoryProxyClasses[tid];
        jobject proxy   = theoryProxies[tid];
        jclass proxyCls2 = (*jniEnv)->GetObjectClass(jniEnv, proxy);
        jmethodID mid   = (*jniEnv)->GetMethodID(jniEnv, proxyCls, "push", "(J)V");
        (*jniEnv)->CallVoidMethod(jniEnv, proxy, mid, thyPtr);
    }

    void pop_default_callback(__in Z3_theory t) {
        jlong thyPtr = theoryToJLong(t);
        unsigned tid = get_theory_id(thyPtr);
        jclass proxyCls = theoryProxyClasses[tid];
        jobject proxy   = theoryProxies[tid];
        jclass proxyCls2 = (*jniEnv)->GetObjectClass(jniEnv, proxy);
        jmethodID mid   = (*jniEnv)->GetMethodID(jniEnv, proxyCls, "pop", "(J)V");
        (*jniEnv)->CallVoidMethod(jniEnv, proxy, mid, thyPtr);
    }

    void restart_default_callback(__in Z3_theory t) {
        jlong thyPtr = theoryToJLong(t);
        unsigned tid = get_theory_id(thyPtr);
        jclass proxyCls = theoryProxyClasses[tid];
        jobject proxy   = theoryProxies[tid];
        jclass proxyCls2 = (*jniEnv)->GetObjectClass(jniEnv, proxy);
        jmethodID mid   = (*jniEnv)->GetMethodID(jniEnv, proxyCls, "restart", "(J)V");
        (*jniEnv)->CallVoidMethod(jniEnv, proxy, mid, thyPtr);
    }

    void reset_default_callback(__in Z3_theory t) {
        jlong thyPtr = theoryToJLong(t);
        unsigned tid = get_theory_id(thyPtr);
        jclass proxyCls = theoryProxyClasses[tid];
        jobject proxy   = theoryProxies[tid];
        jclass proxyCls2 = (*jniEnv)->GetObjectClass(jniEnv, proxy);
        jmethodID mid   = (*jniEnv)->GetMethodID(jniEnv, proxyCls, "reset", "(J)V");
        (*jniEnv)->CallVoidMethod(jniEnv, proxy, mid, thyPtr);
    }

    Z3_bool final_check_default_callback_fptr(__in Z3_theory t) {
        jlong thyPtr = theoryToJLong(t);
        unsigned tid = get_theory_id(thyPtr);
        jclass proxyCls = theoryProxyClasses[tid];
        jobject proxy   = theoryProxies[tid];
        jclass proxyCls2 = (*jniEnv)->GetObjectClass(jniEnv, proxy);
        jmethodID mid   = (*jniEnv)->GetMethodID(jniEnv, proxyCls, "finalCheck", "(J)Z");
        jboolean result = (*jniEnv)->CallBooleanMethod(jniEnv, proxy, mid, thyPtr);
        return (result == JNI_TRUE ? Z3_TRUE : Z3_FALSE);
    }

    void new_eq_default_callback(__in Z3_theory t, __in Z3_ast a, __in Z3_ast b) {
        jlong thyPtr = theoryToJLong(t);
        unsigned tid = get_theory_id(thyPtr);
        jclass proxyCls = theoryProxyClasses[tid];
        jobject proxy   = theoryProxies[tid];
        jclass proxyCls2 = (*jniEnv)->GetObjectClass(jniEnv, proxy);
        jmethodID mid   = (*jniEnv)->GetMethodID(jniEnv, proxyCls, "newEq", "(JJJ)V");
        (*jniEnv)->CallVoidMethod(jniEnv, proxy, mid, thyPtr, astToJLong(a), astToJLong(b));
    }

    void new_diseq_default_callback(__in Z3_theory t, __in Z3_ast a, __in Z3_ast b) {
        jlong thyPtr = theoryToJLong(t);
        unsigned tid = get_theory_id(thyPtr);
        jclass proxyCls = theoryProxyClasses[tid];
        jobject proxy   = theoryProxies[tid];
        jclass proxyCls2 = (*jniEnv)->GetObjectClass(jniEnv, proxy);
        jmethodID mid   = (*jniEnv)->GetMethodID(jniEnv, proxyCls, "newDiseq", "(JJJ)V");
        (*jniEnv)->CallVoidMethod(jniEnv, proxy, mid, thyPtr, astToJLong(a), astToJLong(b));
    }

    void new_assignment_default_callback(__in Z3_theory t, __in Z3_ast a, __in Z3_bool p) {
        //printf("newAssDefaultCall %d %d %d", t, a, p);
        jlong thyPtr = theoryToJLong(t);
        unsigned tid = get_theory_id(thyPtr);
        //printf("thid is %d\n", tid);
        jclass proxyCls = theoryProxyClasses[tid];
        jobject proxy   = theoryProxies[tid];
        jclass proxyCls2 = (*jniEnv)->GetObjectClass(jniEnv, proxy);
        jmethodID mid   = (*jniEnv)->GetMethodID(jniEnv, proxyCls, "newAssignment", "(JJZ)V");
        (*jniEnv)->CallVoidMethod(jniEnv, proxy, mid, thyPtr, astToJLong(a), (p == Z3_TRUE ? JNI_TRUE : JNI_FALSE));
    }

    void new_relevant_default_callback(__in Z3_theory t, __in Z3_ast a) {
        jlong thyPtr = theoryToJLong(t);
        unsigned tid = get_theory_id(thyPtr);
        jclass proxyCls = theoryProxyClasses[tid];
        jobject proxy   = theoryProxies[tid];
        jclass proxyCls2 = (*jniEnv)->GetObjectClass(jniEnv, proxy);
        jmethodID mid   = (*jniEnv)->GetMethodID(jniEnv, proxyCls, "newRelevant", "(JJ)V");
        (*jniEnv)->CallVoidMethod(jniEnv, proxy, mid, thyPtr, astToJLong(a));
    }

#ifdef __cplusplus
}
#endif
