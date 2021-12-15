package z3.scala

import org.scalatest.{FunSuite, Matchers}

class Optimizer extends FunSuite with Matchers {

  test("Optimizer") {
    val z3 = new Z3Context("MODEL" -> true)

    val is = z3.mkIntSort()
    val x = z3.mkIntConst("x")
    val y = z3.mkIntConst("y")

    val a1 = z3.mkGT(x, z3.mkInt(0, is))
    val a2 = z3.mkLT(x, y)
    val a3 = z3.mkLE(z3.mkAdd(y, x), z3.mkInt(0, is))

    val opt = z3.mkOptimizer()
    opt.assertCnstr(z3.mkIff(a3, a1))
    opt.assertCnstr(z3.mkOr(a3, a2))
    opt.assertCnstr(a3,           3)
    opt.assertCnstr(z3.mkNot(a3), 5)
    opt.assertCnstr(z3.mkNot(a1), 10)
    opt.assertCnstr(z3.mkNot(a2), 3)

    val result = opt.check()
    result should equal (Some(true))

    val model = opt.getModel()
    val a1eval = model.evalAs[Boolean](a1)
    val a2eval = model.evalAs[Boolean](a2)
    val a3eval = model.evalAs[Boolean](a3)

    a1eval should equal (Some(true))
    a2eval should equal (Some(false))
    a3eval should equal (Some(true))
  }
}
