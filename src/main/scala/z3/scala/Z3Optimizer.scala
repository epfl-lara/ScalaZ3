package z3.scala

import com.microsoft.z3.Native
import scala.collection.mutable.{Map => MutableMap}

class Z3Optimizer private[z3](val ptr: Long, val context: Z3Context) extends Z3Object {

  override def equals(that: Any): Boolean = {
    that != null &&
    that.isInstanceOf[Z3Optimizer] &&
    that.asInstanceOf[Z3Optimizer].ptr == this.ptr
  }

  def pop() = {
    Native.optimizePop(context.ptr, this.ptr)
  }

  def push() = {
    Native.optimizePush(context.ptr, this.ptr)
  }

  def set(params: Map[String, Any]): Unit = {
    val z3params = new Z3Params(Native.mkParams(context.ptr), context)
    for ((k, v) <- params) z3params.set(k, v)
    Native.optimizeSetParams(context.ptr, ptr, z3params.ptr)
  }

  def assertCnstr(ast: Z3AST) = {
    Native.optimizeAssert(context.ptr, this.ptr, ast.ptr)
  }

  class Handle private[z3](handle: Int) {
    def getUpper: Z3AST = new Z3AST(Native.optimizeGetUpper(context.ptr, Z3Optimizer.this.ptr, handle), context)
    def getLower: Z3AST = new Z3AST(Native.optimizeGetLower(context.ptr, Z3Optimizer.this.ptr, handle), context)
  }

  def maximize(ast: Z3AST) = {
    Native.optimizeMaximize(context.ptr, this.ptr, ast.ptr)
  }

  def minimize(ast: Z3AST) = {
    Native.optimizeMinimize(context.ptr, this.ptr, ast.ptr)
  }

  def assertCnstr(ast: Z3AST, weight: Int) = {
    val s = context.mkFreshStringSymbol("opt$group")
    new Handle(Native.optimizeAssertSoft(context.ptr, this.ptr, ast.ptr, weight.toString, s.ptr))
  }

  private val groupSymbols: MutableMap[String, Z3Symbol] = MutableMap.empty

  def assertCnstr(ast: Z3AST, weight: Int, group: String) = {
    val s = groupSymbols.get(group) match {
      case Some(s) => s
      case None =>
        val s = context.mkFreshStringSymbol(group)
        groupSymbols += group -> s
        s
    }
    new Handle(Native.optimizeAssertSoft(context.ptr, this.ptr, ast.ptr, weight.toString, s.ptr))
  }

  def getAssertions(): Z3ASTVector = {
    new Z3ASTVector(Native.optimizeGetAssertions(context.ptr, this.ptr), context)
  }

  private[this] var lastResult: Option[Boolean] = None

  def check(): Option[Boolean] = {
    val res = i2ob(Native.optimizeCheck(context.ptr, this.ptr))
    lastResult = res
    res
  }

  def getModel(): Z3Model = {
    if (lastResult == Some(true)) {
      new Z3Model(Native.optimizeGetModel(context.ptr, this.ptr), context)
    } else {
      throw new Exception("Cannot get model if check failed")
    }
  }

  def incRef(): Unit = {
    Native.optimizeIncRef(context.ptr, this.ptr)
  }

  def decRef(): Unit = {
    Native.optimizeDecRef(context.ptr, this.ptr)
  }

  def getReasonUnknown(): String = {
    Native.optimizeGetReasonUnknown(context.ptr, this.ptr)
  }

  override def toString: String = {
    Native.optimizeToString(context.ptr, this.ptr)
  }

  locally {
    context.optimizerQueue.track(this)
  }
}
