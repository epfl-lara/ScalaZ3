package z3.scala

import com.microsoft.z3.Native
import com.microsoft.z3.Z3Exception

private[scala] class Z3Params(val ptr: Long, val context: Z3Context) extends Z3Object {

  def set(key: String, value: Any): Unit = value match {
    case b: Boolean => Native.paramsSetBool(context.ptr, ptr, context.mkSymbol(key).ptr, b)
    case d: Double => Native.paramsSetDouble(context.ptr, ptr, context.mkSymbol(key).ptr, d)
    case i: Int => Native.paramsSetUint(context.ptr, ptr, context.mkSymbol(key).ptr, i)
    case s: String => Native.paramsSetSymbol(context.ptr, ptr, context.mkSymbol(key).ptr, context.mkSymbol(s).ptr)
    case _ => throw new Z3Exception("Can't set value " + value + " of type " + value.getClass)
  }

  final protected[z3] def incRef(): Unit = {
    Native.paramsIncRef(context.ptr, ptr)
  }

  final protected[z3] def decRef(): Unit = {
    Native.paramsDecRef(context.ptr, ptr)
  }

  locally {
    context.paramsQueue.track(this)
  }
}
