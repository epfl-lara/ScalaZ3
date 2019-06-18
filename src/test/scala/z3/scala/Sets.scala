package z3.scala

import org.scalatest.{FunSuite, Matchers}

class Sets extends FunSuite with Matchers {

  test("Sets") {
    val z3 = new Z3Context("MODEL" -> true)

    val is = z3.mkIntSort
    val iss = z3.mkSetSort(is)
    val s1 = z3.mkFreshConst("s1", iss)
    val s2 = z3.mkFreshConst("s2", iss)

    val solver = z3.mkSolver
    solver.assertCnstr(z3.mkDistinct(s1, s2))

    val (result, model) = solver.checkAndGetModel
    result should equal(Some(true))

    val s1eval = model.eval(s1)
    val s2eval = model.eval(s2)
    s1eval should be (Symbol("defined"))
    s2eval should be (Symbol("defined"))
    (s1eval,s2eval) match {
      case (Some(se1), Some(se2)) =>
        val s1val = model.getSetValue(se1)
        val s2val = model.getSetValue(se2)
        s1val should be (Symbol("defined"))
        s2val should be (Symbol("defined"))
        s1val should not equal (s2val)
        //println("Set values :" + s1val + ", " + s2val)
      case _ =>
    }
  }
}

