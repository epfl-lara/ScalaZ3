package z3.scala

import com.microsoft.z3.Native

class Z3Tactic private[z3](val ptr : Long, val context : Z3Context) extends Z3Object {
  override def equals(that: Any) : Boolean = {
    that != null &&
      that.isInstanceOf[Z3Tactic] && {
      val that2 = that.asInstanceOf[Z3Tactic]
      that2.ptr == this.ptr // && context.isEqAST(this, that2)
    }
  }

  def incRef(): Unit = {
    Native.tacticIncRef(context.ptr, this.ptr)
  }

  def decRef(): Unit = {
    Native.tacticDecRef(context.ptr, this.ptr)
  }

  locally {
    context.tacticQueue.track(this)
  }
}
