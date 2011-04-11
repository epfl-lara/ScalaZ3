package z3.scala

import z3.scala.Z3ASTTypes._

class Z3Function[A >: BottomType <: TopType, B >: BottomType <: TopType](private val interpretation: (Seq[(Seq[TypedZ3AST[A]], TypedZ3AST[_ <: B])], TypedZ3AST[_ <: B])) {
  private lazy val iMap = interpretation._1.toMap

  val arity = interpretation._1.head._1.size

  def apply(args: TypedZ3AST[A]*) = {
    assert(args.size == arity, "function call with wrong number of arguments")
    iMap.get(args) match {
      case Some(res) => res
      case None => interpretation._2
    }
  }
}

