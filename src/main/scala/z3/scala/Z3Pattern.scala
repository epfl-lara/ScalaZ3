package z3.scala

import z3.Pointer

class Z3Pattern private[z3](ptr: Long, context: Z3Context) extends Pointer(ptr) {
  override def toString : String = context.patternToString(this)
}
