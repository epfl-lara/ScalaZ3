package z3.scala

import com.microsoft.z3.Native

trait Z3Object extends Z3Pointer {
  val ptr: Long
  val context: Z3Context

  protected[z3] def incRef(): Unit
  protected[z3] def decRef(): Unit
}

trait Z3ASTLike extends Z3Object {
  final protected[z3] def incRef(): Unit = {
    Native.incRef(context.ptr, ptr)
  }

  final protected[z3] def decRef(): Unit = {
    Native.decRef(context.ptr, ptr)
  }
}
