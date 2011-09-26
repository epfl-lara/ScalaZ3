#ifndef __SCALA_Z3_EXTRA_H_ 
#define __SCALA_Z3_EXTRA_H_ 

#include <z3.h>
#include <jni.h>

#ifdef __cplusplus
extern "C" {
#endif
    jlong parseSMTLIBStringCommon (JNIEnv * env, jclass cls, jboolean isSMTLIB2, jlong contextPtr, jstring str, jint numSorts, jlongArray sortNames, jlongArray sorts, jint numDecls, jlongArray declNames, jlongArray decls);

    jlong parseSMTLIBFileCommon (JNIEnv * env, jclass cls, jboolean isSMTLIB2, jlong contextPtr, jstring str, jint numSorts, jlongArray sortNames, jlongArray sorts, jint numDecls, jlongArray declNames, jlongArray decls);
#ifdef __cplusplus
}
#endif
#endif
