package z3.scala

import z3.Pointer

class Z3AST private[z3](ptr : Long, val context : Z3Context) extends Pointer(ptr) {
  override def equals(that : Any) : Boolean = {
    that != null &&
    that.isInstanceOf[Z3AST] && {
      val that2 = that.asInstanceOf[Z3AST]
      that2.ptr == this.ptr // && context.isEqAST(this, that2)
    }
  }

  private lazy val hc : Int = (ptr >> 4).toInt
  override def hashCode : Int = hc
  override def toString : String = context.astToString(this)


  def ===(that: Z3AST): Z3AST = context.mkEq(this, that)
  def !==(that: Z3AST): Z3AST = context.mkDistinct(context.mkEq(this, that))

  import dsl.{Tree,BoolSort,Z3ASTWrapper,Eq,Distinct}
  def ===(that: Tree[BoolSort]): Tree[BoolSort] = Eq(Z3ASTWrapper[BoolSort](this), that)
  def !==(that: Tree[BoolSort]): Tree[BoolSort] = Distinct(Z3ASTWrapper[BoolSort](this), that)
}
