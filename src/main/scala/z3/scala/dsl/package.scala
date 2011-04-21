package z3.scala

package object dsl {
  import Operands._

  class UnsatisfiableConstraintException extends Exception
  class SortMismatchException(msg : String) extends Exception("Sort mismatch: " + msg)

  implicit def z3ASTToBoolOperand(ast : Z3AST) : BoolOperand = {
    if(!ast.getSort.isBoolSort) {
      throw new SortMismatchException("expected boolean operand, got: " + ast)
    }
    new BoolOperand(Z3ASTWrapper[BoolSort](ast))
  }

  implicit def booleanValueToBoolOperand(value : Boolean) : BoolOperand = new BoolOperand(BoolConstant(value))

  implicit def boolTreeToBoolOperand[T >: BottomSort <: BoolSort](tree : Tree[T]) : BoolOperand =
    new BoolOperand(tree)

  implicit def boolOperandToBoolTree(operand : BoolOperand) : Tree[BoolSort] = operand.tree.asInstanceOf[Tree[BoolSort]]

  implicit def z3ASTToIntOperand(ast : Z3AST) : IntOperand = {
    if(!ast.getSort.isIntSort) {
      throw new SortMismatchException("expected integer operand, got: " + ast)
    }
    new IntOperand(Z3ASTWrapper[IntSort](ast))
  }

  implicit def intValueToIntOperand(value : Int) : IntOperand = new IntOperand(IntConstant(value))

  implicit def intTreeToIntOperand[T >: BottomSort <: IntSort](tree : Tree[T]) : IntOperand =
    new IntOperand(tree)

  implicit def intOperandToIntTree(operand : IntOperand) : Tree[IntSort] = operand.tree.asInstanceOf[Tree[IntSort]]

  // TODO add intset methods...

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

  // TODO add intset val handler

  def choose[T](predicate : Val[T] => Tree[BoolSort])(implicit vh : ValHandler[T]) : T = find(predicate)(vh) match {
    case Some(result) => result
    case None => throw new UnsatisfiableConstraintException
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

  def findAll[T](predicate : Val[T] => Tree[BoolSort])(implicit vh : ValHandler[T]) : Iterator[T] = {
    val z3 = new Z3Context("MODEL" -> true)
    val valTree = vh.construct
    val valAST = valTree.ast(z3)
    val constraintTree = predicate(valTree)

    z3.assertCnstr(constraintTree.ast(z3))
    z3.checkAndGetAllModels.map(m => {
      val result = vh.convert(m, valAST)
      result
    })
  }

  def choose[T1,T2](predicate : (Val[T1],Val[T2]) => Tree[BoolSort])(implicit vh1 : ValHandler[T1], vh2 : ValHandler[T2]) : (T1,T2) = find(predicate)(vh1, vh2) match {
    case Some(p) => p
    case None => throw new UnsatisfiableConstraintException
  }

  def find[T1,T2](predicate : (Val[T1],Val[T2]) => Tree[BoolSort])(implicit vh1 : ValHandler[T1], vh2 : ValHandler[T2]) : Option[(T1,T2)] = {
    val z3 = new Z3Context("MODEL" -> true)
    val valTree1 = vh1.construct
    val valTree2 = vh2.construct
    val valAST1 = valTree1.ast(z3)
    val valAST2 = valTree2.ast(z3)
    val constraintTree = predicate(valTree1,valTree2)
    z3.assertCnstr(constraintTree.ast(z3))
    z3.checkAndGetModel match {
      case (Some(true), m) => {
        val result1 = vh1.convert(m, valAST1)
        val result2 = vh2.convert(m, valAST2)
        m.delete
        z3.delete
        Some((result1,result2))
      }
      case (_, m) => {
        m.delete
        z3.delete
        None
      }
    }
  }

  def findAll[T1,T2](predicate : (Val[T1],Val[T2]) => Tree[BoolSort])(implicit vh1 : ValHandler[T1], vh2 : ValHandler[T2]) : Iterator[(T1,T2)] = {
    val z3 = new Z3Context("MODEL" -> true)
    val valTree1 = vh1.construct
    val valTree2 = vh2.construct
    val valAST1 = valTree1.ast(z3)
    val valAST2 = valTree2.ast(z3)
    val constraintTree = predicate(valTree1, valTree2)

    z3.assertCnstr(constraintTree.ast(z3))
    z3.checkAndGetAllModels.map(m => {
      val result1 = vh1.convert(m, valAST1)
      val result2 = vh2.convert(m, valAST2)
      (result1,result2)
    })
  }
}
