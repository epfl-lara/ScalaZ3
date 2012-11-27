#include "casts.h"

// INTPTR is supposed to be an integer type of the same size as the pointers.
#if _WIN64 || __amd64__ || _LP64
  #define INTPTR jlong
#else
  #define INTPTR int
#endif

#ifdef __cplusplus
extern "C" {
#endif
    // Cast functions (to limit the number of 'different size' cast errors).
    inline Z3_config asZ3Config(jlong cfg) {
        return (Z3_config)(INTPTR)cfg;
    }

    inline Z3_context asZ3Context(jlong context) {
        return (Z3_context)(INTPTR)context;
    }

    inline Z3_sort asZ3Sort(jlong sort) {
        return (Z3_sort)(INTPTR)sort;
    }

    inline Z3_func_decl asZ3FuncDecl(jlong fd) {
        return (Z3_func_decl)(INTPTR)fd;
    }

    inline Z3_ast asZ3AST(jlong ast) {
        return (Z3_ast)(INTPTR)ast;
    }

    inline Z3_app asZ3App(jlong app) {
        return (Z3_app)(INTPTR)app;
    }

    inline Z3_pattern asZ3Pattern(jlong pat) {
        return (Z3_pattern)(INTPTR)pat;
    }

    inline Z3_symbol asZ3Symbol(jlong sym) {
        return (Z3_symbol)(INTPTR)sym;
    }

    inline Z3_parameter_kind asZ3Parameter(jlong param) {
        return (Z3_parameter_kind)(INTPTR)param;
    }

    inline Z3_model asZ3Model(jlong model) {
        return (Z3_model)(INTPTR)model;
    }

    inline Z3_literals asZ3Literals(jlong lits) {
        return (Z3_literals)(INTPTR)lits;
    }

    inline Z3_constructor asZ3Constructor(jlong cons) {
        return (Z3_constructor)(INTPTR)cons;
    }

    inline Z3_constructor_list asZ3ConstructorList(jlong consList) {
        return (Z3_constructor_list)(INTPTR)consList;
    }

    inline Z3_theory asZ3Theory(jlong thy) {
        return (Z3_theory)(INTPTR)thy;
    }

    inline Z3_tactic asZ3Tactic(jlong tactic) {
        return (Z3_tactic)(INTPTR)tactic;
    }

    inline Z3_solver asZ3Solver(jlong solver) {
        return (Z3_solver)(INTPTR)solver;
    }

    inline Z3_ast_vector asZ3Astvector(jlong vector) {
        return (Z3_ast_vector)(INTPTR)vector;
    }


    // Casts from Z3 types to jlong pointer-wrappers
    #define JLONG_MY_NULL (jlong)0
    inline jlong configToJLong(Z3_config cfg) {
        return (jlong)(INTPTR)cfg;
    }

    inline jlong contextToJLong(Z3_context ctx) {
        return (jlong)(INTPTR)ctx;
    }

    inline jlong sortToJLong(Z3_sort sort) {
        return (jlong)(INTPTR)sort;
    }

    inline jlong funcDeclToJLong(Z3_func_decl fd) {
        return (jlong)(INTPTR)fd;
    }

    inline jlong astToJLong(Z3_ast ast) {
        return (jlong)(INTPTR)ast;
    }

    inline jlong appToJLong(Z3_app app) {
        return (jlong)(INTPTR)app;
    }

    inline jlong patternToJLong(Z3_pattern p) {
        return (jlong)(INTPTR)p;
    }

    inline jlong symbolToJLong(Z3_symbol sym) {
        return (jlong)(INTPTR)sym;
    }

    inline jlong parameterToJLong(Z3_parameter_kind param) {
        return (jlong)(INTPTR)param;
    }

    inline jlong modelToJLong(Z3_model model) {
        return (jlong)(INTPTR)(INTPTR)model;
    }

    inline jlong literalsToJLong(Z3_literals literals) {
        return (jlong)(INTPTR)literals;
    }

    inline jlong constructorToJLong(Z3_constructor constructor) {
        return (jlong)(INTPTR)constructor;
    }

    inline jlong constructorListToJLong(Z3_constructor_list constructorList) {
        return (jlong)(INTPTR)constructorList;
    }

    inline jlong theoryToJLong(Z3_theory theory) {
        return (jlong)(INTPTR)theory;
    }

    inline jlong tacticToJLong(Z3_tactic tactic) {
        return (jlong)(INTPTR)tactic;
    }

    inline jlong solverToJLong(Z3_solver solver) {
        return (jlong)(INTPTR)solver;
    }

    inline jlong astvectorToJLong(Z3_ast_vector vector) {
        return (jlong)(INTPTR)vector;
    }

#ifdef __cplusplus
}
#endif
