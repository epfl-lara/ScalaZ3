#include <jni.h>
#include <z3.h>
#include <stdio.h>
#include <stdlib.h>

#ifndef _Z3_THYCALLBACKS_H_
#define _Z3_THYCALLBACKS_H_

#ifndef __in
#define __in
#endif

#ifndef __out
#define __out
#endif

#define MAXSUPPORTEDZ3THEORIES 128

#ifdef __cplusplus
extern "C" {
#endif

    // this should make sure that somewhere, we have a link from thyPtr to
    // the jobject...
    void set_callback_jni_env(JNIEnv * env, jlong thyPtr, jobject theoryProxy);

    // this will retrieve the right TheoryProxy id...
    unsigned get_theory_id(jlong thyPtr);

    jobject mk_fresh_pointer(JNIEnv * env);

    jlong read_pointer_value(JNIEnv * env, jobject pointer);

    void delete_default_callback(__in Z3_theory t);

    Z3_bool reduce_app_default_callback(__in Z3_theory t, __in Z3_func_decl fd, __in unsigned argc, __in Z3_ast const args[], __out Z3_ast * r);

    Z3_bool reduce_eq_default_callback(__in Z3_theory t, __in Z3_ast a, __in Z3_ast b, __out Z3_ast * r);

    Z3_bool reduce_distinct_default_callback(__in Z3_theory t, __in unsigned argc, __in Z3_ast const args[], __out Z3_ast * r);

    void new_app_default_callback(__in Z3_theory t, __in Z3_ast a);

    void new_elem_default_callback(__in Z3_theory t, __in Z3_ast a);

    void init_search_default_callback(__in Z3_theory t);

    void push_default_callback(__in Z3_theory t);

    void pop_default_callback(__in Z3_theory t);

    void restart_default_callback(__in Z3_theory t);

    void reset_default_callback(__in Z3_theory t);

    Z3_bool final_check_default_callback_fptr(__in Z3_theory t);

    void new_eq_default_callback(__in Z3_theory t, __in Z3_ast a, __in Z3_ast b);

    void new_diseq_default_callback(__in Z3_theory t, __in Z3_ast a, __in Z3_ast b);

    void new_assignment_default_callback(__in Z3_theory t, __in Z3_ast a, __in Z3_bool p);

    void new_relevant_default_callback(__in Z3_theory t, __in Z3_ast a);

#ifdef __cplusplus
}
#endif
#endif
