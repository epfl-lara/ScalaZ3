package z3.scala

import com.microsoft.z3.Native

private[scala] class Z3FuncInterp (val ptr: Long, val context: Z3Context) extends Z3Object {

  lazy val default: Z3AST = new Z3AST(Native.funcInterpGetElse(context.ptr, ptr), context)

  lazy val entries: Seq[(Seq[Z3AST], Z3AST)] = {
    val numEntries = Native.funcInterpGetNumEntries(context.ptr, ptr)
    for (entryIndex <- (0 until numEntries).toList) yield {
      val entry = new Z3FuncInterpEntry(Native.funcInterpGetEntry(context.ptr, ptr, entryIndex), context)
      entry.args -> entry.value
    }
  }

  override def toString = "[" + entries.map { case (args, value) =>
    args.mkString("(", ", ", ")") + " -> " + value
  }.mkString(", ") + ", else -> " + default + "]"

  final protected[z3] def incRef() {
    Native.funcInterpIncRef(context.ptr, ptr)
  }

  final protected[z3] def decRef() {
    Native.funcInterpDecRef(context.ptr, ptr)
  }

  locally { context.interpQueue.track(this) }
}
