package z3.scala

import z3.Pointer

sealed class Z3Pattern private[z3](val ptr: Long, context: Z3Context) {
  override def toString : String = context.patternToString(this)
}
