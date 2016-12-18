package z3.scala.dsl

import z3.scala.{Z3AST,Z3Context}

sealed trait TopSort
sealed trait BoolSort extends TopSort
sealed trait IntSort extends TopSort
sealed trait RealSort extends TopSort
sealed trait BVSort extends TopSort
sealed trait SetSort extends TopSort
sealed trait ArraySort extends TopSort
sealed trait BottomSort extends BoolSort with IntSort with RealSort with BVSort with SetSort with ArraySort

sealed trait Tree[+T >: BottomSort <: TopSort] {
  private[dsl] def build(z3 : Z3Context) : Z3AST

  private var built : Boolean = false
  private var t : Z3AST = null
  def ast(z3 : Z3Context) : Z3AST = {
    if(!built) {
      built = true
      t = build(z3)
    }
    t
  }
}

/** Instances of Val should never be created manually, but rather always
 * through a ValHandler. The type parameter refers to a Scala type for a
 * value that the user wishes to obtain through a call to choose, find or
 * findAll. */
abstract class Val[A] extends Tree[BottomSort] {
  import Operands._

  // def ===(other : Val[A]) : BoolOperand = {
  //   new BoolOperand(Eq(this, other))
  // } 

  //def !==(other : Val[A]) : BoolOperand = {
  //  new BoolOperand(Distinct(this, other))
  //} 

  // This is more general.
  def ===(other : Tree[BottomSort]) : BoolOperand = {
    new BoolOperand(Eq(this, other))
  }

  def !==(other : Tree[BottomSort]) : BoolOperand = {
    new BoolOperand(Distinct(this, other))
  } 

  // Unsafe as such. Better would be to have this in Tree itself, and restrict
  // it to trees of array sorts.
  def apply(t : Tree[_ >: BottomSort <: TopSort]) : Tree[BottomSort] = {
    new MapSelect(this, t)
  }
}

/** This class is used to bridge the gap between non-DSL Z3ASTs and DSL ASTs.
 * There are two important things to check: that the Z3Context is the correct
 * one (when the DSL tree actually gets converted), and that the sort is the
 * advertised one. This second check is currently not performed. It will need
 * to be a runtime check that can happen through an implicit "checker"
 * parameter. */
case class Z3ASTWrapper[+A >: BottomSort <: TopSort] private[z3](ast : Z3AST) extends Tree[A] {
  def build(z3 : Z3Context) : Z3AST = returnIfCompatible(z3)
  override def ast(z3 : Z3Context) : Z3AST = returnIfCompatible(z3)

  private def returnIfCompatible(z3 : Z3Context) : Z3AST = {
    if(z3.ptr != ast.context.ptr) {
      throw new Exception("Error: using incompatible context to convert DSL Tree.")
    } else {
      this.ast
    }
  }
}

sealed trait BinaryOp[+A >: BottomSort <: TopSort,B >: BottomSort <: TopSort] extends Tree[B] {
  val left : Tree[A]
  val right : Tree[A]
}
sealed trait BinaryPred[+A >: BottomSort <: TopSort] extends BinaryOp[A,BoolSort]

sealed trait NAryPred[+A >: BottomSort <: TopSort] extends Tree[BoolSort] {
  val args : Seq[Tree[A]]
}

case class BoolConstant(value : Boolean) extends Tree[BoolSort] {
  private[dsl] def build(z3 : Z3Context) = if(value) z3.mkTrue else z3.mkFalse
}

case class IntConstant(value : Int) extends Tree[IntSort] {
  private[dsl] def build(z3 : Z3Context) = z3.mkInt(value, z3.mkIntSort)
}

case class CharConstant(value : Char) extends Tree[BVSort] {
  private[dsl] def build(z3 : Z3Context) = z3.mkInt(value, z3.mkBVSort(16))
}

case class BoolVar() extends Tree[BoolSort] {
  private[dsl] def build(z3 : Z3Context) = z3.mkFreshConst("C", z3.mkBoolSort)
}

case class IntVar() extends Tree[IntSort] {
  private[dsl] def build(z3 : Z3Context) = z3.mkFreshConst("I", z3.mkIntSort)
}

case class CharVar() extends Tree[BVSort] {
  private[dsl] def build(z3 : Z3Context) = z3.mkFreshConst("BV", z3.mkBVSort(16))
}

case class IntSetVar() extends Tree[SetSort] {
  private[dsl] def build(z3 : Z3Context) = z3.mkFreshConst("IS", z3.mkSetSort(z3.mkIntSort))
}

case class Eq[+A >: BottomSort <: TopSort](left : Tree[A], right : Tree[A]) extends BinaryPred[A] {
  private[dsl] def build(z3 : Z3Context) = z3.mkEq(left.ast(z3), right.ast(z3))
}

case class Distinct[+A >: BottomSort <: TopSort](args : Tree[A]*) extends NAryPred[A] {
  private[dsl] def build(z3 : Z3Context) = z3.mkDistinct(args.map(_.ast(z3)) : _*)
}

case class And[+A >: BottomSort <: BoolSort](args : Tree[A]*) extends NAryPred[A] {
  private[dsl] def build(z3 : Z3Context) = z3.mkAnd(args.map(_.ast(z3)) : _*)
}

case class Or[+A >: BottomSort <: BoolSort](args : Tree[A]*) extends NAryPred[A] {
  private[dsl] def build(z3 : Z3Context) = z3.mkOr(args.map(_.ast(z3)) : _*)
}

case class Not[+A >: BottomSort <: BoolSort](tree : Tree[A]) extends Tree[BoolSort] {
  private[dsl] def build(z3 : Z3Context) = z3.mkNot(tree.ast(z3))
}

case class Iff[+A >: BottomSort <: BoolSort](left : Tree[A], right : Tree[A]) extends BinaryPred[BoolSort] {
  private[dsl] def build(z3 : Z3Context) = z3.mkIff(left.ast(z3), right.ast(z3))
}

case class Implies[+A >: BottomSort <: BoolSort](left : Tree[A], right : Tree[A]) extends BinaryPred[BoolSort] {
  private[dsl] def build(z3 : Z3Context) = z3.mkImplies(left.ast(z3), right.ast(z3))
}

case class Xor[+A >: BottomSort <: BoolSort](left : Tree[A], right : Tree[A]) extends BinaryPred[BoolSort] {
  private[dsl] def build(z3 : Z3Context) = z3.mkXor(left.ast(z3), right.ast(z3))
}

case class Add[+A >: BottomSort <: IntSort](args : Tree[A]*) extends Tree[IntSort] {
  private[dsl] def build(z3 : Z3Context) = z3.mkAdd(args.map(_.ast(z3)) : _*)
}

case class Mul[+A >: BottomSort <: IntSort](args : Tree[A]*) extends Tree[IntSort] {
  private[dsl] def build(z3 : Z3Context) = z3.mkMul(args.map(_.ast(z3)) : _*)
}

case class Sub[+A >: BottomSort <: IntSort](left : Tree[A], right : Tree[A]) extends BinaryOp[A,IntSort] {
  private[dsl] def build(z3 : Z3Context) = z3.mkSub(left.ast(z3), right.ast(z3))
}

case class Div[+A >: BottomSort <: IntSort](left : Tree[A], right : Tree[A]) extends BinaryOp[A,IntSort] {
  private[dsl] def build(z3 : Z3Context) = z3.mkDiv(left.ast(z3), right.ast(z3))
}

case class Mod[+A >: BottomSort <: IntSort](left : Tree[A], right : Tree[A]) extends BinaryOp[A,IntSort] {
  private[dsl] def build(z3 : Z3Context) = z3.mkMod(left.ast(z3), right.ast(z3))
}

case class Rem[+A >: BottomSort <: IntSort](left : Tree[A], right : Tree[A]) extends BinaryOp[A,IntSort] {
  private[dsl] def build(z3 : Z3Context) = z3.mkRem(left.ast(z3), right.ast(z3))
}

case class LT[+A >: BottomSort <: IntSort](left : Tree[A], right : Tree[A]) extends BinaryPred[IntSort] {
  private[dsl] def build(z3 : Z3Context) = z3.mkLT(left.ast(z3), right.ast(z3))
}

case class LE[+A >: BottomSort <: IntSort](left : Tree[A], right : Tree[A]) extends BinaryPred[IntSort] {
  private[dsl] def build(z3 : Z3Context) = z3.mkLE(left.ast(z3), right.ast(z3))
}

case class GT[+A >: BottomSort <: IntSort](left : Tree[A], right : Tree[A]) extends BinaryPred[IntSort] {
  private[dsl] def build(z3 : Z3Context) = z3.mkGT(left.ast(z3), right.ast(z3))
}

case class GE[+A >: BottomSort <: IntSort](left : Tree[A], right : Tree[A]) extends BinaryPred[IntSort] {
  private[dsl] def build(z3 : Z3Context) = z3.mkGE(left.ast(z3), right.ast(z3))
}

case class BVNot[+A >: BottomSort <: BVSort](tree : Tree[A]) extends Tree[BVSort] {
  private[dsl] def build(z3 : Z3Context) = z3.mkBVNot(tree.ast(z3))
}

case class BVRedAnd[+A >: BottomSort <: BVSort](tree : Tree[A]) extends Tree[BVSort] {
  private[dsl] def build(z3 : Z3Context) = z3.mkBVRedAnd(tree.ast(z3))
}

case class BVRedOr[+A >: BottomSort <: BVSort](tree : Tree[A]) extends Tree[BVSort] {
  private[dsl] def build(z3 : Z3Context) = z3.mkBVRedOr(tree.ast(z3))
}

case class BVAnd[+A >: BottomSort <: BVSort](left : Tree[A], right : Tree[A]) extends BinaryOp[A,BVSort] {
  private[dsl] def build(z3 : Z3Context) = z3.mkBVAnd(left.ast(z3), right.ast(z3))
}

case class BVOr[+A >: BottomSort <: BVSort](left : Tree[A], right : Tree[A]) extends BinaryOp[A,BVSort] {
  private[dsl] def build(z3 : Z3Context) = z3.mkBVOr(left.ast(z3), right.ast(z3))
}

case class BVXor[+A >: BottomSort <: BVSort](left : Tree[A], right : Tree[A]) extends BinaryOp[A,BVSort] {
  private[dsl] def build(z3 : Z3Context) = z3.mkBVXor(left.ast(z3), right.ast(z3))
}

case class BVNand[+A >: BottomSort <: BVSort](left : Tree[A], right : Tree[A]) extends BinaryOp[A,BVSort] {
  private[dsl] def build(z3 : Z3Context) = z3.mkBVNand(left.ast(z3), right.ast(z3))
}

case class BVNor[+A >: BottomSort <: BVSort](left : Tree[A], right : Tree[A]) extends BinaryOp[A,BVSort] {
  private[dsl] def build(z3 : Z3Context) = z3.mkBVNor(left.ast(z3), right.ast(z3))
}

case class BVXnor[+A >: BottomSort <: BVSort](left : Tree[A], right : Tree[A]) extends BinaryOp[A,BVSort] {
  private[dsl] def build(z3 : Z3Context) = z3.mkBVXnor(left.ast(z3), right.ast(z3))
}

case class BVNeg[+A >: BottomSort <: BVSort](tree : Tree[A]) extends Tree[BVSort] {
  private[dsl] def build(z3 : Z3Context) = z3.mkBVNeg(tree.ast(z3))
}

case class BVAdd[+A >: BottomSort <: BVSort](left : Tree[A], right : Tree[A]) extends BinaryOp[A,BVSort] {
  private[dsl] def build(z3 : Z3Context) = z3.mkBVAdd(left.ast(z3), right.ast(z3))
}

case class BVSub[+A >: BottomSort <: BVSort](left : Tree[A], right : Tree[A]) extends BinaryOp[A,BVSort] {
  private[dsl] def build(z3 : Z3Context) = z3.mkBVSub(left.ast(z3), right.ast(z3))
}

case class BVMul[+A >: BottomSort <: BVSort](left : Tree[A], right : Tree[A]) extends BinaryOp[A,BVSort] {
  private[dsl] def build(z3 : Z3Context) = z3.mkBVMul(left.ast(z3), right.ast(z3))
}

case class BVUdiv[+A >: BottomSort <: BVSort](left : Tree[A], right : Tree[A]) extends BinaryOp[A,BVSort] {
  private[dsl] def build(z3 : Z3Context) = z3.mkBVUdiv(left.ast(z3), right.ast(z3))
}

case class BVSdiv[+A >: BottomSort <: BVSort](left : Tree[A], right : Tree[A]) extends BinaryOp[A,BVSort] {
  private[dsl] def build(z3 : Z3Context) = z3.mkBVSdiv(left.ast(z3), right.ast(z3))
}

case class BVUrem[+A >: BottomSort <: BVSort](left : Tree[A], right : Tree[A]) extends BinaryOp[A,BVSort] {
  private[dsl] def build(z3 : Z3Context) = z3.mkBVUrem(left.ast(z3), right.ast(z3))
}

case class BVSrem[+A >: BottomSort <: BVSort](left : Tree[A], right : Tree[A]) extends BinaryOp[A,BVSort] {
  private[dsl] def build(z3 : Z3Context) = z3.mkBVSrem(left.ast(z3), right.ast(z3))
}

case class BVSmod[+A >: BottomSort <: BVSort](left : Tree[A], right : Tree[A]) extends BinaryOp[A,BVSort] {
  private[dsl] def build(z3 : Z3Context) = z3.mkBVSmod(left.ast(z3), right.ast(z3))
}

case class BVUlt[+A >: BottomSort <: BVSort](left : Tree[A], right : Tree[A]) extends BinaryPred[BVSort] {
  private[dsl] def build(z3 : Z3Context) = z3.mkBVUlt(left.ast(z3), right.ast(z3))
}

case class BVSlt[+A >: BottomSort <: BVSort](left : Tree[A], right : Tree[A]) extends BinaryPred[BVSort] {
  private[dsl] def build(z3 : Z3Context) = z3.mkBVSlt(left.ast(z3), right.ast(z3))
}

case class BVUle[+A >: BottomSort <: BVSort](left : Tree[A], right : Tree[A]) extends BinaryPred[BVSort] {
  private[dsl] def build(z3 : Z3Context) = z3.mkBVUle(left.ast(z3), right.ast(z3))
}

case class BVSle[+A >: BottomSort <: BVSort](left : Tree[A], right : Tree[A]) extends BinaryPred[BVSort] {
  private[dsl] def build(z3 : Z3Context) = z3.mkBVSle(left.ast(z3), right.ast(z3))
}

case class BVUgt[+A >: BottomSort <: BVSort](left : Tree[A], right : Tree[A]) extends BinaryPred[BVSort] {
  private[dsl] def build(z3 : Z3Context) = z3.mkBVUgt(left.ast(z3), right.ast(z3))
}

case class BVSgt[+A >: BottomSort <: BVSort](left : Tree[A], right : Tree[A]) extends BinaryPred[BVSort] {
  private[dsl] def build(z3 : Z3Context) = z3.mkBVSgt(left.ast(z3), right.ast(z3))
}

case class BVUge[+A >: BottomSort <: BVSort](left : Tree[A], right : Tree[A]) extends BinaryPred[BVSort] {
  private[dsl] def build(z3 : Z3Context) = z3.mkBVUge(left.ast(z3), right.ast(z3))
}

case class BVSge[+A >: BottomSort <: BVSort](left : Tree[A], right : Tree[A]) extends BinaryPred[BVSort] {
  private[dsl] def build(z3 : Z3Context) = z3.mkBVSge(left.ast(z3), right.ast(z3))
}

case class Concat[+A >: BottomSort <: BVSort](left : Tree[A], right : Tree[A]) extends BinaryOp[A,BVSort] {
  private[dsl] def build(z3 : Z3Context) = z3.mkConcat(left.ast(z3), right.ast(z3))
}

case class Extract[+A >: BottomSort <: BVSort](high: Int, low: Int, tree : Tree[A]) extends Tree[BVSort] {
  private[dsl] def build(z3 : Z3Context) = z3.mkExtract(high, low, tree.ast(z3))
}

case class SignExt[+A >: BottomSort <: BVSort](extraSize: Int, tree : Tree[A]) extends Tree[BVSort] {
  private[dsl] def build(z3 : Z3Context) = z3.mkSignExt(extraSize, tree.ast(z3))
}

case class ZeroExt[+A >: BottomSort <: BVSort](extraSize: Int, tree : Tree[A]) extends Tree[BVSort] {
  private[dsl] def build(z3 : Z3Context) = z3.mkZeroExt(extraSize, tree.ast(z3))
}

case class Repeat[+A >: BottomSort <: BVSort](count: Int, tree : Tree[A]) extends Tree[BVSort] {
  private[dsl] def build(z3 : Z3Context) = z3.mkRepeat(count, tree.ast(z3))
}

case class BVShl[+A >: BottomSort <: BVSort](left : Tree[A], right : Tree[A]) extends BinaryOp[A,BVSort] {
  private[dsl] def build(z3 : Z3Context) = z3.mkBVShl(left.ast(z3), right.ast(z3))
}

case class BVLshr[+A >: BottomSort <: BVSort](left : Tree[A], right : Tree[A]) extends BinaryOp[A,BVSort] {
  private[dsl] def build(z3 : Z3Context) = z3.mkBVLshr(left.ast(z3), right.ast(z3))
}

case class BVAshr[+A >: BottomSort <: BVSort](left : Tree[A], right : Tree[A]) extends BinaryOp[A,BVSort] {
  private[dsl] def build(z3 : Z3Context) = z3.mkBVAshr(left.ast(z3), right.ast(z3))
}

case class ExtRotateLeft[+A >: BottomSort <: BVSort](left : Tree[A], right : Tree[A]) extends BinaryOp[A,BVSort] {
  private[dsl] def build(z3 : Z3Context) = z3.mkExtRotateLeft(left.ast(z3), right.ast(z3))
}

case class ExtRotateRight[+A >: BottomSort <: BVSort](left : Tree[A], right : Tree[A]) extends BinaryOp[A,BVSort] {
  private[dsl] def build(z3 : Z3Context) = z3.mkExtRotateRight(left.ast(z3), right.ast(z3))
}

case class BVAddNoOverflow[+A >: BottomSort <: BVSort](left : Tree[A], right : Tree[A], isSigned: Boolean) extends BinaryOp[A,BVSort] {
  private[dsl] def build(z3 : Z3Context) = z3.mkBVAddNoOverflow(left.ast(z3), right.ast(z3), isSigned)
}

case class BVAddNoUnderflow[+A >: BottomSort <: BVSort](left : Tree[A], right : Tree[A]) extends BinaryOp[A,BVSort] {
  private[dsl] def build(z3 : Z3Context) = z3.mkBVAddNoUnderflow(left.ast(z3), right.ast(z3))
}

case class BVSubNoOverflow[+A >: BottomSort <: BVSort](left : Tree[A], right : Tree[A]) extends BinaryOp[A,BVSort] {
  private[dsl] def build(z3 : Z3Context) = z3.mkBVSubNoOverflow(left.ast(z3), right.ast(z3))
}

case class BVSubNoUnderflow[+A >: BottomSort <: BVSort](left : Tree[A], right : Tree[A], isSigned: Boolean) extends BinaryOp[A,BVSort] {
  private[dsl] def build(z3 : Z3Context) = z3.mkBVSubNoUnderflow(left.ast(z3), right.ast(z3), isSigned)
}

case class BVSDivNoOverflow[+A >: BottomSort <: BVSort](left : Tree[A], right : Tree[A]) extends BinaryOp[A,BVSort] {
  private[dsl] def build(z3 : Z3Context) = z3.mkBVSDivNoOverflow(left.ast(z3), right.ast(z3))
}

case class BVNegNoOverflow[+A >: BottomSort <: BVSort](tree : Tree[A]) extends Tree[BVSort] {
  private[dsl] def build(z3 : Z3Context) = z3.mkBVNegNoOverflow(tree.ast(z3))
}

case class BVMulNoOverflow[+A >: BottomSort <: BVSort](left : Tree[A], right : Tree[A], isSigned: Boolean) extends BinaryOp[A,BVSort] {
  private[dsl] def build(z3 : Z3Context) = z3.mkBVMulNoOverflow(left.ast(z3), right.ast(z3), isSigned)
}

case class BVMulNoUnderflow[+A >: BottomSort <: BVSort](left : Tree[A], right : Tree[A]) extends BinaryOp[A,BVSort] {
  private[dsl] def build(z3 : Z3Context) = z3.mkBVMulNoUnderflow(left.ast(z3), right.ast(z3))
}

case class SetUnion[+A >: BottomSort <: SetSort](args: Tree[A]*) extends Tree[SetSort] {
  private[dsl] def build(z3 : Z3Context) = z3.mkSetUnion(args.map(_.ast(z3)) : _*)
}

case class SetIntersect[+A >: BottomSort <: SetSort](args: Tree[A]*) extends Tree[SetSort] {
  private[dsl] def build(z3 : Z3Context) = z3.mkSetIntersect(args.map(_.ast(z3)) : _*)
}

case class SetDifference[+A >: BottomSort <: SetSort](left : Tree[A], right : Tree[A]) extends Tree[SetSort] {
  private[dsl] def build(z3 : Z3Context) = z3.mkSetDifference(left.ast(z3), right.ast(z3))
}

case class SetSubset[+A >: BottomSort <: SetSort](left : Tree[A], right : Tree[A]) extends BinaryPred[SetSort] {
  private[dsl] def build(z3 : Z3Context) = z3.mkSetSubset(left.ast(z3), right.ast(z3))
}

case class EmptyIntSet() extends Tree[SetSort] {
  private [dsl] def build(z3 : Z3Context) = z3.mkEmptySet(z3.mkIntSort)
}

case class SetAdd[+A >: BottomSort <: TopSort](set : Tree[SetSort], elem : Tree[A]) extends Tree[SetSort] {
  private [dsl] def build(z3 : Z3Context) = z3.mkSetAdd(set.ast(z3), elem.ast(z3))
}

// Unsafe.
case class MapSelect(map : Tree[_ >: BottomSort <: TopSort], index : Tree[_ >: BottomSort <: TopSort]) extends Tree[BottomSort] {
  private [dsl] def build(z3 : Z3Context) = z3.mkSelect(map.ast(z3), index.ast(z3))
}
