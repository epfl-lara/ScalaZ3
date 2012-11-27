package z3

import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers

class Quantifiers extends FunSuite with ShouldMatchers {
  import z3.scala._

  test("Quantifiers") {
    val config = new Z3Config
    config.setParamValue("MODEL", "true")
    val z3 = new Z3Context(config)
    toggleWarningMessages(true)
//    z3.traceToStdout
    val intSort = z3.mkIntSort

    val fibonacci = z3.mkFreshFuncDecl("fib", List(intSort), intSort)

    val fib0 = z3.mkEq(fibonacci(z3.mkInt(0, intSort)), z3.mkInt(0, intSort))
    val fib1 = z3.mkEq(fibonacci(z3.mkInt(1, intSort)), z3.mkInt(1, intSort))

    // FORALL x . x > 1 ==> fib(x) = fib(x-1) + fib(x-2)
    val boundVar = z3.mkBound(0, intSort)
    val pattern: Z3Pattern = z3.mkPattern(fibonacci(boundVar))
    val axiomTree = z3.mkImplies(
        z3.mkGT(boundVar, z3.mkInt(1, intSort)),
        z3.mkEq(
          fibonacci(boundVar),
          z3.mkAdd(
            fibonacci(z3.mkSub(boundVar, z3.mkInt(1, intSort))),
            fibonacci(z3.mkSub(boundVar, z3.mkInt(2, intSort))))
          )
        ) 

//    val axiomTree = z3.mkEq(fibonacci(boundVar), boundVar)

    val someName: Z3Symbol = z3.mkIntSymbol(0)
    val fibN = z3.mkQuantifier(true, 0, List(pattern), List((someName, intSort)), axiomTree)
    println("fib0 ::: " + fib0)
    println("fib1 ::: " + fib1)
    println("fibN ::: " + fibN)

    val solver = z3.mkSolver
    solver.assertCnstr(fib0)
    solver.assertCnstr(fib1)
    solver.assertCnstr(fibN)

    // z3.push
    // z3.assertCnstr(z3.mkEq(fibonacci(z3.mkInt(4, intSort)), z3.mkInt(4, intSort)))

    // val (answer, model) = z3.checkAndGetModel

    // println("fibonacci(4) = 4 ?")
    // println(answer match {
    //   case Some(true) => "possibly"
    //   case Some(false) => "no way"
    //   case _ => "error"
    // })

    // z3.pop(0)

    val x = z3.mkConst(z3.mkStringSymbol("v"), intSort)
    val query = z3.mkEq(x, fibonacci(z3.mkInt(5, intSort)))
    // println("Query ::: " + query)
    solver.assertCnstr(query)
    
    // val (answer2, model2) = z3.checkAndGetModel

    // println("fibonacci(5) = ?")
    // answer2 should equal(None)
    // model2.evalAs[Int](x) should equal(Some(5))

    // model2.delete
    z3.delete
  }
}
