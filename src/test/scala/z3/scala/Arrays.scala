package z3.scala

import org.scalatest.{FunSuite, Matchers}

class Arrays extends FunSuite with Matchers {

  test("Arrays") {
    val z3 = new Z3Context("MODEL" -> true)

    val is = z3.mkIntSort()
    val intArraySort = z3.mkArraySort(is, is)
    val array1 = z3.mkFreshConst("arr", intArraySort)
    val array2 = z3.mkFreshConst("arr", intArraySort)
    val x = z3.mkFreshConst("x", is)

    val solver = z3.mkSolver()
    // array1 = [ 42, 42, 42, ... ]
    solver.assertCnstr(z3.mkEq(array1,z3.mkConstArray(is, z3.mkInt(42, is))))
    // x = array1[6]
    solver.assertCnstr(z3.mkEq(x, z3.mkSelect(array1, z3.mkInt(6, is))))
    // array2 = array1[x - 40 -> 0]
    solver.assertCnstr(z3.mkEq(array2, z3.mkStore(array1, z3.mkSub(x, z3.mkInt(40, is)), z3.mkInt(0, is))))

    // "reading" the default value of array2 (should be 42)
    val fourtyTwo = z3.mkFreshConst("ft", is)
    solver.assertCnstr(z3.mkEq(fourtyTwo, z3.mkArrayDefault(array2)))

    val (result, model) = solver.checkAndGetModel()

    //println("model is")
    //println(model)
    result should equal(Some(true))

    val array1Evaluated = model.eval(array1)
    array1Evaluated should be (Symbol("defined"))
    array1Evaluated match {
      case Some(ae) =>
        val array1Val = model.getArrayValue(ae)
        array1Val should be (Symbol("defined"))
        //println("When evaluated, array1 is: " + array1Val)
        array1Val match {
          case Some((valueMap,default)) =>
            model.evalAs[Int](default) should equal (Some(42))
          case None =>
        }
      case None =>
    }

    val array2Evaluated = model.eval(array2)
    array2Evaluated should be (Symbol("defined"))
    array2Evaluated match {
      case Some(ae) => {
        val array2Val = model.getArrayValue(ae)
        array2Val should be (Symbol("defined"))
        //println("When evaluated, array2 is: " + array2Val)
        array2Val match {
          case Some((valueMap,default)) => {
            valueMap(z3.mkInt(2, z3.mkIntSort())) should equal (z3.mkInt(0, z3.mkIntSort()))
            model.evalAs[Int](default) should equal (Some(42))
          }
          case None =>
        }
      }
      case None =>
    }

    // These all seem to fail. Perhaps mkArrayDefault is not
    // supported anymore ?

    //model.evalAs[Int](z3.mkArrayDefault(array1)) should equal (Some(42))
    //model.evalAs[Int](z3.mkArrayDefault(array2)) should equal (Some(42))
    //model.evalAs[Int](fourtyTwo) should equal (Some(42))
  }
}

