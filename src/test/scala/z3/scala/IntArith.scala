package z3

import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers

class IntArith extends FunSuite with ShouldMatchers {
  import z3.scala._

  test("Comfusy-like") {
    val z3 = new Z3Context(new Z3Config("MODEL" -> true))
    val i = z3.mkIntSort
    val h = z3.mkConst(z3.mkStringSymbol("h"), i)
    val m = z3.mkConst(z3.mkStringSymbol("m"), i)
    val s = z3.mkConst(z3.mkStringSymbol("s"), i)
    // builds a constant integer value from the CL arg.
    val t = z3.mkInt(1234, i)
    // more integer constants
    val z = z3.mkInt(0, i)
    val sx = z3.mkInt(60, i)
    // builds the constraint h*3600 + m * 60 + s == totSecs
    val cs1 = z3.mkEq(
    z3.mkAdd(
      z3.mkMul(z3.mkInt(3600, i), h),
      z3.mkMul(sx,                m),
      s),
    t)
    // more constraints
    val cs2 = z3.mkAnd(z3.mkGE(h, z), z3.mkLT(h, z3.mkInt(24, i)))
    val cs3 = z3.mkAnd(z3.mkGE(m, z), z3.mkLT(m, sx))
    val cs4 = z3.mkAnd(z3.mkGE(s, z), z3.mkLT(s, sx))

    val solver = z3.mkSolver
    solver.assertCnstr(z3.mkAnd(cs1, cs2, cs3, cs4))

    // attempting to solve the constraints
    val (sol, model) = solver.checkAndGetModel

    sol should equal(Some(true))
    model.evalAs[Int](h) should equal(Some(0))
    model.evalAs[Int](m) should equal(Some(20))
    model.evalAs[Int](s) should equal(Some(34))

    model.delete
    z3.delete
  }
}

