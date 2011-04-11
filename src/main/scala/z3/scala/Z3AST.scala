package z3.scala

import z3.Pointer
import Z3ASTTypes._

class RichZ3NumeralAST(val ast: TypedZ3AST[_ <: NumeralType]) {

  private implicit def int2ast(i: Int) = ast.context.mkInt(i, ast.context.mkIntSort)

  def ++(that: TypedZ3AST[_ <: NumeralType]): TypedZ3AST[NumeralType] =
    ast.context.mkAdd(ast, that)

  def ++(that: Int): TypedZ3AST[NumeralType] =
    ast.context.mkAdd(ast, that)

  def *(that: TypedZ3AST[_ <: NumeralType]): TypedZ3AST[NumeralType] =
    ast.context.mkMul(ast, that)

  def *(that: Int): TypedZ3AST[NumeralType] =
    ast.context.mkMul(ast, that)

  def -(that: TypedZ3AST[_ <: NumeralType]): TypedZ3AST[NumeralType] =
    ast.context.mkSub(ast, that)

  def -(that: Int): TypedZ3AST[NumeralType] =
    ast.context.mkSub(ast, that)

  def unary_- : TypedZ3AST[NumeralType] =
    ast.context.mkSub(ast.context.mkInt(0, ast.context.mkIntSort), ast)

  def /(that: TypedZ3AST[_ <: NumeralType]): TypedZ3AST[NumeralType] =
    ast.context.mkDiv(ast, that)

  def /(that: Int): TypedZ3AST[NumeralType] =
    ast.context.mkDiv(ast, that)

  def div(that: TypedZ3AST[_ <: NumeralType]): TypedZ3AST[NumeralType] =
    ast.context.mkDiv(ast, that)

  def div(that: Int): TypedZ3AST[NumeralType] =
    ast.context.mkDiv(ast, that)

  def mod(that: TypedZ3AST[_ <: NumeralType]): TypedZ3AST[NumeralType] =
    ast.context.mkMod(ast, that)

  def mod(that: Int): TypedZ3AST[NumeralType] =
    ast.context.mkMod(ast, that)

  def rem(that: TypedZ3AST[_ <: NumeralType]): TypedZ3AST[NumeralType] =
    ast.context.mkRem(ast, that)

  def rem(that: Int): TypedZ3AST[NumeralType] =
    ast.context.mkRem(ast, that)

  def <(that: TypedZ3AST[_ <: NumeralType]): TypedZ3AST[BoolType] =
    ast.context.mkLT(ast, that)

  def <(that: Int): TypedZ3AST[BoolType] =
    ast.context.mkLT(ast, that)

  def <=(that: TypedZ3AST[_ <: NumeralType]): TypedZ3AST[BoolType] =
    ast.context.mkLE(ast, that)

  def <=(that: Int): TypedZ3AST[BoolType] =
    ast.context.mkLE(ast, that)

  def >(that: TypedZ3AST[_ <: NumeralType]): TypedZ3AST[BoolType] =
    ast.context.mkGT(ast, that)

  def >(that: Int): TypedZ3AST[BoolType] =
    ast.context.mkGT(ast, that)

  def >=(that: TypedZ3AST[_ <: NumeralType]): TypedZ3AST[BoolType] =
    ast.context.mkGE(ast, that)

  def >=(that: Int): TypedZ3AST[BoolType] =
    ast.context.mkGE(ast, that)
}

class RichZ3BVAST(val ast: TypedZ3AST[_ <: BVType]) {
}

class RichZ3BoolAST(val ast: TypedZ3AST[_ <: BoolType]) {
  def unary_! : TypedZ3AST[BoolType] =
    ast.context.mkNot(ast)

  def <-->(that: TypedZ3AST[_ <: BoolType]): TypedZ3AST[BoolType] =
    ast.context.mkIff(ast, that)

  def -->(that: TypedZ3AST[_ <: BoolType]): TypedZ3AST[BoolType] =
    ast.context.mkImplies(ast, that)

  def xor(that: TypedZ3AST[_ <: BoolType]): TypedZ3AST[BoolType] =
    ast.context.mkXor(ast, that)

  def ^(that: TypedZ3AST[_ <: BoolType]): TypedZ3AST[BoolType] =
    xor(that)

  def &&(that: TypedZ3AST[_ <: BoolType]): TypedZ3AST[BoolType] =
    ast.context.mkAnd(ast, that)

  def ||(that: TypedZ3AST[_ <: BoolType]): TypedZ3AST[BoolType] =
    ast.context.mkOr(ast, that)
}

class RichZ3ArrayAST(val ast: TypedZ3AST[_ <: ArrayType]) {
  def union(that: TypedZ3AST[_ <: ArrayType]): TypedZ3AST[ArrayType] =
    ast.context.mkSetUnion(ast, that)

  def |(that: TypedZ3AST[_ <: ArrayType]): TypedZ3AST[ArrayType] =
    union(that)

  def intersect(that: TypedZ3AST[_ <: ArrayType]): TypedZ3AST[ArrayType] =
    ast.context.mkSetIntersect(ast, that)

  def &(that: TypedZ3AST[_ <: ArrayType]): TypedZ3AST[ArrayType] =
    intersect(that)
  
  def **(that: TypedZ3AST[_ <: ArrayType]): TypedZ3AST[ArrayType] =
    intersect(that)

  def diff(that: TypedZ3AST[_ <: ArrayType]): TypedZ3AST[ArrayType] =
    ast.context.mkSetDifference(ast, that)

  def &~(that: TypedZ3AST[_ <: ArrayType]): TypedZ3AST[ArrayType] =
    diff(that)

  def contains(that: Z3AST): TypedZ3AST[BoolType] =
    ast.context.mkSetMember(that, ast)
  
  def subsetOf(that: TypedZ3AST[_ <: ArrayType]): TypedZ3AST[BoolType] =
    ast.context.mkSetSubset(ast, that)
}

object TypedZ3AST {
  implicit def ast2numeralAst(ast: TypedZ3AST[_ <: NumeralType]): RichZ3NumeralAST = new RichZ3NumeralAST(ast)
  implicit def ast2bvAst(ast: TypedZ3AST[_ <: BVType]): RichZ3BVAST = new RichZ3BVAST(ast)
  implicit def ast2boolAst(ast: TypedZ3AST[_ <: BoolType]): RichZ3BoolAST = new RichZ3BoolAST(ast)
  implicit def ast2arrayAst(ast: TypedZ3AST[_ <: ArrayType]): RichZ3ArrayAST = new RichZ3ArrayAST(ast)

  // these are used with the choose construct.
  implicit def fromVar[A,B >: BottomType <: TopType](vr: z3.scala.choose.Var[A]) : TypedZ3AST[B] = vr.ast.asInstanceOf[TypedZ3AST[B]]
}

class TypedZ3AST[+A >: BottomType <: TopType]  private[z3](ptr: Long, val context: Z3Context) extends Pointer(ptr) {
  private implicit def int2ast(i: Int) = context.mkInt(i, context.mkIntSort)

  override def equals(that: Any) : Boolean = {
    that != null && that.isInstanceOf[TypedZ3AST[_]] && that.asInstanceOf[Z3AST].ptr == this.ptr // && context.isEqAST(this, that.asInstanceOf[Z3AST])
  }

  private lazy val hc : Int = (ptr >> 4).toInt
  override def hashCode : Int = hc
  override def toString : String = context.astToString(this)

  def ===(that: Z3AST): TypedZ3AST[BoolType] =
    context.mkEq(this, that)

  def ===(that: Int): TypedZ3AST[BoolType] =
    context.mkEq(this, that)

  def !==(that: Z3AST): TypedZ3AST[BoolType] =
    context.mkNot(context.mkEq(this, that))

  def !==(that: Int): TypedZ3AST[BoolType] =
    context.mkNot(context.mkEq(this, that))
}
