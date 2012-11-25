import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers

class TheoryExtension extends FunSuite with ShouldMatchers {
  import z3.scala._

  /** A simple theory that declares a new sort and a partial order on it, with LT
   * and LE operations. */
  class PartialOrderTheory(z3: Z3Context) extends Z3Theory(z3, "Partial Order") {
      val tSort      = mkTheorySort(z3.mkStringSymbol("tSort"))
      val lessEqThan = mkTheoryFuncDecl(z3.mkStringSymbol("poLE"), List(tSort, tSort), z3.mkBoolSort)
      val lessThan   = mkTheoryFuncDecl(z3.mkStringSymbol("poLT"), List(tSort, tSort), z3.mkBoolSort)

      setCallbacks(
        reduceApp = true,
        newApp = true,
        newAssignment = true
      )

      override def reduceApp(fd: Z3FuncDecl, args: Z3AST*) : Option[Z3AST] = {
        if(fd == lessEqThan) {
          if(args(0) == args(1)) {
            Some(z3.mkTrue)
          } else {
            None
          }
        } else if(fd == lessThan) { 
          if(args(0) == args(1)) {
            Some(z3.mkFalse)
          } else {
            None
          }
        } else {
          None
        }
      }

      override def newApp(app: Z3AST) : Unit = {
        println("New app : " + app)
      }

      override def newAssignment(pred: Z3AST, polarity: Boolean) : Unit = {
        println("New assignment : " + pred)
      }
  }

  test("Theory extension") {
    val z3 = new Z3Context(new Z3Config("MODEL" -> true))
    toggleWarningMessages(true)
    val thy = new PartialOrderTheory(z3)

    val thySort = thy.tSort
    val thyLT = thy.lessThan
    val thyLE = thy.lessEqThan

    {
      z3.push
      println("Problem 0, should be solved by simplifying only (no model).")
      val a = z3.mkFreshConst("a", thySort)
      z3.assertCnstr(thyLE(a, a))
      z3.assertCnstr(z3.mkNot(thyLT(a, a)))
      val (res1, _) = z3.checkAndGetModel
      res1 should equal(Some(true))
      z3.pop(1)
    }

    {
      z3.push
      println("Problem 1.")
      
      val a = z3.mkFreshConst("a", thySort)
      val b = z3.mkFreshConst("b", thySort)
      val c = z3.mkFreshConst("c", thySort)

      z3.assertCnstr(thyLE(a, b))
      z3.assertCnstr(thyLE(b, c))
      z3.assertCnstr(thyLT(c, c))

      val (res2, _) = z3.checkAndGetModel
      res2 should equal(Some(false))
      z3.pop(1)
    }

  }
}

