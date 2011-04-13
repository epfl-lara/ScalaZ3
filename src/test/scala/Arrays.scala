import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers

class Arrays extends FunSuite with ShouldMatchers {
  import z3.scala._

  test("Arrays") {
    val z3 = new Z3Context(new Z3Config("MODEL" -> true))

    val is = z3.mkIntSort
    val intArraySort = z3.mkArraySort(is, is)
    val array1 = z3.mkFreshConst("arr", intArraySort)
    val array2 = z3.mkFreshConst("arr", intArraySort)
    val x = z3.mkFreshConst("x", is)

    // array1 = [ 42, 42, 42, ... ]
    z3.assertCnstr(array1 === z3.mkConstArray(is, z3.mkInt(42, is)))
    // x = array1[6]
    z3.assertCnstr(x === z3.mkSelect(array1, z3.mkInt(6, is)))
    // array2 = array1[x - 40 -> 0]
    z3.assertCnstr(array2 === z3.mkStore(array1, z3.mkSub(x, z3.mkInt(40, is)), z3.mkInt(0, is)))

    // "reading" the default value of array2 (should be 42)
    val fourtyTwo = z3.mkFreshConst("ft", is)
    z3.assertCnstr(fourtyTwo === z3.mkArrayDefault(array2))

    val (result, model) = z3.checkAndGetModel
    result should equal(Some(true))
    val array1Evaluated = model.eval(array1)
    array1Evaluated should be ('defined)
    array1Evaluated match {
      case Some(ae) =>
        val array1Val = z3.getArrayValue(ae)
        array1Val should be ('defined)
        array1Val match {
          case Some(av) =>
            av._1 should be ('empty)
            model.evalAs[Int](av._2) should equal (Some(42))
          case None =>
        }
      case None =>
    }
 
    // z3.checkAndGetModel match {
    //   case (None, _) => println("Z3 failed. The reason is: " + z3.getSearchFailure.message)
    //   case (Some(false), _) => println("Unsat.")
    //   case (Some(true), model) => println("Sat. The model is: \n" + model)
    //     println("and here is the first array: " + z3.getArrayValue(model.eval(array1).get))
    // }
  }
}

