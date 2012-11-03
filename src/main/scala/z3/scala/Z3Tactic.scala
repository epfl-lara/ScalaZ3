package z3.scala

class Z3Tactic private[z3](val ptr : Long, val context : Z3Context) {
  override def equals(that : Any) : Boolean = {
    that != null &&
      that.isInstanceOf[Z3Tactic] && {
      val that2 = that.asInstanceOf[Z3Tactic]
      that2.ptr == this.ptr // && context.isEqAST(this, that2)
    }
  }

  def delete() = context.tacticDelete(this)

}
