package z3.scala

package object dsl {
  import Operands._

  implicit def z3ASTToBoolOperand(ast : Z3AST) : BoolOperand[BoolSort] = new BoolOperand[BoolSort](Z3ASTWrapper[BoolSort](ast))

  implicit def booleanValueToBoolOperand(value : Boolean) : BoolOperand[BoolSort] = new BoolOperand[BoolSort](BoolConstant(value))

  implicit def boolTreeToBoolOperand[T >: BottomSort <: BoolSort](tree : Tree[T]) : BoolOperand[BoolSort] = 
    new BoolOperand[BoolSort](tree)

  implicit def boolOperandToBoolTree(operand : BoolOperand[_]) : Tree[BoolSort] = operand.tree.asInstanceOf[Tree[BoolSort]]

  implicit def z3ASTToIntOperand(ast : Z3AST) : IntOperand[IntSort] = new IntOperand[IntSort](Z3ASTWrapper[IntSort](ast))

  implicit def intValueToIntOperand(value : Int) : IntOperand[IntSort] = new IntOperand[IntSort](IntConstant(value))

  implicit def intTreeToIntOperand[T >: BottomSort <: IntSort](tree : Tree[T]) : IntOperand[IntSort] = 
    new IntOperand[IntSort](tree)

  implicit def intOperandToIntTree(operand : IntOperand[_]) : Tree[IntSort] = operand.tree.asInstanceOf[Tree[IntSort]]


  // The following is for the choose magic.

  sealed abstract class ValHandler[A] {
    def construct : Val[A]
    def convert(model : Z3Model, ast : Z3AST) : A
  }

  implicit object IntValHandler extends ValHandler[Int] {
    def construct : Val[Int] = new Val[Int] {
      def build(z3 : Z3Context) = z3.mkFreshConst("I", z3.mkIntSort)
    }

    def convert(model : Z3Model, ast : Z3AST) : Int =
      model.evalAs[Int](ast).getOrElse(0)
  }

  def find[T](predicate : Val[T] => Tree[BoolSort])(implicit vh : ValHandler[T]) : Option[T] = {
    val z3 = new Z3Context("MODEL" -> true)
    val valTree = vh.construct
    val valAST = valTree.ast(z3)
    val constraintTree = predicate(valTree)
    z3.assertCnstr(constraintTree.ast(z3))
    z3.checkAndGetModel match {
      case (Some(true), m) => {
        val result = vh.convert(m, valAST)
        m.delete
        z3.delete
        Some(result)
      }
      case (_, m) => {
        m.delete
        z3.delete
        None
      }
    }
  }
}
