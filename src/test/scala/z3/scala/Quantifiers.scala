package z3.scala

import org.scalatest.{FunSuite, Matchers}

class Quantifiers extends FunSuite with Matchers {

  /*
   * (declare-sort Type)
   * (declare-fun subtype (Type Type) Bool)
   * (declare-fun array-of (Type) Type)
   * (assert (forall ((x Type)) (subtype x x)))
   * (assert (forall ((x Type) (y Type) (z Type))
   *             (=> (and (subtype x y) (subtype y z)) 
   *                             (subtype x z)))) 
   * (assert (forall ((x Type) (y Type))
   *             (=> (and (subtype x y) (subtype y x)) 
   *                             (= x y))))
   * (assert (forall ((x Type) (y Type) (z Type))
   *             (=> (and (subtype x y) (subtype x z)) 
   *                             (or (subtype y z) (subtype z y))))) 
   * (assert (forall ((x Type) (y Type))
   *             (=> (subtype x y) 
   *                             (subtype (array-of x) (array-of y)))))
   * (declare-const root-type Type)
   * (assert (forall ((x Type)) (subtype x root-type)))
   * (check-sat)
   */

  test("Quantifiers") {
    val z3 = new Z3Context("MODEL" -> true)
    val solver = z3.mkSolver

    /*
     * (declare-sort Type)
     * (declare-fun subtype (Type Type) Bool)
     * (declare-fun array-of (Type) Type)
     */
    val typeSort = z3.mkUninterpretedSort("Type")
    val subtype = z3.mkFuncDecl("subtype", List[Z3Sort](typeSort, typeSort).toArray, z3.mkBoolSort)
    val arrayOf = z3.mkFuncDecl("array-of", List[Z3Sort](typeSort).toArray, typeSort)

    val syms @ List(xSym, ySym, zSym) = List("x", "y", "z").map(z3.mkSymbol(_))
    val consts @ List(x, y, z) = syms.map(sym => z3.mkConst(sym, typeSort))

    /* (assert (forall ((x Type)) (subtype x x))) */
    solver.assertCnstr(z3.mkForall(0, Seq.empty,
      Seq(xSym -> typeSort),
      subtype(x, x)))

    /* (assert (forall ((x Type) (y Type) (z Type))
                   (=> (and (subtype x y) (subtype y z)) 
                                  (subtype x z)))) */
    solver.assertCnstr(z3.mkForall(0, Seq.empty,
      Seq(xSym -> typeSort, ySym -> typeSort, zSym -> typeSort),
      z3.mkImplies(z3.mkAnd(subtype(x, y), subtype(y, z)), subtype(x, z))))

    /* (assert (forall ((x Type) (y Type))
                   (=> (and (subtype x y) (subtype y x)) 
                                  (= x y)))) */
    solver.assertCnstr(z3.mkForall(0, Seq.empty,
      Seq(xSym -> typeSort, ySym -> typeSort),
      z3.mkImplies(z3.mkAnd(subtype(x, y), subtype(y, x)), z3.mkEq(x, y))))

    /* (assert (forall ((x Type) (y Type) (z Type))
                   (=> (and (subtype x y) (subtype x z)) 
                                  (or (subtype y z) (subtype z y))))) */
    solver.assertCnstr(z3.mkForall(0, Seq.empty,
      Seq(xSym -> typeSort, ySym -> typeSort, zSym -> typeSort),
      z3.mkImplies(z3.mkAnd(subtype(x, y), subtype(x, z)), z3.mkOr(subtype(y, z), subtype(z, y)))))

    /* (assert (forall ((x Type) (y Type))
                  (=> (subtype x y) 
                                  (subtype (array-of x) (array-of y))))) */
    solver.assertCnstr(z3.mkForall(0, Seq.empty,
      Seq(xSym -> typeSort, ySym -> typeSort),
      z3.mkImplies(subtype(x, y), subtype(arrayOf(x), arrayOf(y)))))

    /* (declare-const root-type Type) */
    val rootType = z3.mkConst("root-type", typeSort)

    /* (assert (forall ((x Type)) (subtype x root-type))) */
    solver.assertCnstr(z3.mkForall(0, Seq.empty,
      Seq(xSym -> typeSort),
      subtype(x, rootType)))

    solver.check should equal(Some(true))
  }
}
