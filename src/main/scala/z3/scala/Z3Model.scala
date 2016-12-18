package z3.scala

import com.microsoft.z3.Native

object Z3Model {
  implicit def ast2int(model: Z3Model, ast: Z3AST): Option[Int] = {
    val res = model.eval(ast)
    if (res.isEmpty)
      None
    else
      model.context.getNumeralInt(res.get).value
  }

  implicit def ast2bool(model: Z3Model, ast: Z3AST): Option[Boolean] = {
    val res = model.eval(ast)
    if (res.isEmpty)
      None
    else
      model.context.getBoolValue(res.get)
  }

  implicit def ast2char(model: Z3Model, ast: Z3AST): Option[Char] = {
    val res = model.eval(ast)
    if (res.isEmpty)
      None
    else
      model.context.getNumeralInt(res.get).value.map(_.toChar)
  }
}

sealed class Z3Model private[z3](val ptr: Long, val context: Z3Context) extends Z3Object {
  override def toString : String = context.modelToString(this)

  def incRef() {
    Native.modelIncRef(context.ptr, this.ptr)
  }

  def decRef() {
    Native.modelDecRef(context.ptr, this.ptr)
  }

  def eval(ast: Z3AST, completion: Boolean = false) : Option[Z3AST] = {
    if(this.ptr == 0L) {
      throw new IllegalStateException("The model is not initialized.")
    }
    val out = new Native.LongPtr()
    val result = Native.modelEval(context.ptr, this.ptr, ast.ptr, completion, out)
    if (result && out.value != 0L) {
      Some(new Z3AST(out.value, context))
    } else {
      None
    }
  }

  def evalAs[T](input: Z3AST)(implicit converter: (Z3Model, Z3AST) => Option[T]): Option[T] = {
    converter(this, input)
  }

  def getConsts: Iterator[Z3FuncDecl] = {
    val model = this
    new Iterator[Z3FuncDecl] {
      val total: Int = Native.modelGetNumConsts(context.ptr, model.ptr)
      var returned: Int = 0

      override def hasNext: Boolean = (returned < total)
      override def next(): Z3FuncDecl = {
        val toReturn = new Z3FuncDecl(Native.modelGetConstDecl(context.ptr, model.ptr, returned), 0, context)
        returned += 1
        toReturn
      }
    }
  }

  def getConstInterpretations : Iterator[(Z3FuncDecl, Z3AST)] = {
    val model = this
    val constantIterator = getConsts
    new Iterator[(Z3FuncDecl, Z3AST)] {
      override def hasNext: Boolean = constantIterator.hasNext
      override def next(): (Z3FuncDecl, Z3AST) = {
        val nextConstant = constantIterator.next()
        (nextConstant, model.eval(nextConstant()).get)
      }
    }
  }

  private lazy val constantInterpretationMap: Map[String, Z3AST] =
   getConstInterpretations.map(p => (p._1.getName.toString, p._2)).toMap

  def getConstInterpretation(name: String): Option[Z3AST] = constantInterpretationMap.get(name)

  def getFuncInterpretations : Iterator[(Z3FuncDecl, Seq[(Seq[Z3AST], Z3AST)], Z3AST)] = {
    val model = this
    new Iterator[(Z3FuncDecl, Seq[(Seq[Z3AST], Z3AST)], Z3AST)] {
      val total: Int = Native.modelGetNumFuncs(context.ptr, model.ptr)
      var returned: Int = 0

      override def hasNext: Boolean = (returned < total)
      override def next(): (Z3FuncDecl, Seq[(Seq[Z3AST], Z3AST)], Z3AST) = {
        val declPtr = Native.modelGetFuncDecl(context.ptr, model.ptr, returned)
        val arity = Native.getDomainSize(context.ptr, declPtr)
        val funcDecl = new Z3FuncDecl(declPtr, arity, context)
        val funcInterp = new Z3FuncInterp(Native.modelGetFuncInterp(context.ptr, model.ptr, declPtr), context)
        returned += 1
        (funcDecl, funcInterp.entries, funcInterp.default)
      }
    }
  }

  private lazy val funcInterpretationMap: Map[Z3FuncDecl, (Seq[(Seq[Z3AST], Z3AST)], Z3AST)] =
    getFuncInterpretations.map(i => (i._1, (i._2, i._3))).toMap

  def isArrayValue(ast: Z3AST) : Boolean = {
    Native.isAsArray(context.ptr, ast.ptr)
  }

  def getArrayValue(ast: Z3AST) : Option[(Map[Z3AST, Z3AST], Z3AST)] = if (isArrayValue(ast)) {
    val funcPtr = Native.getAsArrayFuncDecl(context.ptr, ast.ptr)
    val arity = Native.getDomainSize(context.ptr, funcPtr)
    assert(arity == 1, "Arrays with arity > 1 aren't handled by ScalaZ3")
    val funcInterp = new Z3FuncInterp(Native.modelGetFuncInterp(context.ptr, this.ptr, funcPtr), context)
    Some(funcInterp.entries.map { case (args, value) => args.head -> value }.toMap, funcInterp.default)
  } else {
    import Z3DeclKind._
    def rec(ast: Z3AST): Option[(Map[Z3AST, Z3AST], Z3AST)] = context.getASTKind(ast) match {
      case Z3AppAST(funcDecl, args) => context.getDeclKind(funcDecl) match {
        case OpStore => rec(args(0)).map { case (mapping, default) =>
          (mapping + (args(1) -> args(2)), default)
        }
        case OpConstArray =>
          Some(Map.empty, args(0))
        case _ => None
      }
      case _ => None
    }

    rec(ast)
  }

  def getSetValue(ast: Z3AST) : Option[(Set[Z3AST], Boolean)] = {
    val value = getArrayValue(ast)

    value.flatMap { case (mapping, dflt) =>
      evalAs[Boolean](dflt) match {
        case Some(false) => // finite set
          Some((mapping.collect { case (k, v) if evalAs[Boolean](v) == Some(true) => k }.toSet, true))
        case Some(true) =>  // co-finite set
          Some((mapping.collect { case (k, v) if evalAs[Boolean](v) == Some(false) => k }.toSet, false))
        case _ =>
          None
      }
    }
  }

  def getFuncInterpretation(fd: Z3FuncDecl): Option[(Seq[(Seq[Z3AST], Z3AST)], Z3AST)] = 
    funcInterpretationMap.get(fd)

  locally {
    context.modelQueue.track(this)
  }
}
