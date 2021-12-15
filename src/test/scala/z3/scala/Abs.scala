package z3.scala

import org.scalatest.{FunSuite, Matchers}

class Abs extends FunSuite with Matchers {

  test("array-map absolute value") {
    val z3 = new Z3Context("MODEL" -> true)

    val is = z3.mkIntSort()
    val intArraySort = z3.mkArraySort(is, is)
    val array1 = z3.mkFreshConst("arr", intArraySort)
    val array2 = z3.mkFreshConst("arr", intArraySort)

    val abs = z3.getAbsFuncDecl()

    val solver = z3.mkSolver()

    solver.assertCnstr(z3.mkEq(z3.mkSelect(array1, z3.mkInt(0, is)), z3.mkInt(1, is)))
    solver.assertCnstr(z3.mkEq(z3.mkSelect(array1, z3.mkInt(1, is)), z3.mkInt(0, is)))
    solver.assertCnstr(z3.mkEq(z3.mkSelect(array1, z3.mkInt(2, is)), z3.mkInt(-1, is)))

    solver.assertCnstr(z3.mkEq(array2, z3.mkArrayMap(abs, array1)))

    val (result, model) = solver.checkAndGetModel()

    result should equal(Some(true))

    val array2Ev = model.eval(array2)
    array2Ev should be (Symbol("defined"))
    val array2Val = model.getArrayValue(array2Ev.get)
    array2Val should be (Symbol("defined"))
    val (valueMap0, default) = array2Val.get
    val valueMap = valueMap0.withDefaultValue(default)
    valueMap(z3.mkInt(0, is)) should equal (z3.mkInt(1, is))
    valueMap(z3.mkInt(1, is)) should equal (z3.mkInt(0, is))
    valueMap(z3.mkInt(2, is)) should equal (z3.mkInt(1, is))
  }
}
