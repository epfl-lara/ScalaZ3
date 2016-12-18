package z3.scala.dsl

/** We call Operands what is otherwise known as "Rich" classes (from the "Pimp
 * My Library" paradigm). For simplicity and to avoid ambiguous resolutions,
 * operations on operands always return operands, never trees. Conversion from
 * and to trees are done by implicit functions in the dsl package object. */
object Operands {
  class BoolOperand private[dsl](val tree : Tree[_ >: BottomSort <: BoolSort]) {
    def &&[T >: BottomSort <: BoolSort](other : BoolOperand) : BoolOperand = {
      new BoolOperand(And(tree, other.tree))
    }

    def ||(other : BoolOperand) : BoolOperand = {
      new BoolOperand(Or(tree, other.tree))
    }
    
    def unary_! : BoolOperand= {
      new BoolOperand(Not(tree))
    }

    def <-->(other : BoolOperand) : BoolOperand = {
      new BoolOperand(Iff(tree, other.tree))
    }

    def ===(other : BoolOperand) = <-->(other)

    def -->(other : BoolOperand) : BoolOperand = {
      new BoolOperand(Implies(tree, other.tree))
    }

    def ^^(other : BoolOperand) : BoolOperand = {
      new BoolOperand(Xor(tree, other.tree))
    }

    def !==(other : BoolOperand) = ^^(other)
  }

  class IntOperand private[dsl](val tree : Tree[_ >: BottomSort <: IntSort]) {
    def +(other : IntOperand) : IntOperand= {
      new IntOperand(Add(tree, other.tree))
    }

    def *(other : IntOperand) : IntOperand = {
      new IntOperand(Mul(tree, other.tree))
    }

    def -(other : IntOperand) : IntOperand = {
      new IntOperand(Sub(tree, other.tree))
    }

    def /(other : IntOperand) : IntOperand = {
      new IntOperand(Div(tree, other.tree))
    }

    def %(other : IntOperand) : IntOperand = {
      new IntOperand(Mod(tree, other.tree))
    }

    def rem(other : IntOperand) : IntOperand = {
      new IntOperand(Rem(tree, other.tree))
    }

    def ===(other : IntOperand) : BoolOperand = {
      new BoolOperand(Eq(tree, other.tree))
    }

    def !==(other : IntOperand) : BoolOperand = {
      new BoolOperand(Distinct(tree, other.tree))
    }

    def <(other : IntOperand) : BoolOperand = {
      new BoolOperand(LT(tree, other.tree))
    }

    def <=(other : IntOperand) : BoolOperand = {
      new BoolOperand(LE(tree, other.tree))
    }

    def >(other : IntOperand) : BoolOperand = {
      new BoolOperand(GT(tree, other.tree))
    }

    def >=(other : IntOperand) : BoolOperand = {
      new BoolOperand(GE(tree, other.tree))
    }
  }

  class BitVectorOperand private[dsl](val tree : Tree[_ >: BottomSort <: BVSort]) {
    def unary_~ : BitVectorOperand = {
      new BitVectorOperand(BVNot(tree))
    }

    def reducedAnd : BitVectorOperand = {
      new BitVectorOperand(BVRedAnd(tree))
    }

    def reducedOr : BitVectorOperand = {
      new BitVectorOperand(BVRedOr(tree))
    }

    def |(other : BitVectorOperand) : BitVectorOperand = {
      new BitVectorOperand(BVOr(tree, other.tree))
    }

    def &(other : BitVectorOperand) : BitVectorOperand = {
      new BitVectorOperand(BVAnd(tree, other.tree))
    }

    def ^(other : BitVectorOperand) : BitVectorOperand = {
      new BitVectorOperand(BVXor(tree, other.tree))
    }

    def nand(other : BitVectorOperand) : BitVectorOperand = {
      new BitVectorOperand(BVNand(tree, other.tree))
    }

    def nor(other : BitVectorOperand) : BitVectorOperand = {
      new BitVectorOperand(BVNor(tree, other.tree))
    }

    def xnor(other : BitVectorOperand) : BitVectorOperand = {
      new BitVectorOperand(BVXnor(tree, other.tree))
    }

    def unary_- : BitVectorOperand = {
      new BitVectorOperand(BVNeg(tree))
    }

    def *(other : BitVectorOperand) : BitVectorOperand = {
      new BitVectorOperand(BVMul(tree, other.tree))
    }

    def -(other : BitVectorOperand) : BitVectorOperand = {
      new BitVectorOperand(BVSub(tree, other.tree))
    }

    def ~/(other : BitVectorOperand) : BitVectorOperand = {
      new BitVectorOperand(BVUdiv(tree, other.tree))
    }

    def /(other : BitVectorOperand) : BitVectorOperand = {
      new BitVectorOperand(BVSdiv(tree, other.tree))
    }

    def %(other : BitVectorOperand) : BitVectorOperand = {
      new BitVectorOperand(BVSmod(tree, other.tree))
    }

    def urem(other : BitVectorOperand) : BitVectorOperand = {
      new BitVectorOperand(BVUrem(tree, other.tree))
    }

    def srem(other : BitVectorOperand) : BitVectorOperand = {
      new BitVectorOperand(BVSrem(tree, other.tree))
    }

    def ===(other : BitVectorOperand) : BoolOperand = {
      new BoolOperand(Eq(tree, other.tree))
    }

    def !==(other : BitVectorOperand) : BoolOperand = {
      new BoolOperand(Distinct(tree, other.tree))
    }

    def ~<(other : BitVectorOperand) : BoolOperand = {
      new BoolOperand(BVUlt(tree, other.tree))
    }

    def ~<=(other : BitVectorOperand) : BoolOperand = {
      new BoolOperand(BVUle(tree, other.tree))
    }

    def ~>(other : BitVectorOperand) : BoolOperand = {
      new BoolOperand(BVUgt(tree, other.tree))
    }

    def ~>=(other : BitVectorOperand) : BoolOperand = {
      new BoolOperand(BVUge(tree, other.tree))
    }

    def <(other : BitVectorOperand) : BoolOperand = {
      new BoolOperand(BVSlt(tree, other.tree))
    }

    def <=(other : BitVectorOperand) : BoolOperand = {
      new BoolOperand(BVSle(tree, other.tree))
    }

    def >(other : BitVectorOperand) : BoolOperand = {
      new BoolOperand(BVSgt(tree, other.tree))
    }

    def >=(other : BitVectorOperand) : BoolOperand = {
      new BoolOperand(BVSge(tree, other.tree))
    }

    def concat(other : BitVectorOperand) : BitVectorOperand = {
      new BitVectorOperand(Concat(tree, other.tree))
    }

    def extract(high: Int, low: Int) : BitVectorOperand = {
      new BitVectorOperand(Extract(high, low, tree))
    }

    def signExt(extraSize: Int) : BitVectorOperand = {
      new BitVectorOperand(SignExt(extraSize, tree))
    }

    def zeroExt(extraSize: Int) : BitVectorOperand = {
      new BitVectorOperand(ZeroExt(extraSize, tree))
    }

    def repeat(count: Int) : BitVectorOperand = {
      new BitVectorOperand(Repeat(count, tree))
    }

    def <<(other : BitVectorOperand) : BitVectorOperand = {
      new BitVectorOperand(BVShl(tree, other.tree))
    }

    def >>(other : BitVectorOperand) : BitVectorOperand = {
      new BitVectorOperand(BVAshr(tree, other.tree))
    }

    def >>>(other : BitVectorOperand) : BitVectorOperand = {
      new BitVectorOperand(BVLshr(tree, other.tree))
    }

    def rotateLeft(other : BitVectorOperand) : BitVectorOperand = {
      new BitVectorOperand(ExtRotateLeft(tree, other.tree))
    }

    def rotateRight(other : BitVectorOperand) : BitVectorOperand = {
      new BitVectorOperand(ExtRotateRight(tree, other.tree))
    }

    def addNoOverflow(other : BitVectorOperand, isSigned: Boolean) : BitVectorOperand = {
      new BitVectorOperand(BVAddNoOverflow(tree, other.tree, isSigned))
    }

    def addNoUnderflow(other : BitVectorOperand) : BitVectorOperand = {
      new BitVectorOperand(BVAddNoUnderflow(tree, other.tree))
    }

    def subNoOverflow(other : BitVectorOperand) : BitVectorOperand = {
      new BitVectorOperand(BVSubNoOverflow(tree, other.tree))
    }

    def subNoUnderflow(other : BitVectorOperand, isSigned: Boolean) : BitVectorOperand = {
      new BitVectorOperand(BVSubNoUnderflow(tree, other.tree, isSigned))
    }

    def sdivNoOverflow(other : BitVectorOperand) : BitVectorOperand = {
      new BitVectorOperand(BVSDivNoOverflow(tree, other.tree))
    }

    def negNoOverflow : BitVectorOperand= {
      new BitVectorOperand(BVNegNoOverflow(tree))
    }

    def mulNoOverflow(other : BitVectorOperand, isSigned: Boolean) : BitVectorOperand = {
      new BitVectorOperand(BVMulNoOverflow(tree, other.tree, isSigned))
    }

    def mulNoUnderflow(other : BitVectorOperand) : BitVectorOperand = {
      new BitVectorOperand(BVMulNoUnderflow(tree, other.tree))
    }
  }

  class SetOperand private[dsl](val tree : Tree[_ >: BottomSort <: SetSort]) {
    def ++(other : SetOperand) : SetOperand = {
      new SetOperand(SetUnion(tree, other.tree))
    }

    def **(other : SetOperand) : SetOperand = {
      new SetOperand(SetIntersect(tree, other.tree))
    }

    def --(other : SetOperand) : SetOperand = {
      new SetOperand(SetDifference(tree, other.tree))
    }

    def ===(other : SetOperand) : BoolOperand = {
      new BoolOperand(Eq(tree, other.tree))
    }

    def !==(other : SetOperand) : BoolOperand = {
      new BoolOperand(Distinct(tree, other.tree))
    }

    def subsetOf(other : SetOperand) : BoolOperand = {
      new BoolOperand(SetSubset(tree, other.tree))
    }
  }
}
