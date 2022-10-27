package z3.scala

import scala.language.implicitConversions

package object dsl {

  import Operands._

  class UnsatisfiableConstraintException extends Exception

  class SortMismatchException(msg: String) extends Exception("Sort mismatch: " + msg)

  given z3ASTToBoolOperand: Conversion[Z3AST, BoolOperand] = { ast =>
    if (!ast.getSort.isBoolSort) {
      throw new SortMismatchException("expected boolean operand, got: " + ast)
    }
    new BoolOperand(Z3ASTWrapper[BoolSort](ast))
  }

  given booleanValueToBoolTree: Conversion[Boolean, Tree[BoolSort]] = BoolConstant.apply

  given booleanValueToBoolOperand: Conversion[Boolean, BoolOperand] = x => new BoolOperand(BoolConstant(x))

  given booleanValToBoolTree: Conversion[Val[Boolean], Tree[BoolSort]] = _.tree.asInstanceOf[Tree[BoolSort]]

  given booleanValToBoolOperand: Conversion[Val[Boolean], BoolOperand] = x => new BoolOperand(x.tree.asInstanceOf[Tree[BoolSort]])

  given boolTreeToBoolOperand[T >: BottomSort <: BoolSort]: Conversion[Tree[T], BoolOperand] = new BoolOperand(_)

  given boolOperandToBoolTree: Conversion[BoolOperand, Tree[BoolSort]] = _.tree

  given z3ASTToIntOperand: Conversion[Z3AST, IntOperand] = { ast =>
    if (!ast.getSort.isIntSort) {
      throw new SortMismatchException("expected integer operand, got: " + ast)
    }
    new IntOperand(Z3ASTWrapper[IntSort](ast))
  }

  given intValueToIntTree: Conversion[Int, Tree[IntSort]] = IntConstant.apply

  given intValueToIntOperand: Conversion[Int, IntOperand] = x => new IntOperand(IntConstant(x))

  given intValToIntTree: Conversion[Val[Int], Tree[IntSort]] = _.tree.asInstanceOf[Tree[IntSort]]

  given intValToIntOperand: Conversion[Val[Int], IntOperand] = x => new IntOperand(x.tree.asInstanceOf[Tree[IntSort]])

  given intTreeToIntOperand[T >: BottomSort <: IntSort]: Conversion[Tree[T], IntOperand] = new IntOperand(_)

  given intOperandToIntTree: Conversion[IntOperand, Tree[IntSort]] = _.tree

  given charValueToBVTree: Conversion[Char, Tree[BVSort]] = CharConstant.apply

  given charValueToBVOperand: Conversion[Char, BitVectorOperand] = x => new BitVectorOperand(CharConstant(x))

  given charValToCharTree: Conversion[Val[Char], Tree[BVSort]] = _.tree.asInstanceOf[Tree[BVSort]]

  given charValToBVOperand: Conversion[Val[Char], BitVectorOperand] = x => new BitVectorOperand(x.tree.asInstanceOf[Tree[BVSort]])

  given bvTreeToBVOperand[T >: BottomSort <: BVSort]: Conversion[Tree[T], BitVectorOperand] = new BitVectorOperand(_)

  given bvOperandToBVTree: Conversion[BitVectorOperand, Tree[BVSort]] = _.tree

  given z3ASTToSetOperand: Conversion[Z3AST, SetOperand] = x =>
    // TODO how do we check the type (set of any type?) here?
    new SetOperand(Z3ASTWrapper[SetSort](x))

  given intSetValueToSetTree: Conversion[Set[Int], Tree[SetSort]] = _.foldLeft[Tree[SetSort]](EmptyIntSet())((set, elem) => SetAdd(set, IntConstant(elem)))

  given intSetValueToSetOperand: Conversion[Set[Int], SetOperand] = x => new SetOperand(intSetValueToSetTree(x))

  given setTreeToSetOperand[T >: BottomSort <: SetSort]: Conversion[Tree[T], SetOperand] = new SetOperand(_)

  given setOperandToSetTree: Conversion[SetOperand, Tree[SetSort]] = _.tree

  // All default values

  given DefaultInt: Default[Int] with
    val value = 0

  given DefaultBoolean: Default[Boolean] with
    val value = true

  given DefaultChar: Default[Char] with
    val value = '\u0000'

  given liftDefaultToSet[A: Default]: Default[Set[A]] = {
    new Default[Set[A]] {
      val value = Set.empty[A]
    }
  }

  given liftDefaultToFun[A, B: Default]: Default[A => B] = {
    new Default[A => B] {
      val value = ((a: A) => summon[Default[B]].value)
    }
  }

  // Predefined ValHandler's

  given BooleanValHandler: ValHandler[Boolean] with
    def mkSort(z3: Z3Context): Z3Sort = z3.mkBoolSort()

    def convert(model: Z3Model, ast: Z3AST): Boolean =
      model.evalAs[Boolean](ast).getOrElse(false)

    override type ValSort = BoolSort

  given IntValHandler: ValHandler[Int] with
    def mkSort(z3: Z3Context): Z3Sort = z3.mkIntSort()

    def convert(model: Z3Model, ast: Z3AST): Int =
      model.evalAs[Int](ast).getOrElse(0)

    override type ValSort = IntSort

  given CharValHandler: ValHandler[Char] with
    def mkSort(z3: Z3Context): Z3Sort = z3.mkBVSort(16)

    def convert(model: Z3Model, ast: Z3AST): Char =
      model.evalAs[Char](ast).getOrElse('\u0000')

    override type ValSort = BVSort

  /** Instances of this class are used to represent models of Z3 maps, which
   * are typically defined by a finite collection of pairs and a default
   * value. More sophisticated representations à la functional programs that
   * can sometimes be obtained from quantified formulas are not yet
   * supported. PS. */
  class PointWiseFunction[-A, +B](points: Map[A, B], default: B) extends (A => B) {
    def apply(a: A): B = points.getOrElse(a, default)
  }

  given liftToFuncHandler[A: Default : ValHandler, B: Default : ValHandler]: ValHandler[A => B] = new ValHandler[A => B] {
    private val underlyingA = summon[ValHandler[A]]
    private val underlyingB = summon[ValHandler[B]]

    def mkSort(z3: Z3Context): Z3Sort =
      z3.mkArraySort(underlyingA.mkSort(z3), underlyingB.mkSort(z3))

    def convert(model: Z3Model, ast: Z3AST): (A => B) = {
      model.eval(ast) match {
        case None => default.value
        case Some(evaluated) => model.getArrayValue(evaluated) match {
          case Some((mp, dflt)) =>
            new PointWiseFunction[A, B](
              mp.map(kv => (underlyingA.convert(model, kv._1), underlyingB.convert(model, kv._2))),
              underlyingB.convert(model, dflt)
            )
          case None => default.value
        }
      }
    }

    override type ValSort = ArraySort
  }

  def choose[T: ValHandler](predicate: Val[T] => Tree[BoolSort]): T = find(predicate) match {
    case Some(result) => result
    case None => throw new UnsatisfiableConstraintException
  }

  def find[T: ValHandler](predicate: Val[T] => Tree[BoolSort]): Option[T] = {
    val z3 = new Z3Context("MODEL" -> true)
    val solver = z3.mkSolver()
    val vh = summon[ValHandler[T]]
    val value = new Val[T]
    val valAST = value.tree.ast(z3)
    val constraintTree = predicate(value)
    solver.assertCnstr(constraintTree.ast(z3))
    solver.checkAndGetModel() match {
      case (Some(true), m) =>
        val result = vh.convert(m, valAST)
        z3.delete()
        Some(result)
      case _ =>
        z3.delete()
        None
    }
  }

  def findAll[T: ValHandler](predicate: Val[T] => Tree[BoolSort]): Iterator[T] = {
    val z3 = new Z3Context("MODEL" -> true)
    val solver = z3.mkSolver()
    val vh = summon[ValHandler[T]]
    val value = new Val[T]
    val valAST = value.tree.ast(z3)
    val constraintTree = predicate(value)

    solver.assertCnstr(constraintTree.ast(z3))
    solver.checkAndGetAllModels().map(m => {
      val result = vh.convert(m, valAST)
      result
    })
  }

  def choose[T1: ValHandler, T2: ValHandler](predicate: (Val[T1], Val[T2]) => Tree[BoolSort]): (T1, T2) = find(predicate) match {
    case Some(p) => p
    case None => throw new UnsatisfiableConstraintException
  }

  def find[T1: ValHandler, T2: ValHandler](predicate: (Val[T1], Val[T2]) => Tree[BoolSort]): Option[(T1, T2)] = {
    val z3 = new Z3Context("MODEL" -> true)
    val solver = z3.mkSolver()
    val vh1 = summon[ValHandler[T1]]
    val vh2 = summon[ValHandler[T2]]
    val value1 = new Val[T1]
    val value2 = new Val[T2]
    val valAST1 = value1.tree.ast(z3)
    val valAST2 = value2.tree.ast(z3)
    val constraintTree = predicate(value1, value2)
    solver.assertCnstr(constraintTree.ast(z3))
    solver.checkAndGetModel() match {
      case (Some(true), m) =>
        val result1 = vh1.convert(m, valAST1)
        val result2 = vh2.convert(m, valAST2)
        z3.delete()
        Some((result1, result2))
      case _ =>
        z3.delete()
        None
    }
  }

  def findAll[T1: ValHandler, T2: ValHandler](predicate: (Val[T1], Val[T2]) => Tree[BoolSort]): Iterator[(T1, T2)] = {
    val z3 = new Z3Context("MODEL" -> true)
    val solver = z3.mkSolver()
    val vh1 = summon[ValHandler[T1]]
    val vh2 = summon[ValHandler[T2]]
    val value1 = new Val[T1]
    val value2 = new Val[T2]
    val valAST1 = value1.tree.ast(z3)
    val valAST2 = value2.tree.ast(z3)
    val constraintTree = predicate(value1, value2)

    solver.assertCnstr(constraintTree.ast(z3))
    solver.checkAndGetAllModels().map(m => {
      val result1 = vh1.convert(m, valAST1)
      val result2 = vh2.convert(m, valAST2)
      (result1, result2)
    })
  }

  def choose[T1: ValHandler, T2: ValHandler, T3: ValHandler](predicate: (Val[T1], Val[T2], Val[T3]) => Tree[BoolSort]): (T1, T2, T3) = find(predicate) match {
    case Some(p) => p
    case None => throw new UnsatisfiableConstraintException
  }

  def find[T1: ValHandler, T2: ValHandler, T3: ValHandler](predicate: (Val[T1], Val[T2], Val[T3]) => Tree[BoolSort]): Option[(T1, T2, T3)] = {
    val z3 = new Z3Context("MODEL" -> true)
    val solver = z3.mkSolver()
    val vh1 = summon[ValHandler[T1]]
    val vh2 = summon[ValHandler[T2]]
    val vh3 = summon[ValHandler[T3]]
    val value1 = new Val[T1]
    val value2 = new Val[T2]
    val value3 = new Val[T3]
    val valAST1 = value1.tree.ast(z3)
    val valAST2 = value2.tree.ast(z3)
    val valAST3 = value3.tree.ast(z3)
    val constraintTree = predicate(value1, value2, value3)
    solver.assertCnstr(constraintTree.ast(z3))
    solver.checkAndGetModel() match {
      case (Some(true), m) =>
        val result1 = vh1.convert(m, valAST1)
        val result2 = vh2.convert(m, valAST2)
        val result3 = vh3.convert(m, valAST3)
        z3.delete()
        Some((result1, result2, result3))
      case _ =>
        z3.delete()
        None
    }
  }

  def findAll[T1: ValHandler, T2: ValHandler, T3: ValHandler](predicate: (Val[T1], Val[T2], Val[T3]) => Tree[BoolSort]): Iterator[(T1, T2, T3)] = {
    val z3 = new Z3Context("MODEL" -> true)
    val solver = z3.mkSolver()

    val vh1 = summon[ValHandler[T1]]
    val vh2 = summon[ValHandler[T2]]
    val vh3 = summon[ValHandler[T3]]
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
      (result1, result2, result3)
    })
  }

  given astVectorToSeq: Conversion[Z3ASTVector, Seq[Z3AST]] = _.toSeq
}
