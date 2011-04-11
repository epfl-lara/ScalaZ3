import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import z3.scala._
import z3.scala.Z3ASTTypes._

class EmptyTheory(z3: Z3Context) extends Z3Theory(z3, "dummy")

class ADTs extends FunSuite with ShouldMatchers {
  test("ADTs") {
    val config = new Z3Config("MODEL" -> "true", "RELEVANCY" -> "0")
    val ctx = new Z3Context(config)
    val thy = new EmptyTheory(ctx)
    // ctx.traceToStdout()
    toggleWarningMessages(true)
    val intSort = ctx.mkIntSort

    import Z3Context.{RecursiveType,RegularSort}

    // this builds two (recursive) datatypes:
    //   Tree ::= Tree * Int * Tree | Leaf
    // and
    //   TreeList ::= Tree TreeList | Nil
    // ...and constructor/tester functions, as well as selectors.
    val creationResult = ctx.mkADTSorts(
      List(
        ("Tree",
          List("Node", "Leaf"),
          List(
            List(  // Fields for Node
              ("left", RecursiveType(0)),   // '0' means "Tree", because that's the first in the mkADTSorts call.
              ("value", RegularSort(intSort)),
              ("right", RecursiveType(0))),
            Nil    // Fields for Leaf
          )
        ),
        ("TreeList",
          List("Cons", "Nil"),
          List(
            List( // Fields for Cons
              ("head", RecursiveType(0)),    // head has type Tree
              ("tail", RecursiveType(1))),
            Nil   // Nields for Nil
          )
        )
      )
    ).toList

    // we then recover all the relevant Function Declarations...
    assert(creationResult.size == 2)
    val firstType = creationResult(0)
    val (treeSort: Z3Sort, consSeq, testSeq, selesSeq) = firstType
    assert(consSeq.size == 2)
    assert(testSeq.size == 2)
    assert(selesSeq.size == 2)
    val nodeCons: Z3FuncDecl = consSeq(0)
    val leafCons: Z3FuncDecl = consSeq(1)
    val isNode: Z3FuncDecl = testSeq(0)
    val isLeaf: Z3FuncDecl = testSeq(1)
    val nodeSels: Seq[Z3FuncDecl] = selesSeq(0)
    assert(selesSeq(1).size == 0) // no fields for Leaf
    assert(nodeSels.size == 3)
    val nodeLeftSelector: Z3FuncDecl = nodeSels(0)
    val nodeValueSelector: Z3FuncDecl = nodeSels(1)
    val nodeRightSelector: Z3FuncDecl = nodeSels(2)

    val secondType = creationResult(1)
    val (treeListSort: Z3Sort, consSeq2, testSeq2, selesSeq2) = secondType
    assert(consSeq2.size == 2)
    assert(testSeq2.size == 2)
    assert(selesSeq2.size == 2)
    val consCons: Z3FuncDecl = consSeq2(0)
    val nilCons:  Z3FuncDecl = consSeq2(1)
    val isCons: Z3FuncDecl = testSeq2(0)
    val isNil: Z3FuncDecl = testSeq2(1)
    val consHeadSelector: Z3FuncDecl = selesSeq2(0)(0)
    val consTailSelector: Z3FuncDecl = selesSeq2(0)(1)

    // ...and finally we can use them to build a problem.
    val x:  TypedZ3AST[NumeralType] = ctx.mkConst[NumeralType](ctx.mkStringSymbol("x"), intSort)
    val t1: Z3AST = ctx.mkConst(ctx.mkStringSymbol("t1"), treeSort)
    val t2: Z3AST = ctx.mkConst(ctx.mkStringSymbol("t2"), treeSort)
    val t3: Z3AST = ctx.mkConst(ctx.mkStringSymbol("t3"), treeSort)

    // t1 == t2
    //ctx.assertCnstr(ctx.mkEq(t1,t2))
    //// t1 != t3
    //ctx.assertCnstr(ctx.mkDistinct(t1,t3))
    //// x > 4
    //ctx.assertCnstr(ctx.mkGT(x, ctx.mkInt(4, intSort)))

    ctx.assertCnstr(t1 === leafCons())
    ctx.assertCnstr(nodeValueSelector(t1) === ctx.mkInt(4, intSort))

    // t1 != Leaf
    //ctx.assertCnstr(ctx.mkNot(isLeaf(t1)))

    // isNode(t2) => (t2 = Node(Leaf, x, t3))
    // ctx.assertCnstr(ctx.mkImplies(isNode(t2), ctx.mkEq(t2, nodeCons(leafCons(), x, t3))))
    // replace by this and it becomes unsat..
    // ctx.assertCnstr(ctx.mkEq(t1,t3))

    //println(ctx.mkImplies(isNode(t2), nodeValueSelector(t2) === ctx.mkInt(12, intSort)))

    val (sol, model) = ctx.checkAndGetModel

    sol should equal(Some(true))
    model.eval(t1) should equal(Some(leafCons()))

    model.delete
    ctx.delete
  }
}

