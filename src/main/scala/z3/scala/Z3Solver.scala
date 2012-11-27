package z3.scala

import z3.Z3Wrapper

class Z3Solver private[z3](val ptr : Long, val context : Z3Context) extends Z3Object {
  override def equals(that : Any) : Boolean = {
    that != null &&
      that.isInstanceOf[Z3Solver] && {
      val that2 = that.asInstanceOf[Z3Solver]
      that2.ptr == this.ptr // && context.isEqAST(this, that2)
    }
  }

  def pop(numScopes : Int = 1) = {
    context.solverPop(this, numScopes)
  }

  def push() = {
    context.solverPush(this)
  }

  def assertCnstr(ast: Z3AST) = {
    context.solverAssertCnstr(this, ast)
  }

  def check() : Option[Boolean] = {
    context.solverCheck(this)
  }

  def getModel() : Z3Model = {
    context.solverGetModel(this)
  }

  def getUnsatCore() : Z3ASTVector = {
    new Z3ASTVector(Z3Wrapper.solverGetUnsatCore(context.ptr, this.ptr), context)
  }

  def reset() = context.solverReset(this)

  @deprecated("Delete should not be needed explicitly anymore", "")
  def delete() = decRef()

  def incRef() {
    Z3Wrapper.solverIncRef(context.ptr, this.ptr)
  }

  def decRef() {
    Z3Wrapper.solverDecRef(context.ptr, this.ptr)
  }

  locally {
    context.solverQueue.incRef(this)
  }

  override def finalize() {
    context.solverQueue.decRef(this)
  }
}
