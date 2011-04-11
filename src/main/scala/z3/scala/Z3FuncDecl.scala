package z3.scala

import z3.Pointer
import z3.scala.Z3ASTTypes._

// We store the arity when it's known to help preventing segfaults...
class Z3FuncDecl private[z3](ptr: Long, val arity: Int, context: Z3Context) extends Pointer(ptr) {
  def apply[A >: BottomType <: TopType](args: Z3AST*) : TypedZ3AST[A] = context.mkApp[A](this, args:_*)

  lazy val getName: Z3Symbol = context.getDeclName(this)

  def getDomainSize : Int = arity

  def getDomain(i: Int) : Z3Sort = context.getDomain(this, i)

  lazy val getRange : Z3Sort = context.getRange(this)

//  override def equals(that: Any) : Boolean = {
//    if(that == null) false else (that.isInstanceOf[Z3FuncDecl] && this.ptr == that.asInstanceOf[Z3FuncDecl].ptr)
//  }

  override def equals(that: Any) : Boolean = {
    that != null && that.isInstanceOf[Z3FuncDecl] && context.isEqFuncDecl(this, that.asInstanceOf[Z3FuncDecl])
  }

  private lazy val hc : Int = 0
  override def hashCode : Int = hc

  override def toString : String = context.funcDeclToString(this)
}
