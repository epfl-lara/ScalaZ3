#include <z3.h>
#include <jni.h>

#ifndef __SCALA_Z3_CASTS_H_
#define __SCALA_Z3_CASTS_H_

#ifdef __cplusplus
extern "C" {
#endif
    // Cast functions (to limit the number of 'different size' cast errors).
    inline Z3_config asZ3Config(jlong cfg);
    inline Z3_context asZ3Context(jlong context);
    inline Z3_sort asZ3Sort(jlong sort);
    inline Z3_func_decl asZ3FuncDecl(jlong fd);
    inline Z3_ast asZ3AST(jlong ast);
    inline Z3_app asZ3App(jlong app);
    inline Z3_pattern asZ3Pattern(jlong pat);
    inline Z3_symbol asZ3Symbol(jlong sym);
    inline Z3_parameter asZ3Parameter(jlong param);
    inline Z3_model asZ3Model(jlong model);
    inline Z3_literals asZ3Literals(jlong lits);
    inline Z3_constructor asZ3Constructor(jlong cons);
    inline Z3_constructor_list asZ3ConstructorList(jlong consList);
    inline Z3_theory asZ3Theory(jlong thy);

    // Casts from Z3 types to jlong pointer-wrappers
    #define JLONG_MY_NULL (jlong)0
    inline jlong configToJLong(Z3_config cfg);
    inline jlong contextToJLong(Z3_context ctx);
    inline jlong sortToJLong(Z3_sort sort);
    inline jlong funcDeclToJLong(Z3_func_decl fd);
    inline jlong astToJLong(Z3_ast ast);
    inline jlong appToJLong(Z3_app app);
    inline jlong patternToJLong(Z3_pattern p);
    inline jlong symbolToJLong(Z3_symbol sym);
    inline jlong parameterToJLong(Z3_parameter param);
    inline jlong modelToJLong(Z3_model model);
    inline jlong literalsToJLong(Z3_literals literals);
    inline jlong constructorToJLong(Z3_constructor constructor);
    inline jlong constructorListToJLong(Z3_constructor_list constructorList);
    inline jlong theoryToJLong(Z3_theory theory);

#ifdef __cplusplus
}
#endif
#endif
