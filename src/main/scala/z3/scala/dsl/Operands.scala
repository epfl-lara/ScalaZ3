package z3.scala.dsl

/** We call Operands what is otherwise known as "Rich" classes (from the "Pimp
 * My Library" paradigm). For simplicity and to avoid ambiguous resolutions,
 * operations on operands always return operands, never trees. Conversion from
 * and to trees are done by implicit functions in the dsl package object. */
object Operands {
  class BoolOperand[T >: BottomSort <: BoolSort](val tree : Tree[T]) {
    def &&[T2 >: BottomSort <: BoolSort](other : BoolOperand[T2]) : BoolOperand[BoolSort] = {
      new BoolOperand[BoolSort](And(tree, other.tree))
    }

    def ||[T2 >: BottomSort <: BoolSort](other : BoolOperand[T2]) : BoolOperand[BoolSort] = {
      new BoolOperand[BoolSort](Or(tree, other.tree))
    }
    
    def unary_! : BoolOperand[BoolSort] = {
      new BoolOperand[BoolSort](Not(tree))
    }

    def <-->[T2 >: BottomSort <: BoolSort](other : BoolOperand[T2]) : BoolOperand[BoolSort] = {
      new BoolOperand[BoolSort](Iff(tree, other.tree))
    }

    def ===[T2 >: BottomSort <: BoolSort](other : BoolOperand[T2]) = <-->(other)

    def -->[T2 >: BottomSort <: BoolSort](other : BoolOperand[T2]) : BoolOperand[BoolSort] = {
      new BoolOperand[BoolSort](Implies(tree, other.tree))
    }

    def ^^[T2 >: BottomSort <: BoolSort](other : BoolOperand[T2]) : BoolOperand[BoolSort] = {
      new BoolOperand[BoolSort](Xor(tree, other.tree))
    }

    def !==[T2 >: BottomSort <: BoolSort](other : BoolOperand[T2]) = ^^(other)
  }

  class IntOperand[T >: BottomSort <: IntSort](val tree : Tree[T]) {
    def +[T2 >: BottomSort <: IntSort](other : IntOperand[T2]) : IntOperand[IntSort] = {
      new IntOperand[IntSort](Add(tree, other.tree))
    }

    def *[T2 >: BottomSort <: IntSort](other : IntOperand[T2]) : IntOperand[IntSort] = {
      new IntOperand[IntSort](Mul(tree, other.tree))
    }

    def -[T2 >: BottomSort <: IntSort](other : IntOperand[T2]) : IntOperand[IntSort] = {
      new IntOperand[IntSort](Sub(tree, other.tree))
    }

    def /[T2 >: BottomSort <: IntSort](other : IntOperand[T2]) : IntOperand[IntSort] = {
      new IntOperand[IntSort](Div(tree, other.tree))
    }

    def %[T2 >: BottomSort <: IntSort](other : IntOperand[T2]) : IntOperand[IntSort] = {
      new IntOperand[IntSort](Mod(tree, other.tree))
    }

    def rem[T2 >: BottomSort <: IntSort](other : IntOperand[T2]) : IntOperand[IntSort] = {
      new IntOperand[IntSort](Rem(tree, other.tree))
    }

    def ===[T2 >: BottomSort <: IntSort](other : IntOperand[T2]) : BoolOperand[BoolSort] = {
      new BoolOperand[BoolSort](Eq(tree, other.tree))
    }

    def !==[T2 >: BottomSort <: IntSort](other : IntOperand[T2]) : BoolOperand[BoolSort] = {
      new BoolOperand[BoolSort](Distinct(tree, other.tree))
    }

    def <[T2 >: BottomSort <: IntSort](other : IntOperand[T2]) : BoolOperand[BoolSort] = {
      new BoolOperand[BoolSort](LT(tree, other.tree))
    }

    def <=[T2 >: BottomSort <: IntSort](other : IntOperand[T2]) : BoolOperand[BoolSort] = {
      new BoolOperand[BoolSort](LE(tree, other.tree))
    }

    def >[T2 >: BottomSort <: IntSort](other : IntOperand[T2]) : BoolOperand[BoolSort] = {
      new BoolOperand[BoolSort](GT(tree, other.tree))
    }

    def >=[T2 >: BottomSort <: IntSort](other : IntOperand[T2]) : BoolOperand[BoolSort] = {
      new BoolOperand[BoolSort](GE(tree, other.tree))
    }
  }
}
