package z3

import org.scalatest.{FunSuite, Matchers}

class Core extends FunSuite with Matchers {
  import z3.scala._
  import z3.scala.dsl._

  test("Core") {
    val z3 = new Z3Context("MODEL" -> "true")

    val x = z3.mkFreshConst("x", z3.mkIntSort)
    val y = z3.mkFreshConst("y", z3.mkIntSort)
    val p1 = z3.mkFreshConst("p1", z3.mkBoolSort)
    val p2 = z3.mkFreshConst("p2", z3.mkBoolSort)
    val p3 = z3.mkFreshConst("p3", z3.mkBoolSort)
  
    val zero = z3.mkInt(0, z3.mkIntSort)

    val solver = z3.mkSolver
    solver.assertCnstr(p1 --> !(!(x === zero)))
    solver.assertCnstr(p2 --> !(y === zero))
    solver.assertCnstr(p3 --> !(x === zero))

    val (result, model, core) = solver.checkAssumptionsGetModelOrCore(p1, p2, p3)

    result should equal (Some(false))
    core.toSet should equal (Set(p1, p3))
  }
}

