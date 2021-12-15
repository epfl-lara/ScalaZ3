package z3.scala

import scala.language.implicitConversions

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

  implicit def booleanValueToBoolTree(value : Boolean) : Tree[BoolSort] = BoolConstant(value)

  implicit def booleanValueToBoolOperand(value : Boolean) : BoolOperand = new BoolOperand(BoolConstant(value))

  implicit def booleanValToBoolTree(value : Val[Boolean]) : Tree[BoolSort] = value.tree.asInstanceOf[Tree[BoolSort]]

  implicit def booleanValToBoolOperand(value : Val[Boolean]) : BoolOperand =
    new BoolOperand(value.tree.asInstanceOf[Tree[BoolSort]])

  implicit def boolTreeToBoolOperand[T >: BottomSort <: BoolSort](tree : Tree[T]) : BoolOperand =
    new BoolOperand(tree)

  implicit def boolOperandToBoolTree(operand : BoolOperand) : Tree[BoolSort] = operand.tree.asInstanceOf[Tree[BoolSort]]

  implicit def z3ASTToIntOperand(ast : Z3AST) : IntOperand = {
    if(!ast.getSort.isIntSort) {
      throw new SortMismatchException("expected integer operand, got: " + ast)
    }
    new IntOperand(Z3ASTWrapper[IntSort](ast))
  }

  implicit def intValueToIntTree(value : Int) : Tree[IntSort] = IntConstant(value)

  implicit def intValueToIntOperand(value : Int) : IntOperand = new IntOperand(IntConstant(value))

  implicit def intValToIntTree(value : Val[Int]) : Tree[IntSort] = value.tree.asInstanceOf[Tree[IntSort]]

  implicit def intValToIntOperand(value : Val[Int]) : IntOperand =
    new IntOperand(value.tree.asInstanceOf[Tree[IntSort]])

  implicit def intTreeToIntOperand[T >: BottomSort <: IntSort](tree : Tree[T]) : IntOperand =
    new IntOperand(tree)

  implicit def intOperandToIntTree(operand : IntOperand) : Tree[IntSort] = operand.tree.asInstanceOf[Tree[IntSort]]

  implicit def charValueToBVTree(value : Char) : Tree[BVSort] = CharConstant(value)

  implicit def charValueToBVOperand(value : Char) : BitVectorOperand = new BitVectorOperand(CharConstant(value))

  implicit def charValToCharTree(value : Val[Char]) : Tree[BVSort] = value.tree.asInstanceOf[Tree[BVSort]]

  implicit def charValToBVOperand(value : Val[Char]) : BitVectorOperand =
    new BitVectorOperand(value.tree.asInstanceOf[Tree[BVSort]])

  implicit def bvTreeToBVOperand[T >: BottomSort <: BVSort](tree : Tree[T]) : BitVectorOperand =
    new BitVectorOperand(tree)

  implicit def bvOperandToBVTree(operand : BitVectorOperand) : Tree[BVSort] = operand.tree.asInstanceOf[Tree[BVSort]]

  implicit def z3ASTToSetOperand(ast : Z3AST) : SetOperand = {
    // TODO how do we check the type (set of any type?) here?
    new SetOperand(Z3ASTWrapper[SetSort](ast))
  }

  implicit def intSetValueToSetTree(value : Set[Int]) : Tree[SetSort] = {
    value.foldLeft[Tree[SetSort]](EmptyIntSet())((set, elem) => SetAdd(set, IntConstant(elem)))
  }

  implicit def intSetValueToSetOperand(value : Set[Int]) : SetOperand = {
    new SetOperand(intSetValueToSetTree(value))
  }

  implicit def setTreeToSetOperand[T >: BottomSort <: SetSort](tree : Tree[T]) : SetOperand =
    new SetOperand(tree)

  implicit def setOperandToSetTree(operand : SetOperand) : Tree[SetSort] = operand.tree.asInstanceOf[Tree[SetSort]]

  // All default values

  implicit object DefaultInt extends Default[Int] {
    val value = 0
  }

  implicit object DefaultBoolean extends Default[Boolean] {
    val value = true
  }

  implicit object DefaultChar extends Default[Char] {
    val value = '\u0000'
  }

  implicit def liftDefaultToSet[A : Default] : Default[Set[A]] = {
    new Default[Set[A]] {
      val value = Set.empty[A]
    }
  }

  implicit def liftDefaultToFun[A,B : Default] : Default[A=>B] = {
    new Default[A=>B] {
      val value = ((a : A) => implicitly[Default[B]].value)
    }
  }

  // Predefined ValHandler's

  implicit object BooleanValHandler extends ValHandler[Boolean] {
    def mkSort(z3 : Z3Context) : Z3Sort = z3.mkBoolSort()

    def convert(model : Z3Model, ast : Z3AST) : Boolean =
      model.evalAs[Boolean](ast).getOrElse(false)

    override type ValSort = BoolSort
  }

  implicit object IntValHandler extends ValHandler[Int] {
    def mkSort(z3 : Z3Context) : Z3Sort = z3.mkIntSort()

    def convert(model : Z3Model, ast : Z3AST) : Int =
      model.evalAs[Int](ast).getOrElse(0)

    override type ValSort = IntSort
  }

  implicit object CharValHandler extends ValHandler[Char] {
    def mkSort(z3 : Z3Context) : Z3Sort = z3.mkBVSort(16)

    def convert(model : Z3Model, ast : Z3AST) : Char =
      model.evalAs[Char](ast).getOrElse('\u0000')

    override type ValSort = BVSort
  }

  /** Instances of this class are used to represent models of Z3 maps, which
   * are typically defined by a finite collection of pairs and a default
   * value. More sophisticated representations à la functional programs that
   * can sometimes be obtained from quantified formulas are not yet
   * supported. PS. */
  class PointWiseFunction[-A,+B](points: Map[A,B], default: B) extends (A=>B) {
    def apply(a : A) : B = points.getOrElse(a, default)
  }
  implicit def liftToFuncHandler[A : Default : ValHandler, B : Default : ValHandler] : ValHandler[A=>B] = new ValHandler[A=>B] {
    private val underlyingA = implicitly[ValHandler[A]]
    private val underlyingB = implicitly[ValHandler[B]]

    def mkSort(z3 : Z3Context) : Z3Sort =
      z3.mkArraySort(underlyingA.mkSort(z3), underlyingB.mkSort(z3))

    def convert(model : Z3Model, ast : Z3AST) : (A=>B) = {
      model.eval(ast) match {
        case None => default.value
        case Some(evaluated) => model.getArrayValue(evaluated) match {
          case Some((mp,dflt)) => {
            new PointWiseFunction[A,B](
              mp.map(kv => (underlyingA.convert(model,kv._1), underlyingB.convert(model,kv._2))),
              underlyingB.convert(model,dflt)
            )
          }
          case None => default.value
        }
      }
    }

    override type ValSort = ArraySort
  }

  def choose[T:ValHandler](predicate : Val[T] => Tree[BoolSort]) : T = find(predicate) match {
    case Some(result) => result
    case None => throw new UnsatisfiableConstraintException
  }

  def find[T:ValHandler](predicate : Val[T] => Tree[BoolSort]) : Option[T] = {
    val z3 = new Z3Context("MODEL" -> true)
    val solver = z3.mkSolver()
    val vh = implicitly[ValHandler[T]]
    val value = new Val[T]
    val valAST = value.tree.ast(z3)
    val constraintTree = predicate(value)
    solver.assertCnstr(constraintTree.ast(z3))
    solver.checkAndGetModel() match {
      case (Some(true), m) => {
        val result = vh.convert(m, valAST)
        z3.delete()
        Some(result)
      }
      case (_, m) => {
        z3.delete()
        None
      }
    }
  }

  def findAll[T:ValHandler](predicate : Val[T] => Tree[BoolSort]) : Iterator[T] = {
    val z3 = new Z3Context("MODEL" -> true)
    val solver = z3.mkSolver()
    val vh = implicitly[ValHandler[T]]
    val value = new Val[T]
    val valAST = value.tree.ast(z3)
    val constraintTree = predicate(value)

    solver.assertCnstr(constraintTree.ast(z3))
    solver.checkAndGetAllModels().map(m => {
      val result = vh.convert(m, valAST)
      result
    })
  }

  def choose[T1:ValHandler,T2:ValHandler](predicate : (Val[T1],Val[T2]) => Tree[BoolSort]) : (T1,T2) = find(predicate) match {
    case Some(p) => p
    case None => throw new UnsatisfiableConstraintException
  }

  def find[T1:ValHandler,T2:ValHandler](predicate : (Val[T1],Val[T2]) => Tree[BoolSort]) : Option[(T1,T2)] = {
    val z3 = new Z3Context("MODEL" -> true)
    val solver = z3.mkSolver()
    val vh1 = implicitly[ValHandler[T1]]
    val vh2 = implicitly[ValHandler[T2]]
    val value1 = new Val[T1]
    val value2 = new Val[T2]
    val valAST1 = value1.tree.ast(z3)
    val valAST2 = value2.tree.ast(z3)
    val constraintTree = predicate(value1,value2)
    solver.assertCnstr(constraintTree.ast(z3))
    solver.checkAndGetModel() match {
      case (Some(true), m) => {
        val result1 = vh1.convert(m, valAST1)
        val result2 = vh2.convert(m, valAST2)
        z3.delete()
        Some((result1,result2))
      }
      case (_, m) => {
        z3.delete()
        None
      }
    }
  }

  def findAll[T1:ValHandler,T2:ValHandler](predicate : (Val[T1],Val[T2]) => Tree[BoolSort]) : Iterator[(T1,T2)] = {
    val z3 = new Z3Context("MODEL" -> true)
    val solver = z3.mkSolver()
    val vh1 = implicitly[ValHandler[T1]]
    val vh2 = implicitly[ValHandler[T2]]
    val value1 = new Val[T1]
    val value2 = new Val[T2]
    val valAST1 = value1.tree.ast(z3)
    val valAST2 = value2.tree.ast(z3)
    val constraintTree = predicate(value1, value2)

    solver.assertCnstr(constraintTree.ast(z3))
    solver.checkAndGetAllModels().map(m => {
      val result1 = vh1.convert(m, valAST1)
      val result2 = vh2.convert(m, valAST2)
      (result1,result2)
    })
  }

  def choose[T1:ValHandler,T2:ValHandler,T3:ValHandler](predicate : (Val[T1],Val[T2],Val[T3]) => Tree[BoolSort]) : (T1,T2,T3) = find(predicate) match {
    case Some(p) => p
    case None => throw new UnsatisfiableConstraintException
  }

  def find[T1:ValHandler,T2:ValHandler,T3:ValHandler](predicate : (Val[T1],Val[T2],Val[T3]) => Tree[BoolSort]) : Option[(T1,T2,T3)] = {
    val z3 = new Z3Context("MODEL" -> true)
    val solver = z3.mkSolver()
    val vh1 = implicitly[ValHandler[T1]]
    val vh2 = implicitly[ValHandler[T2]]
    val vh3 = implicitly[ValHandler[T3]]
    val value1 = new Val[T1]
    val value2 = new Val[T2]
    val value3 = new Val[T3]
    val valAST1 = value1.tree.ast(z3)
    val valAST2 = value2.tree.ast(z3)
    val valAST3 = value3.tree.ast(z3)
    val constraintTree = predicate(value1,value2, value3)
    solver.assertCnstr(constraintTree.ast(z3))
    solver.checkAndGetModel() match {
      case (Some(true), m) => {
        val result1 = vh1.convert(m, valAST1)
        val result2 = vh2.convert(m, valAST2)
        val result3 = vh3.convert(m, valAST3)
        z3.delete()
        Some((result1,result2,result3))
      }
      case (_, m) => {
        z3.delete()
        None
      }
    }
  }

  def findAll[T1:ValHandler,T2:ValHandler,T3:ValHandler](predicate : (Val[T1],Val[T2],Val[T3]) => Tree[BoolSort]) : Iterator[(T1,T2,T3)] = {
    val z3 = new Z3Context("MODEL" -> true)
    val solver = z3.mkSolver()

    val vh1 = implicitly[ValHandler[T1]]
    val vh2 = implicitly[ValHandler[T2]]
    val vh3 = implicitly[ValHandler[T3]]
    val value1 = new Val[T1]
    val value2 = new Val[T2]
    val value3 = new Val[T3]
    val valAST1 = value1.tree.ast(z3)
    val valAST2 = value2.tree.ast(z3)
    val valAST3 = value3.tree.ast(z3)
    val constraintTree = predicate(value1, value2, value3)

    solver.assertCnstr(constraintTree.ast(z3))
    solver.checkAndGetAllModels().map(m => {
      val result1 = vh1.convert(m, valAST1)
      val result2 = vh2.convert(m, valAST2)
      val result3 = vh3.convert(m, valAST3)
      (result1,result2,result3)
    })
  }

  implicit def astvectorToSeq(v: Z3ASTVector): Seq[Z3AST] = v.toSeq
}
