package z3.scala

import com.microsoft.z3.Native

final class Z3ASTVector private[z3](val ptr: Long, val context: Z3Context) extends Z3Object {

  def incRef(): Unit = {
    Native.astVectorIncRef(context.ptr, this.ptr)
  }

  def decRef(): Unit = {
    Native.astVectorDecRef(context.ptr, this.ptr)
  }

  def get(i: Int): Z3AST = {
    new Z3AST(Native.astVectorGet(context.ptr, this.ptr, i), context)
  }

  def set(i: Int, v: Z3AST): Unit = {
    Native.astVectorSet(context.ptr, this.ptr, i, v.ptr)
  }

  def size: Int = {
    Native.astVectorSize(context.ptr, this.ptr)
  }

  // Utility functions
  def apply(i: Int): Z3AST = {
    get(i)
  }

  def update(i: Int, v: Z3AST) = {
    set(i, v)
  }

  def toSeq: Seq[Z3AST] = {
    for (i <- 0 until size) yield {
      get(i)
    }
  }

  locally {
    context.astVectorQueue.track(this)
  }
}
