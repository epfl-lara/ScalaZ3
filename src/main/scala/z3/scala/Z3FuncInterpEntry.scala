package z3.scala

import com.microsoft.z3.Native

private[scala] class Z3FuncInterpEntry(val ptr: Long, val context: Z3Context) extends Z3Object {

  lazy val value = new Z3AST(Native.funcEntryGetValue(context.ptr, ptr), context)

  lazy val args: Seq[Z3AST] = {
    val numArgs = Native.funcEntryGetNumArgs(context.ptr, ptr)
    for (argIndex <- (0 until numArgs).toList) yield {
      new Z3AST(Native.funcEntryGetArg(context.ptr, ptr, argIndex), context)
    }
  }

  final protected[z3] def incRef() {
    Native.funcEntryIncRef(context.ptr, ptr)
  }

  final protected[z3] def decRef() {
    Native.funcEntryDecRef(context.ptr, ptr)
  }

  locally { context.entryQueue.track(this) }
}

