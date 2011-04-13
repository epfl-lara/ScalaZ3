package z3.scala

/** This class is inherited to use convenient implicit conversion and utility
 * methods */
class Z3Application(val cfg: Z3Config = new Z3Config("MODEL" -> true)) {
  val ctx = new Z3Context(cfg)

  def conjunct(args: Iterable[Z3AST]): Z3AST = {
    if (args.size == 0)
      ctx.mkTrue
    else
      ctx.mkAnd(args.toSeq: _*)
  }

  def disjunct(args: Iterable[Z3AST]): Z3AST = {
    if (args.size == 0)
      ctx.mkFalse
    else
      ctx.mkOr(args.toSeq: _*)
  }

  def forAll(args: Iterable[Z3AST], pred: (Z3AST => Z3AST)): Z3AST = {
    conjunct(args.map(pred))
  }

  implicit def int2Const(i: Int): Z3AST = ctx.mkInt(i, ctx.mkIntSort)
  implicit def bool2Const(b: Boolean): Z3AST =
    if (b) ctx.mkTrue else ctx.mkFalse

  implicit def intList2constList(xs: List[Int]): List[Z3AST] =
    xs.map(x => int2Const(x))

  implicit def string2Symbol(s: String): Z3Symbol = ctx.mkStringSymbol(s)
  implicit def int2Symbol(i: Int): Z3Symbol = ctx.mkIntSymbol(i)

  implicit def intList2symbolList(xs: List[Int]): List[Z3Symbol] =
    xs.map(x => int2Symbol(x))

}
