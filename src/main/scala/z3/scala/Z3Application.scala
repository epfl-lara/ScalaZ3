package z3.scala

import z3.scala.Z3ASTTypes._

/** This class is inherited to use convenient implicit conversion and utility
 * methods */
class Z3Application(val cfg: Z3Config = new Z3Config("MODEL" -> true)) {
  val ctx = new Z3Context(cfg)

  def conjunct(args: Iterable[TypedZ3AST[_ >: BottomType <: BoolType]]): TypedZ3AST[BoolType] = {
    if (args.size == 0)
      ctx.mkTrue
    else
      ctx.mkAnd(args.toSeq: _*)
  }

  def disjunct(args: Iterable[TypedZ3AST[_ >: BottomType <: BoolType]]): TypedZ3AST[BoolType] = {
    if (args.size == 0)
      ctx.mkFalse
    else
      ctx.mkOr(args.toSeq: _*)
  }

  def forAll[A >: BottomType <: TopType](args: Iterable[TypedZ3AST[A]], pred: (TypedZ3AST[A] => TypedZ3AST[BoolType])): TypedZ3AST[BoolType] = {
    conjunct(args.map(pred))
  }

  implicit def int2Const(i: Int): TypedZ3AST[NumeralType] = ctx.mkInt(i, ctx.mkIntSort)
  implicit def bool2Const(b: Boolean): TypedZ3AST[BoolType] =
    if (b) ctx.mkTrue else ctx.mkFalse

  implicit def intList2constList(xs: List[Int]): List[TypedZ3AST[NumeralType]] =
    xs.map(x => int2Const(x))

  implicit def string2Symbol(s: String): Z3Symbol = ctx.mkStringSymbol(s)
  implicit def int2Symbol(i: Int): Z3Symbol = ctx.mkIntSymbol(i)

  implicit def intList2symbolList(xs: List[Int]): List[Z3Symbol] =
    xs.map(x => int2Symbol(x))

}
