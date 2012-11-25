package z3.scala

class Z3Solver private[z3](val ptr : Long, val context : Z3Context) {
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

  def reset() = context.solverReset(this)

  def delete() = context.solverDelete(this)

}
