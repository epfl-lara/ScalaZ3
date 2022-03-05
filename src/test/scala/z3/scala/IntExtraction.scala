package z3.scala

import org.scalatest.{FunSuite, Matchers}

class IntExtraction extends FunSuite with Matchers {

  test(s"BigInt extraction") {
    val z3 = new Z3Context("MODEL" -> true)

    val i = z3.mkIntSort
    val x = z3.mkConst(z3.mkStringSymbol("x"), i)
    val m = z3.mkInt(Int.MaxValue, i)

    val solver = z3.mkSolver

    solver.assertCnstr(z3.mkEq(x, z3.mkAdd(m, m)))

    val (sol, model) = solver.checkAndGetModel

    sol should equal(Some(true))
    model.evalAs[BigInt](x) should equal(Some(BigInt(Int.MaxValue) * 2))

    z3.delete
  }
}

