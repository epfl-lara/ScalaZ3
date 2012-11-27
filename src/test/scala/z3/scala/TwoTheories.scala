import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers

class TwoTheories extends FunSuite with ShouldMatchers {
  import z3.scala._

  class Theory1(z3: Z3Context) extends Z3Theory(z3, "TH1") {
    val sort = mkTheorySort(z3.mkStringSymbol("t1sort"))
    // FORALL x, y : p(x, y) ==> x == y
    val p    = mkTheoryFuncDecl(z3.mkStringSymbol("p1"), List(sort, sort), z3.mkBoolSort)

    setCallbacks(
      newAssignment = true
    )
    showCallbacks(false)

    override def newAssignment(pred: Z3AST, polarity: Boolean) : Unit = z3.getASTKind(pred) match {
      case Z3AppAST(d, args) if d == p => {
        val axiom = if(polarity) {
          z3.mkImplies(pred, args(0) === args(1))
        } else {
          z3.mkImplies(z3.mkNot(args(0) === args(1)), z3.mkNot(pred))
        }
        println("From theory 1, adding the axiom:\n" + axiom)
        assertAxiom(axiom)
      }
      case _ => ;
    }
  }

  class Theory2(z3: Z3Context) extends Z3Theory(z3, "TH2") {
    val sort = mkTheorySort(z3.mkStringSymbol("t2sort"))
    // FORALL x, y : p(x, y) ==> x != y
    val p    = mkTheoryFuncDecl(z3.mkStringSymbol("p2"), List(sort, sort), z3.mkBoolSort)

    setCallbacks(
      newAssignment = true
    )
    showCallbacks(false)

    override def newAssignment(pred: Z3AST, polarity: Boolean) : Unit = z3.getASTKind(pred) match {
      case Z3AppAST(d, args) if d == p => {
        val axiom = if(polarity) {
          z3.mkImplies(pred, z3.mkNot(args(0) === args(1)))
        } else {
          z3.mkImplies((args(0) === args(1)), z3.mkNot(pred))
        }
        println("From theory 2, adding the axiom:\n" + axiom)
        assertAxiom(axiom)
      }
      case _ => ;
    }
  }

  test("Two theories") {
    val z3 = new Z3Context(new Z3Config("MODEL" -> true))

    // z3.traceToStdout

    toggleWarningMessages(true)

    val thy1 = new Theory1(z3)
    val thy2 = new Theory2(z3)
    
    val s1 = thy1.sort
    val p1 = thy1.p
    val s2 = thy2.sort
    val p2 = thy2.p

    val f12 = z3.mkFreshFuncDecl("s1tos2", List(s1), s2)
    val f21 = z3.mkFreshFuncDecl("s2tos1", List(s2), s1)

    val a1 = z3.mkFreshConst("a", s1)
    val b1 = z3.mkFreshConst("b", s1)
    val a2 = z3.mkFreshConst("a", s2)
    val b2 = z3.mkFreshConst("b", s2)

    val solver = z3.mkSolver
    solver.assertCnstr(f12(a1) === a2)
    solver.assertCnstr(f12(b1) === b2)
    solver.assertCnstr(f21(a2) === a1)
    solver.assertCnstr(f21(b2) === b1)
    solver.assertCnstr(p1(a1,b1))
    solver.assertCnstr(p2(a2,b2))

    val (result, model) = solver.checkAndGetModel()
    result should equal(Some(false))
  }
}

