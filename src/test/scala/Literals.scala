import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers

class Literals extends FunSuite with ShouldMatchers {
  import z3.scala._

  test("Literals") {
    val z3 = new Z3Context(new Z3Config("MODEL" -> true))

    val bs = z3.mkBoolSort
    val p1 = z3.mkFreshConst("p", bs)
    val p2 = z3.mkFreshConst("p", bs)
    val p3 = z3.mkFreshConst("p", bs)

    val cstr = (p1 --> p2) && (p2 || p3)

//    val cstr = z3.mkFreshConst("i", z3.mkIntSort) > z3.mkInt(0, z3.mkIntSort)

    println("Constraint: " + cstr)
    z3.assertCnstr(cstr)

    var continue = true
    var unsat = false

    var nbInvocations = 0

    while(continue) {
      z3.checkAndGetModel match {
        case (None, model) => println("Z3 failed. The reason is: " + z3.getSearchFailure.message); continue = false; model.delete
        case (Some(false), _) => println("Unsat."); unsat = true
        case (Some(true), model) => println("Sat. The model is: \n" + model); model.delete
      }

      nbInvocations = nbInvocations + 1

      if(continue) {
        val literals = z3.getRelevantLiterals
        val numLits = literals.getNumLiterals

        if(numLits > 0) {
          println(numLits + " interesting literals.")
  
          for(lit: Z3AST <- literals.getLiterals) {
            println("Literal : " + lit)
          }

          println("-----\n")
          literals.block
        } else {
          println("No interesting literals.")
          println("-----\n")
          continue = false
        }

        if(unsat) continue = false
      } 
    }

    nbInvocations should equal (4)
  }
}

