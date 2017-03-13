package z3.scala

import org.scalatest.{FunSuite, Matchers}

class Sequences extends FunSuite with Matchers {

  test("Simple SAT") {
    val z3 = new Z3Context("MODEL" -> true)

    val is = z3.mkIntSort
    val iss = z3.mkSeqSort(is)
    val s1 = z3.mkFreshConst("s1", iss)
    val s2 = z3.mkFreshConst("s2", iss)

    val solver = z3.mkSolver
    solver.assertCnstr(z3.mkDistinct(s1, s2))

    val (result, model) = solver.checkAndGetModel
    result should equal (Some(true))

    val s1eval = model.eval(s1)
    val s2eval = model.eval(s2)

    s1eval should be ('defined)
    s2eval should be ('defined)
  }

  test("Different head") {
    val z3 = new Z3Context("MODEL" -> true)

    val is = z3.mkIntSort
    val iss = z3.mkSeqSort(is)
    val s1 = z3.mkFreshConst("s1", iss)
    val s2 = z3.mkFreshConst("s2", iss)

    val solver = z3.mkSolver
    solver.assertCnstr(z3.mkEq(s1, s2))
    solver.assertCnstr(z3.mkEq(z3.mkUnitSeq(z3.mkInt(1, is)), z3.mkSeqExtract(s1, z3.mkInt(0, is), z3.mkInt(1, is))))
    solver.assertCnstr(z3.mkEq(z3.mkUnitSeq(z3.mkInt(0, is)), z3.mkSeqExtract(s2, z3.mkInt(0, is), z3.mkInt(1, is))))

    val result = solver.check
    result should equal (Some(false))
  }

  test("Compatible sub-sequences") {
    val z3 = new Z3Context("MODEL" -> true)

    val is = z3.mkIntSort
    val iss = z3.mkSeqSort(is)
    val s1 = z3.mkFreshConst("s1", iss)
    val s2 = z3.mkFreshConst("s2", iss)

    val solver = z3.mkSolver
    solver.assertCnstr(z3.mkEq(s1, s2))
    solver.assertCnstr(z3.mkEq(z3.mkUnitSeq(z3.mkInt(1, is)), z3.mkSeqExtract(s1, z3.mkInt(0, is), z3.mkInt(1, is))))
    solver.assertCnstr(z3.mkEq(z3.mkUnitSeq(z3.mkInt(0, is)), z3.mkSeqExtract(s2, z3.mkInt(1, is), z3.mkInt(1, is))))

    val (result, model) = solver.checkAndGetModel
    result should equal (Some(true))

    val s1eval = model.eval(s1)
    val s2eval = model.eval(s2)

    s1eval should be ('defined)
    s2eval should be ('defined)
  }
}
