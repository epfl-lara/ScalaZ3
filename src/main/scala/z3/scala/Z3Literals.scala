package z3.scala

import z3.{Z3Wrapper,Pointer}
import Z3ASTTypes._

class Z3Literals private[z3](ptr: Long, context: Z3Context) extends Pointer(ptr) {
  def delete : Unit = {
    Z3Wrapper.delLiterals(context.ptr, this.ptr)
  }

  def getNumLiterals : Int = context.getNumLiterals(this) 

//  def getLabelSymbol(idx : Int) : Z3Symbol = context.getLabelSymbol(this, idx)

  def getLiterals[A >: BottomType <: TopType] : Iterator[TypedZ3AST[A]] = new Iterator[TypedZ3AST[A]] {
    val total : Int = getNumLiterals
    var returned : Int = 0

    override def hasNext : Boolean = (returned < total)
    override def next() : TypedZ3AST[A] = {
      val toReturn = getLiteral[A](returned)
      returned += 1
      toReturn
    }
  }

  def getLiteral[A >: BottomType <: TopType](idx : Int) : TypedZ3AST[A] = context.getLiteral[A](this, idx)

  def disableLiteral(idx : Int) : Unit = context.disableLiteral(this, idx)

  def block : Unit = context.blockLiterals(this)

}
