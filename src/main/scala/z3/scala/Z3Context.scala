package z3.scala

import dsl.Z3ASTWrapper
import z3.Z3Wrapper
import com.microsoft.z3.Native
import com.microsoft.z3.enumerations._
import scala.collection.mutable.{Set=>MutableSet}
import java.math.BigInteger

object Z3Context {
  sealed abstract class ADTSortReference
  case class RecursiveType(index: Int) extends ADTSortReference
  case class RegularSort(sort: Z3Sort) extends ADTSortReference

  object AstPrintMode extends Enumeration {
    type AstPrintMode = Value
    val Z3_PRINT_SMTLIB_FULL, Z3_PRINT_LOW_LEVEL, Z3_PRINT_SMTLIB_COMPLIANT, Z3_PRINT_SMTLIB2_COMPLIANT = Value
  }
  import AstPrintMode._
}

sealed class Z3Context(val config: Map[String, String]) {
  val ptr: Long = Z3Wrapper.creation_lock.synchronized {
    val cfgPtr = Native.mkConfig()
    for ((key, value) <- config) Native.setParamValue(cfgPtr, key, value)
    val ptr = Native.mkContextRc(cfgPtr)
    Native.delConfig(cfgPtr)
    ptr
  }

  Z3Wrapper.registerContext(ptr, this)
  Native.setInternalErrorHandler(ptr)

  val astQueue       = new Z3RefCountQueue[Z3ASTLike]
  val astVectorQueue = new Z3RefCountQueue[Z3ASTVector]
  val interpQueue    = new Z3RefCountQueue[Z3FuncInterp]
  val entryQueue     = new Z3RefCountQueue[Z3FuncInterpEntry]
  val modelQueue     = new Z3RefCountQueue[Z3Model]
  val paramsQueue    = new Z3RefCountQueue[Z3Params]
  val solverQueue    = new Z3RefCountQueue[Z3Solver]
  val optimizerQueue = new Z3RefCountQueue[Z3Optimizer]
  val tacticQueue    = new Z3RefCountQueue[Z3Tactic]

  def this(params: (String, Any)*) =
    this(params.map { case (s, a) => (s, a.toString) }.toMap)

  private var deleted : Boolean = false
  override def finalize() : Unit = {
    if(!deleted) this.delete()
  }

  def delete() : Unit = {
    if(!deleted) {
      astQueue.clearQueue()
      modelQueue.clearQueue()
      solverQueue.clearQueue()
      optimizerQueue.clearQueue()
      astVectorQueue.clearQueue()
      tacticQueue.clearQueue()
      interpQueue.clearQueue()
      entryQueue.clearQueue()

      Z3Wrapper.unregisterContext(this.ptr)

      Native.delContext(this.ptr)
      deleted = true
    }
  }

  def onError(code: Long): Nothing = {
    throw new Exception("Unexpected Z3 error (code="+code+")")
  }

  @deprecated("Use interrupt instead", "")
  def softCheckCancel() : Unit = {
    Native.interrupt(this.ptr)
  }

  def astToString(ast: Z3AST) : String = {
    Native.astToString(this.ptr, ast.ptr)
  }

  def funcDeclToString(funcDecl: Z3FuncDecl) : String = {
    Native.funcDeclToString(this.ptr, funcDecl.ptr)
  }

  def sortToString(sort: Z3Sort) : String = {
    Native.sortToString(this.ptr, sort.ptr)
  }

  def patternToString(pattern: Z3Pattern) : String = {
    Native.patternToString(this.ptr, pattern.ptr)
  }

  def modelToString(model: Z3Model) : String = {
    Native.modelToString(this.ptr, model.ptr)
  }

  def benchmarkToSMTLIBString(name : String, logic : String, status : String, attributes : String, assumptions : Seq[Z3AST], formula : Z3AST) : String = {
    Native.benchmarkToSmtlibString(this.ptr, name, logic, status, attributes, assumptions.size, toPtrArray(assumptions), formula.ptr)
  }

  def updateParamValue(paramID: String, paramValue: String) : Unit = {
    Native.updateParamValue(this.ptr, paramID, paramValue)
  }

  private val usedIntSymbols : MutableSet[Int] = MutableSet.empty
  private var lastUsed : Int = -1

  def mkSymbol(i: Int) : Z3Symbol = mkIntSymbol(i)
  def mkSymbol(s: String) : Z3Symbol = mkStringSymbol(s)

  def mkIntSymbol(i: Int) : Z3Symbol = {
    usedIntSymbols += i
    new Z3Symbol(Native.mkIntSymbol(this.ptr, i), this)
  }

  def mkFreshIntSymbol : Z3Symbol = {
    var i = lastUsed + 1
    while(usedIntSymbols(i)) {
      i += 1
    }
    lastUsed = i
    mkIntSymbol(i)
  }

  private val usedStringSymbols : MutableSet[String] = MutableSet.empty
  def mkStringSymbol(s: String) : Z3Symbol = {
    usedStringSymbols += s
    new Z3Symbol(Native.mkStringSymbol(this.ptr, s), this)
  }

  def mkFreshStringSymbol(s: String) : Z3Symbol = {
    if(!usedStringSymbols(s)) {
      mkStringSymbol(s)
    } else {
      var i = 0
      while(usedStringSymbols(s + i)) {
        i += 1
      }
      mkStringSymbol(s + i)
    }
  }

  def isEqSort(s1: Z3Sort, s2: Z3Sort) : Boolean = {
    Native.isEqSort(this.ptr, s1.ptr, s2.ptr)
  }

  def mkUninterpretedSort(s: Z3Symbol) : Z3Sort = {
    new Z3Sort(Native.mkUninterpretedSort(this.ptr, s.ptr), this)
  }

  def mkUninterpretedSort(s : String) : Z3Sort = {
    mkUninterpretedSort(mkStringSymbol(s))
  }

  def mkBoolSort() : Z3Sort = {
    new Z3Sort(Native.mkBoolSort(this.ptr), this)
  }

  def mkIntSort() : Z3Sort = {
    new Z3Sort(Native.mkIntSort(this.ptr), this)
  }

  def mkRealSort() : Z3Sort = {
    new Z3Sort(Native.mkRealSort(this.ptr), this)
  }

  import Z3Context.{ADTSortReference,RecursiveType,RegularSort}

  def mkADTSorts(defs: Seq[(String, Seq[String], Seq[Seq[(String,ADTSortReference)]])]) : Seq[(Z3Sort, Seq[Z3FuncDecl], Seq[Z3FuncDecl], Seq[Seq[Z3FuncDecl]])] = {
    val typeCount: Int = defs.size

    // the following big block builds the following three lists
    var symbolList:   List[Z3Symbol] = Nil
    var consListList: List[Long] = Nil
    var consScalaList: List[List[(Long,Int)]] = Nil // in the Scala list, we maintain number of fields

    for(tuple <- defs) yield {
      val (typeName, typeConstructorNames, typeConstructorArgs) = tuple
      val constructorCount: Int = typeConstructorNames.size
      if(constructorCount != typeConstructorArgs.size) {
        throw new IllegalArgumentException("sequence of constructor names should have the same size as sequence of constructor param lists, for type " + typeName)
      }

      val sym: Z3Symbol = mkStringSymbol(typeName)
      symbolList = sym :: symbolList

      val constructors = (for((tcn, tca) <- (typeConstructorNames zip typeConstructorArgs)) yield {
        val consSym: Z3Symbol = mkStringSymbol(tcn)
        val testSym: Z3Symbol = mkStringSymbol("is" + tcn)
        val fieldSyms: Array[Long] = tca.map(p => mkStringSymbol(p._1).ptr).toArray
        val fieldSorts: Array[Long] = tca.map(p => p._2 match {
          case RecursiveType(idx) if idx >= typeCount => throw new IllegalArgumentException("index of recursive type is too big (" + idx + ") for field " + p._1 + " of type " + typeName)
          case RegularSort(srt) => srt.ptr
          case RecursiveType(_) => 0L
        }).toArray

        val fieldRefs: Array[Int] = tca.map(p => p._2 match {
          case RegularSort(_) => 0
          case RecursiveType(idx) => idx
        }).toArray

        val consPtr = Native.mkConstructor(this.ptr, consSym.ptr, testSym.ptr, fieldSyms.size, fieldSyms, fieldSorts, fieldRefs)
        (consPtr, fieldSyms.size)
      })

      val consArr = constructors.map(_._1).toArray
      val consList = Native.mkConstructorList(this.ptr, consArr.length, consArr)
      consListList = consList :: consListList
      consScalaList = constructors.toList :: consScalaList
    }

    symbolList   = symbolList.reverse
    consListList = consListList.reverse
    consScalaList = consScalaList.reverse

    val newSorts = new Array[Long](typeCount)
    Native.mkDatatypes(this.ptr, typeCount, toPtrArray(symbolList), newSorts, consListList.toArray)

    consListList.foreach(cl => Native.delConstructorList(this.ptr, cl))

    for((sort, consLst) <- (newSorts.toIndexedSeq zip consScalaList)) yield {
      val zipped = for (cons <- consLst) yield {
        val consFunPtr = new Native.LongPtr()
        val testFunPtr = new Native.LongPtr()

        val selectors: Array[Long] = if (cons._2 > 0) new Array[Long](cons._2) else new Array[Long](0)

        Native.queryConstructor(this.ptr, cons._1, cons._2, consFunPtr, testFunPtr, selectors)

        val consFun = new Z3FuncDecl(consFunPtr.value, cons._2, this)
        val testFun = new Z3FuncDecl(testFunPtr.value, 1, this)
        (consFun, (testFun, if(cons._2 > 0) selectors.map(new Z3FuncDecl(_, 1, this)).toList else Nil))
      }

      val (consFuns, unzippedOnce) = zipped.unzip
      val (testFuns, selectorFunss) = unzippedOnce.unzip

      (new Z3Sort(sort, this), consFuns, testFuns, selectorFunss)
    }
  }

  def isEqAST(t1: Z3AST, t2: Z3AST) : Boolean = {
    Native.isEqAst(this.ptr, t1.ptr, t2.ptr)
  }

  def mkApp(funcDecl: Z3FuncDecl, args: Z3AST*) : Z3AST = {
    if(funcDecl.arity != args.size)
      throw new IllegalArgumentException("Calling mkApp with wrong number of arguments.")

    new Z3AST(Native.mkApp(this.ptr, funcDecl.ptr, args.size, toPtrArray(args)), this)
  }

  def isEqFuncDecl(fd1: Z3FuncDecl, fd2: Z3FuncDecl) : Boolean = {
    Native.isEqFuncDecl(this.ptr, fd1.ptr, fd2.ptr)
  }

  def mkConst(symbol: Z3Symbol, sort: Z3Sort) : Z3AST = {
    new Z3AST(Native.mkConst(this.ptr, symbol.ptr, sort.ptr), this)
  }

  def mkConst(s: String, sort: Z3Sort) : Z3AST = {
    mkConst(mkStringSymbol(s), sort)
  }

  def mkIntConst(symbol: Z3Symbol) : Z3AST = {
    mkConst(symbol, mkIntSort())
  }

  def mkIntConst(s: String) : Z3AST = {
    mkIntConst(mkStringSymbol(s))
  }

  def mkBoolConst(symbol: Z3Symbol) : Z3AST = {
    mkConst(symbol, mkBoolSort())
  }

  def mkBoolConst(s: String) : Z3AST = {
    mkBoolConst(mkStringSymbol(s))
  }

  def mkFuncDecl(symbol: Z3Symbol, domainSorts: Seq[Z3Sort], rangeSort: Z3Sort) : Z3FuncDecl = {
    new Z3FuncDecl(Native.mkFuncDecl(this.ptr, symbol.ptr, domainSorts.size, toPtrArray(domainSorts), rangeSort.ptr), domainSorts.size, this)
  }

  def mkFuncDecl(symbol: Z3Symbol, domainSort: Z3Sort, rangeSort: Z3Sort) : Z3FuncDecl = {
    mkFuncDecl(symbol, Seq(domainSort), rangeSort)
  }

  def mkFuncDecl(symbol: String, domainSorts: Seq[Z3Sort], rangeSort: Z3Sort) : Z3FuncDecl = {
    mkFuncDecl(mkStringSymbol(symbol), domainSorts, rangeSort)
  }

  def mkFuncDecl(symbol: String, domainSort: Z3Sort, rangeSort: Z3Sort) : Z3FuncDecl = {
    mkFuncDecl(mkStringSymbol(symbol), Seq(domainSort), rangeSort)
  }

  def mkFreshConst(prefix: String, sort: Z3Sort) : Z3AST = {
    new Z3AST(Native.mkFreshConst(this.ptr, prefix, sort.ptr), this)
  }

  def mkFreshIntConst(prefix: String) : Z3AST = {
    mkFreshConst(prefix, mkIntSort())
  }

  def mkFreshBoolConst(prefix: String) : Z3AST = {
    mkFreshConst(prefix, mkBoolSort())
  }

  def mkFreshFuncDecl(prefix: String, domainSorts: Seq[Z3Sort], rangeSort: Z3Sort) : Z3FuncDecl = {
    new Z3FuncDecl(Native.mkFreshFuncDecl(this.ptr, prefix, domainSorts.size, toPtrArray(domainSorts), rangeSort.ptr), domainSorts.size, this)
  }

  def mkTrue() : Z3AST = {
    new Z3AST(Native.mkTrue(this.ptr), this)
  }

  def mkFalse() : Z3AST = {
    new Z3AST(Native.mkFalse(this.ptr), this)
  }

  def mkEq(ast1: Z3AST, ast2: Z3AST) : Z3AST = {
    new Z3AST(Native.mkEq(this.ptr, ast1.ptr, ast2.ptr), this)
  }

  def mkDistinct(args: Z3AST*) : Z3AST = {
    if(args.size == 0) {
      throw new IllegalArgumentException("mkDistinct needs at least one argument")
    } else if(args.size == 1) {
      mkTrue()
    } else {
      new Z3AST(Native.mkDistinct(this.ptr, args.length, toPtrArray(args)), this)
    }
  }

  def mkNot(ast: Z3AST) : Z3AST = {
    new Z3AST(Native.mkNot(this.ptr, ast.ptr), this)
  }

  def mkITE(t1: Z3AST, t2: Z3AST, t3: Z3AST) : Z3AST = {
    new Z3AST(Native.mkIte(this.ptr, t1.ptr, t2.ptr, t3.ptr), this)
  }

  def mkIff(t1: Z3AST, t2: Z3AST) : Z3AST = {
    new Z3AST(Native.mkIff(this.ptr, t1.ptr, t2.ptr), this)
  }

  def mkImplies(t1: Z3AST, t2: Z3AST) : Z3AST = {
    new Z3AST(Native.mkImplies(this.ptr, t1.ptr, t2.ptr), this)
  }

  def mkXor(t1: Z3AST, t2: Z3AST) : Z3AST = {
    new Z3AST(Native.mkXor(this.ptr, t1.ptr, t2.ptr), this)
  }

  def mkAnd(args: Z3AST*) : Z3AST = {
    if(args.size == 0) {
      throw new IllegalArgumentException("mkAnd needs at least one argument")
    } else if(args.size == 1) {
      new Z3AST(args(0).ptr, this)
    } else {
      new Z3AST(Native.mkAnd(this.ptr, args.length, toPtrArray(args)), this)
    }
  }

  def mkOr(args: Z3AST*) : Z3AST = {
    if(args.size == 0) {
      throw new IllegalArgumentException("mkOr needs at least one argument")
    } else if(args.size == 1) {
      new Z3AST(args(0).ptr, this)
    } else {
      new Z3AST(Native.mkOr(this.ptr, args.length, toPtrArray(args)), this)
    }
  }

  def mkAdd(args: Z3AST*) : Z3AST = {
    if(args.size == 0) {
      throw new IllegalArgumentException("mkAdd needs at least one argument")
    } else if(args.size == 1) {
      new Z3AST(args(0).ptr, this)
    } else {
      new Z3AST(Native.mkAdd(this.ptr, args.length, toPtrArray(args)), this)
    }
  }

  def mkMul(args: Z3AST*) : Z3AST = {
    if(args.size == 0) {
      throw new IllegalArgumentException("mkMul needs at least one argument")
    } else if(args.size == 1) {
      new Z3AST(args(0).ptr, this)
    } else {
      new Z3AST(Native.mkMul(this.ptr, args.length, toPtrArray(args)), this)
    }
  }

  def mkSub(args: Z3AST*) : Z3AST = {
    if(args.size == 0) {
      throw new IllegalArgumentException("mkSub needs at least one argument")
    } else if(args.size == 1) {
      new Z3AST(args(0).ptr, this)
    } else {
      new Z3AST(Native.mkSub(this.ptr, args.length, toPtrArray(args)), this)
    }
  }

  def mkUnaryMinus(ast: Z3AST) : Z3AST = {
    new Z3AST(Native.mkUnaryMinus(this.ptr, ast.ptr), this)
  }

  def mkDiv(ast1: Z3AST, ast2: Z3AST) : Z3AST = {
    new Z3AST(Native.mkDiv(this.ptr, ast1.ptr, ast2.ptr), this)
  }

  def mkMod(ast1: Z3AST, ast2: Z3AST) : Z3AST = {
    new Z3AST(Native.mkMod(this.ptr, ast1.ptr, ast2.ptr), this)
  }

  def mkRem(ast1: Z3AST, ast2: Z3AST) : Z3AST = {
    new Z3AST(Native.mkRem(this.ptr, ast1.ptr, ast2.ptr), this)
  }

  def mkPower(ast1: Z3AST, ast2: Z3AST) : Z3AST = {
    new Z3AST(Native.mkPower(this.ptr, ast1.ptr, ast2.ptr), this)
  }

  def mkLT(ast1: Z3AST, ast2: Z3AST) : Z3AST = {
    new Z3AST(Native.mkLt(this.ptr, ast1.ptr, ast2.ptr), this)
  }

  def mkLE(ast1: Z3AST, ast2: Z3AST) : Z3AST = {
    new Z3AST(Native.mkLe(this.ptr, ast1.ptr, ast2.ptr), this)
  }

  def mkGT(ast1: Z3AST, ast2: Z3AST) : Z3AST = {
    new Z3AST(Native.mkGt(this.ptr, ast1.ptr, ast2.ptr), this)
  }

  def mkGE(ast1: Z3AST, ast2: Z3AST) : Z3AST = {
    new Z3AST(Native.mkGe(this.ptr, ast1.ptr, ast2.ptr), this)
  }

  def mkInt2Real(ast: Z3AST) : Z3AST = {
    new Z3AST(Native.mkInt2real(this.ptr, ast.ptr), this)
  }

  def mkAbs(ast: Z3AST) : Z3AST = {
    new Z3AST(Native.mkAbs(this.ptr, ast.ptr), this)
  }

  def mkReal2Int(ast: Z3AST) : Z3AST = {
    new Z3AST(Native.mkReal2int(this.ptr, ast.ptr), this)
  }

  def mkIsInt(ast: Z3AST) : Z3AST = {
    new Z3AST(Native.mkIsInt(this.ptr, ast.ptr), this)
  }

  def mkArraySort(domain: Z3Sort, range: Z3Sort) : Z3Sort = {
    new Z3Sort(Native.mkArraySort(this.ptr, domain.ptr, range.ptr), this)
  }

  def mkSelect(array: Z3AST, index: Z3AST) : Z3AST = {
    new Z3AST(Native.mkSelect(this.ptr, array.ptr, index.ptr), this)
  }

  def mkStore(array: Z3AST, index: Z3AST, value: Z3AST) : Z3AST = {
    new Z3AST(Native.mkStore(this.ptr, array.ptr, index.ptr, value.ptr), this)
  }

  def mkConstArray(sort: Z3Sort, value: Z3AST) : Z3AST = {
    new Z3AST(Native.mkConstArray(this.ptr, sort.ptr, value.ptr), this)
  }

  def mkArrayMap(f: Z3FuncDecl, args: Z3AST*): Z3AST = {
    new Z3AST(Native.mkMap(this.ptr, f.ptr, args.size, args.map(_.ptr).toArray), this)
  }

  def mkArrayMap(op: Z3DeclKind, args: Z3AST*): Z3AST = {
    mkArrayMap(getFuncDecl(op), args : _*)
  }

  def mkArrayDefault(array: Z3AST) : Z3AST = {
    new Z3AST(Native.mkArrayDefault(this.ptr, array.ptr), this)
  }

  def mkTupleSort(name: Z3Symbol, sorts: Z3Sort*) : (Z3Sort,Z3FuncDecl,Seq[Z3FuncDecl]) = {
    require(sorts.size > 0)
    val sz = sorts.size
    val consPtr = new Native.LongPtr()
    val projFuns = new Array[Long](sz)
    val fieldNames = sorts.map(s => mkFreshStringSymbol(s"$name-field")).toArray
    val sortPtr = Native.mkTupleSort(this.ptr, name.ptr, sz, fieldNames.map(_.ptr), sorts.map(_.ptr).toArray, consPtr, projFuns)
    val newSort = new Z3Sort(sortPtr, this)
    val consFuncDecl = new Z3FuncDecl(consPtr.value, sz, this)
    val projFuncDecls = projFuns.map(ptr => new Z3FuncDecl(ptr, 1, this)).toSeq
    (newSort, consFuncDecl, projFuncDecls)
  }

  def mkTupleSort(name : String, sorts : Z3Sort*) : (Z3Sort,Z3FuncDecl,Seq[Z3FuncDecl]) = mkTupleSort(mkStringSymbol(name), sorts : _*)

  def mkSetSort(underlying: Z3Sort) : Z3Sort = {
    new Z3Sort(Native.mkSetSort(this.ptr, underlying.ptr), this)
  }

  def mkEmptySet(sort: Z3Sort) : Z3AST = {
    new Z3AST(Native.mkEmptySet(this.ptr, sort.ptr), this)
  }

  def mkFullSet(sort: Z3Sort) : Z3AST = {
    new Z3AST(Native.mkFullSet(this.ptr, sort.ptr), this)
  }

  def mkSetAdd(set: Z3AST, elem: Z3AST) : Z3AST = {
    new Z3AST(Native.mkSetAdd(this.ptr, set.ptr, elem.ptr), this)
  }

  def mkSetDel(set: Z3AST, elem: Z3AST) : Z3AST = {
    new Z3AST(Native.mkSetDel(this.ptr, set.ptr, elem.ptr), this)
  }

  def mkSetUnion(args: Z3AST*) : Z3AST = {
    if(args.size == 0) {
      throw new IllegalArgumentException("mkSetUnion needs at least one argument")
    } else if(args.size == 1) {
      new Z3AST(args(0).ptr, this)
    } else {
      new Z3AST(Native.mkSetUnion(this.ptr, args.length, toPtrArray(args)), this)
    }
  }

  def mkSetIntersect(args: Z3AST*) : Z3AST = {
    if(args.size == 0) {
      throw new IllegalArgumentException("mkSetIntersect needs at least one argument")
    } else if(args.size == 1) {
      new Z3AST(args(0).ptr, this)
    } else {
      new Z3AST(Native.mkSetIntersect(this.ptr, args.length, toPtrArray(args)), this)
    }
  }

  def mkSetDifference(ast1: Z3AST, ast2: Z3AST) : Z3AST = {
    new Z3AST(Native.mkSetDifference(this.ptr, ast1.ptr, ast2.ptr), this)
  }

  def mkSetComplement(ast: Z3AST) : Z3AST = {
    new Z3AST(Native.mkSetComplement(this.ptr, ast.ptr), this)
  }

  def mkSetMember(elem: Z3AST, set: Z3AST) : Z3AST = {
    new Z3AST(Native.mkSetMember(this.ptr, elem.ptr, set.ptr), this)
  }

  def mkSetSubset(ast1: Z3AST, ast2: Z3AST) : Z3AST = {
    new Z3AST(Native.mkSetSubset(this.ptr, ast1.ptr, ast2.ptr), this)
  }

  def mkSeqSort(underlying: Z3Sort) : Z3Sort = {
    new Z3Sort(Native.mkSeqSort(this.ptr, underlying.ptr), this)
  }

  def mkEmptySeq(sort: Z3Sort): Z3AST = {
    new Z3AST(Native.mkSeqEmpty(this.ptr, sort.ptr), this)
  }

  def mkUnitSeq(e: Z3AST): Z3AST = {
    new Z3AST(Native.mkSeqUnit(this.ptr, e.ptr), this)
  }

  def mkSeqConcat(args: Z3AST*): Z3AST = {
    new Z3AST(Native.mkSeqConcat(this.ptr, args.size, toPtrArray(args)), this)
  }

  def mkSeqLength(e: Z3AST): Z3AST = {
    new Z3AST(Native.mkSeqLength(this.ptr, e.ptr), this)
  }

  def mkSeqPrefix(seq1: Z3AST, seq2: Z3AST): Z3AST = {
    new Z3AST(Native.mkSeqPrefix(this.ptr, seq1.ptr, seq2.ptr), this)
  }

  def mkSeqSuffix(seq1: Z3AST, seq2: Z3AST): Z3AST = {
    new Z3AST(Native.mkSeqSuffix(this.ptr, seq1.ptr, seq2.ptr), this)
  }

  def mkSeqContains(seq1: Z3AST, seq2: Z3AST): Z3AST = {
    new Z3AST(Native.mkSeqContains(this.ptr, seq1.ptr, seq2.ptr), this)
  }

  def mkSeqAt(seq: Z3AST, i: Z3AST): Z3AST = {
    new Z3AST(Native.mkSeqAt(this.ptr, seq.ptr, i.ptr), this)
  }

  def mkSeqExtract(seq: Z3AST, start: Z3AST, length: Z3AST): Z3AST = {
    new Z3AST(Native.mkSeqExtract(this.ptr, seq.ptr, start.ptr, length.ptr), this)
  }

  def mkSeqIndexOf(seq: Z3AST, sub: Z3AST, offset: Z3AST): Z3AST = {
    new Z3AST(Native.mkSeqIndex(this.ptr, seq.ptr, sub.ptr, offset.ptr), this)
  }

  def mkSeqReplace(seq: Z3AST, src: Z3AST, dest: Z3AST): Z3AST = {
    new Z3AST(Native.mkSeqReplace(this.ptr, seq.ptr, src.ptr, dest.ptr), this)
  }

  def mkSeqToRe(seq: Z3AST): Z3AST = {
    new Z3AST(Native.mkSeqToRe(this.ptr, seq.ptr), this)
  }

  def mkSeqInRe(seq: Z3AST, re: Z3AST): Z3AST = {
    new Z3AST(Native.mkSeqInRe(this.ptr, seq.ptr, re.ptr), this)
  }

  def mkReStar(re: Z3AST): Z3AST = {
    new Z3AST(Native.mkReStar(this.ptr, re.ptr), this)
  }

  def mkRePlus(re: Z3AST): Z3AST = {
    new Z3AST(Native.mkRePlus(this.ptr, re.ptr), this)
  }

  def mkReOption(re: Z3AST): Z3AST = {
    new Z3AST(Native.mkReOption(this.ptr, re.ptr), this)
  }

  def mkReConcat(args: Z3AST*): Z3AST = {
    new Z3AST(Native.mkReConcat(this.ptr, args.size, toPtrArray(args)), this)
  }

  def mkReUnion(args: Z3AST*): Z3AST = {
    new Z3AST(Native.mkReUnion(this.ptr, args.size, toPtrArray(args)), this)
  }

  def mkStringSort(): Z3AST = {
    new Z3AST(Native.mkStringSort(this.ptr), this)
  }

  def mkString(s: String): Z3AST = {
    new Z3AST(Native.mkString(this.ptr, s), this)
  }

  def getString(ast: Z3AST): String = {
    Native.getString(this.ptr, ast.ptr)
  }

  def mkInt(value: Int, sort: Z3Sort) : Z3AST = {
    new Z3AST(Native.mkInt(this.ptr, value, sort.ptr), this)
  }

  def mkReal(numerator: Int, denominator: Int) : Z3AST = {
    new Z3AST(Native.mkReal(this.ptr, numerator, denominator), this)
  }

  def mkNumeral(value: String, sort: Z3Sort) : Z3AST = {
    new Z3AST(Native.mkNumeral(this.ptr, value, sort.ptr), this)
  }

  def mkPattern(args: Z3AST*) : Z3Pattern = {
    new Z3Pattern(Native.mkPattern(this.ptr, args.size, toPtrArray(args)), this)
  }

  def mkBound(index: Int, sort: Z3Sort) : Z3AST = {
    new Z3AST(Native.mkBound(this.ptr, index, sort.ptr), this)
  }

  def mkForall(weight: Int, patterns: Seq[Z3Pattern], decls: Seq[(Z3Symbol,Z3Sort)], body: Z3AST) : Z3AST = mkQuantifier(true, weight, patterns, decls, body)

  def mkExists(weight: Int, patterns: Seq[Z3Pattern], decls: Seq[(Z3Symbol,Z3Sort)], body: Z3AST) : Z3AST = mkQuantifier(false, weight, patterns, decls, body)

  def mkQuantifier(isForAll: Boolean, weight: Int, patterns: Seq[Z3Pattern], decls: Seq[(Z3Symbol,Z3Sort)], body: Z3AST) : Z3AST = {
    val (declSyms, declSorts) = decls.unzip
    new Z3AST(
      Native.mkQuantifier(
        this.ptr,
        isForAll,
        weight,
        patterns.size,
        toPtrArray(patterns),
        decls.size,
        toPtrArray(declSorts),
        toPtrArray(declSyms),
        body.ptr),
      this
    )
  }

  def mkBVSort(size: Int) : Z3Sort = {
    new Z3Sort(Native.mkBvSort(this.ptr, size), this)
  }

  def mkInt2BV(size: Int, ast: Z3AST) : Z3AST = {
    new Z3AST(Native.mkInt2bv(this.ptr, size, ast.ptr), this)
  }

  def mkBV2Int(ast: Z3AST, isSigned: Boolean) : Z3AST = {
    new Z3AST(Native.mkBv2int(this.ptr, ast.ptr, isSigned), this)
  }

  def mkBVNot(ast: Z3AST) : Z3AST = {
    new Z3AST(Native.mkBvnot(this.ptr, ast.ptr), this)
  }

  def mkBVRedAnd(ast: Z3AST) : Z3AST = {
    new Z3AST(Native.mkBvredand(this.ptr, ast.ptr), this)
  }

  def mkBVRedOr(ast: Z3AST) : Z3AST = {
    new Z3AST(Native.mkBvredor(this.ptr, ast.ptr), this)
  }

  def mkBVAnd(ast1: Z3AST, ast2: Z3AST) : Z3AST = {
    new Z3AST(Native.mkBvand(this.ptr, ast1.ptr, ast2.ptr), this)
  }

  def mkBVOr(ast1: Z3AST, ast2: Z3AST) : Z3AST = {
    new Z3AST(Native.mkBvor(this.ptr, ast1.ptr, ast2.ptr), this)
  }

  def mkBVXor(ast1: Z3AST, ast2: Z3AST) : Z3AST = {
    new Z3AST(Native.mkBvxor(this.ptr, ast1.ptr, ast2.ptr), this)
  }

  def mkBVNand(ast1: Z3AST, ast2: Z3AST) : Z3AST = {
    new Z3AST(Native.mkBvnand(this.ptr, ast1.ptr, ast2.ptr), this)
  }

  def mkBVNor(ast1: Z3AST, ast2: Z3AST) : Z3AST = {
    new Z3AST(Native.mkBvnor(this.ptr, ast1.ptr, ast2.ptr), this)
  }

  def mkBVXnor(ast1: Z3AST, ast2: Z3AST) : Z3AST = {
    new Z3AST(Native.mkBvxnor(this.ptr, ast1.ptr, ast2.ptr), this)
  }

  def mkBVNeg(ast: Z3AST) : Z3AST = {
    new Z3AST(Native.mkBvneg(this.ptr, ast.ptr), this)
  }

  def mkBVAdd(ast1: Z3AST, ast2: Z3AST) : Z3AST = {
    new Z3AST(Native.mkBvadd(this.ptr, ast1.ptr, ast2.ptr), this)
  }

  def mkBVSub(ast1: Z3AST, ast2: Z3AST) : Z3AST = {
    new Z3AST(Native.mkBvsub(this.ptr, ast1.ptr, ast2.ptr), this)
  }

  def mkBVMul(ast1: Z3AST, ast2: Z3AST) : Z3AST = {
    new Z3AST(Native.mkBvmul(this.ptr, ast1.ptr, ast2.ptr), this)
  }

  def mkBVUdiv(ast1: Z3AST, ast2: Z3AST) : Z3AST = {
    new Z3AST(Native.mkBvudiv(this.ptr, ast1.ptr, ast2.ptr), this)
  }

  def mkBVSdiv(ast1: Z3AST, ast2: Z3AST) : Z3AST = {
    new Z3AST(Native.mkBvsdiv(this.ptr, ast1.ptr, ast2.ptr), this)
  }

  def mkBVUrem(ast1: Z3AST, ast2: Z3AST) : Z3AST = {
    new Z3AST(Native.mkBvurem(this.ptr, ast1.ptr, ast2.ptr), this)
  }

  def mkBVSrem(ast1: Z3AST, ast2: Z3AST) : Z3AST = {
    new Z3AST(Native.mkBvsrem(this.ptr, ast1.ptr, ast2.ptr), this)
  }

  def mkBVSmod(ast1: Z3AST, ast2: Z3AST) : Z3AST = {
    new Z3AST(Native.mkBvsmod(this.ptr, ast1.ptr, ast2.ptr), this)
  }

  def mkBVUlt(ast1: Z3AST, ast2: Z3AST) : Z3AST = {
    new Z3AST(Native.mkBvult(this.ptr, ast1.ptr, ast2.ptr), this)
  }

  def mkBVSlt(ast1: Z3AST, ast2: Z3AST) : Z3AST = {
    new Z3AST(Native.mkBvslt(this.ptr, ast1.ptr, ast2.ptr), this)
  }

  def mkBVUle(ast1: Z3AST, ast2: Z3AST) : Z3AST = {
    new Z3AST(Native.mkBvule(this.ptr, ast1.ptr, ast2.ptr), this)
  }

  def mkBVSle(ast1: Z3AST, ast2: Z3AST) : Z3AST = {
    new Z3AST(Native.mkBvsle(this.ptr, ast1.ptr, ast2.ptr), this)
  }

  def mkBVUgt(ast1: Z3AST, ast2: Z3AST) : Z3AST = {
    new Z3AST(Native.mkBvugt(this.ptr, ast1.ptr, ast2.ptr), this)
  }

  def mkBVSgt(ast1: Z3AST, ast2: Z3AST) : Z3AST = {
    new Z3AST(Native.mkBvsgt(this.ptr, ast1.ptr, ast2.ptr), this)
  }

  def mkBVUge(ast1: Z3AST, ast2: Z3AST) : Z3AST = {
    new Z3AST(Native.mkBvuge(this.ptr, ast1.ptr, ast2.ptr), this)
  }

  def mkBVSge(ast1: Z3AST, ast2: Z3AST) : Z3AST = {
    new Z3AST(Native.mkBvsge(this.ptr, ast1.ptr, ast2.ptr), this)
  }

  def mkConcat(ast1: Z3AST, ast2: Z3AST) : Z3AST = {
    new Z3AST(Native.mkConcat(this.ptr, ast1.ptr, ast2.ptr), this)
  }

  def mkExtract(high: Int, low: Int, ast: Z3AST) : Z3AST = {
    new Z3AST(Native.mkExtract(this.ptr, high, low, ast.ptr), this)
  }

  def mkSignExt(extraSize: Int, ast: Z3AST) : Z3AST = {
    new Z3AST(Native.mkSignExt(this.ptr, extraSize, ast.ptr), this)
  }

  def mkZeroExt(extraSize: Int, ast: Z3AST) : Z3AST = {
    new Z3AST(Native.mkZeroExt(this.ptr, extraSize, ast.ptr), this)
  }

  def mkRepeat(count: Int, ast: Z3AST) : Z3AST = {
    new Z3AST(Native.mkRepeat(this.ptr, count, ast.ptr), this)
  }

  def mkBVShl(ast1: Z3AST, ast2: Z3AST) : Z3AST = {
    new Z3AST(Native.mkBvshl(this.ptr, ast1.ptr, ast2.ptr), this)
  }

  def mkBVLshr(ast1: Z3AST, ast2: Z3AST) : Z3AST = {
    new Z3AST(Native.mkBvlshr(this.ptr, ast1.ptr, ast2.ptr), this)
  }

  def mkBVAshr(ast1: Z3AST, ast2: Z3AST) : Z3AST = {
    new Z3AST(Native.mkBvashr(this.ptr, ast1.ptr, ast2.ptr), this)
  }

  def mkExtRotateLeft(ast1: Z3AST, ast2: Z3AST) : Z3AST = {
    new Z3AST(Native.mkExtRotateLeft(this.ptr, ast1.ptr, ast2.ptr), this)
  }

  def mkExtRotateRight(ast1: Z3AST, ast2: Z3AST) : Z3AST = {
    new Z3AST(Native.mkExtRotateRight(this.ptr, ast1.ptr, ast2.ptr), this)
  }

  def mkBVAddNoOverflow(ast1: Z3AST, ast2: Z3AST, isSigned: Boolean) : Z3AST = {
    new Z3AST(Native.mkBvaddNoOverflow(this.ptr, ast1.ptr, ast2.ptr, isSigned), this)
  }

  def mkBVAddNoUnderflow(ast1: Z3AST, ast2: Z3AST) : Z3AST = {
    new Z3AST(Native.mkBvaddNoUnderflow(this.ptr, ast1.ptr, ast2.ptr), this)
  }

  def mkBVSubNoOverflow(ast1: Z3AST, ast2: Z3AST) : Z3AST = {
    new Z3AST(Native.mkBvsubNoOverflow(this.ptr, ast1.ptr, ast2.ptr), this)
  }

  def mkBVSubNoUnderflow(ast1: Z3AST, ast2: Z3AST, isSigned: Boolean) : Z3AST = {
    new Z3AST(Native.mkBvsubNoUnderflow(this.ptr, ast1.ptr, ast2.ptr, isSigned), this)
  }

  def mkBVSDivNoOverflow(ast1: Z3AST, ast2: Z3AST) : Z3AST = {
    new Z3AST(Native.mkBvsdivNoOverflow(this.ptr, ast1.ptr, ast2.ptr), this)
  }

  def mkBVNegNoOverflow(ast: Z3AST) : Z3AST = {
    new Z3AST(Native.mkBvnegNoOverflow(this.ptr, ast.ptr), this)
  }

  def mkBVMulNoOverflow(ast1: Z3AST, ast2: Z3AST, isSigned: Boolean) : Z3AST = {
    new Z3AST(Native.mkBvmulNoOverflow(this.ptr, ast1.ptr, ast2.ptr, isSigned), this)
  }

  def mkBVMulNoUnderflow(ast1: Z3AST, ast2: Z3AST) : Z3AST = {
    new Z3AST(Native.mkBvmulNoUnderflow(this.ptr, ast1.ptr, ast2.ptr), this)
  }

  def getFuncDecl(op: Z3DeclKind, sorts: Z3Sort*): Z3FuncDecl = {
    val app = op match {
      case OpAdd => mkAdd(sorts.map(mkFreshConst("x", _)) : _*)
      case OpSub => mkSub(sorts.map(mkFreshConst("x", _)) : _*)
      case OpUMinus => mkUnaryMinus(mkFreshConst("x", sorts(0)))
      case OpMul => mkMul(sorts.map(mkFreshConst("x", _)) : _*)
      case OpDiv => mkDiv(mkFreshConst("a", sorts(0)), mkFreshConst("b", sorts(1)))
      case OpAbs => mkAbs(mkFreshConst("x", sorts(0)))
      case _ => error("Unexpected decl kind " + op)
    }

    getASTKind(app) match {
      case Z3AppAST(decl, _) => decl
      case _ => error("Unexpected non-app AST " + app)
    }
  }

  def getSymbolKind(symbol: Z3Symbol) : Z3SymbolKind[_] = {
    Native.getSymbolKind(this.ptr, symbol.ptr) match {
      case 0 => Z3IntSymbol(getSymbolInt(symbol))
      case 1 => Z3StringSymbol(getSymbolString(symbol))
      case other => error("Z3_get_symbol_kind returned an unknown value : " + other)
    }
  }

  private[z3] def getSymbolInt(symbol: Z3Symbol) : Int = {
    Native.getSymbolInt(this.ptr, symbol.ptr)
  }

  private[z3] def getSymbolString(symbol: Z3Symbol) : String = {
    Native.getSymbolString(this.ptr, symbol.ptr)
  }

  def getASTKind(ast: Z3AST) : Z3ASTKind = {
    Z3_ast_kind.fromInt(Native.getAstKind(this.ptr, ast.ptr)) match {
      case Z3_ast_kind.Z3_NUMERAL_AST =>
        if (getSort(ast).isRealSort) getNumeralReal(ast)
        else getNumeralInt(ast)

      case Z3_ast_kind.Z3_APP_AST =>
        val numArgs = getAppNumArgs(ast)
        val args = (Seq.tabulate(numArgs)){ i => getAppArg(ast, i) }
        Z3AppAST(getAppDecl(ast, numArgs), args)

      case Z3_ast_kind.Z3_VAR_AST =>
        val index = getIndexValue(ast)
        Z3VarAST(index)

      case Z3_ast_kind.Z3_QUANTIFIER_AST =>
        val body = getQuantifierBody(ast)
        val forall = isQuantifierForall(ast)
        val numVars = getQuantifierNumBound(ast)
        val varSymbols = (0 to numVars-1).map(getQuantifierBoundName(ast, _))
        val varNames = varSymbols.map(x => getSymbolKind(x) match {
          case Z3IntSymbol(x) => "#" + x.toString()
          case Z3StringSymbol(s) => s
        })
        Z3QuantifierAST(forall, varNames, body)

      case Z3_ast_kind.Z3_SORT_AST =>
        Z3SortAST(getSort(ast))

      case Z3_ast_kind.Z3_FUNC_DECL_AST =>
        val arity = Native.getDomainSize(this.ptr, ast.ptr)
        Z3FuncDeclAST(new Z3FuncDecl(ast.ptr, arity, this))

      case _ => Z3UnknownAST
    }
  }

  def getIndexValue(ast: Z3AST) : Int = {
    Native.getIndexValue(this.ptr, ast.ptr)
  }

  def isQuantifierForall(ast: Z3AST) : Boolean = {
    Native.isQuantifierForall(this.ptr, ast.ptr)
  }

  def getQuantifierBody(ast: Z3AST) : Z3AST = {
    new Z3AST(Native.getQuantifierBody(this.ptr, ast.ptr), this)
  }

  def getQuantifierBoundName(ast: Z3AST, i: Int) : Z3Symbol = {
    new Z3Symbol(Native.getQuantifierBoundName(this.ptr, ast.ptr, i), this)
  }

  def getQuantifierNumBound(ast: Z3AST) : Int = {
    Native.getQuantifierNumBound(this.ptr, ast.ptr)
  }

  def getDeclKind(funcDecl: Z3FuncDecl) : Z3DeclKind = {
    val kind = Native.getDeclKind(this.ptr, funcDecl.ptr)
    Z3DeclKind.fromInt(kind)
  }

  def getAppDecl(ast: Z3AST, arity: Int = -1) : Z3FuncDecl = {
    val ad = Native.getAppDecl(this.ptr, ast.ptr)
    val ary = if(arity > -1) arity else Native.getDomainSize(this.ptr, ad)
    new Z3FuncDecl(ad, ary, this)
  }

  private def getAppNumArgs(ast: Z3AST) : Int = {
    Native.getAppNumArgs(this.ptr, ast.ptr)
  }

  private def getAppArg(ast: Z3AST, i: Int) : Z3AST = {
    new Z3AST(Native.getAppArg(this.ptr, ast.ptr, i), this)
  }

  def getDeclName(fd: Z3FuncDecl) : Z3Symbol = {
    new Z3Symbol(Native.getDeclName(this.ptr, fd.ptr), this)
  }

  // TODO arity
  def getDeclFuncDeclParameter(fd: Z3FuncDecl, idx: Int, arity: Int = 1) : Z3FuncDecl = {
    new Z3FuncDecl(Native.getDeclFuncDeclParameter(this.ptr, fd.ptr, idx), arity, this)
  }

  def getSort(ast: Z3AST) : Z3Sort = {
    new Z3Sort(Native.getSort(this.ptr, ast.ptr), this)
  }

  def getDomainSize(funcDecl: Z3FuncDecl) : Int = funcDecl.arity

  def getDomain(funcDecl: Z3FuncDecl, i: Int) : Z3Sort = {
    if(funcDecl.arity <= i)
      throw new IllegalArgumentException("Calling getDomain with too large index.")

    new Z3Sort(Native.getDomain(this.ptr, funcDecl.ptr, i), this)
  }

  def getRange(funcDecl: Z3FuncDecl) : Z3Sort = {
    new Z3Sort(Native.getRange(this.ptr, funcDecl.ptr), this)
  }

  def getNumeralInt(ast: Z3AST) : Z3NumeralIntAST = {
    val ip = new Native.IntPtr
    val res = Native.getNumeralInt(this.ptr, ast.ptr, ip)
    if(res)
      Z3NumeralIntAST(Some(ip.value))
    else
      Z3NumeralIntAST(None)
  }

  def getNumeralReal(ast: Z3AST) : Z3NumeralRealAST = {
    val numZ3AST = new Z3AST(Native.getNumerator(this.ptr, ast.ptr), this)
    val denZ3AST = new Z3AST(Native.getDenominator(this.ptr, ast.ptr), this)
    val num = new BigInt(new BigInteger(Native.getNumeralString(this.ptr, numZ3AST.ptr)))
    val den = new BigInt(new BigInteger(Native.getNumeralString(this.ptr, denZ3AST.ptr)))
    Z3NumeralRealAST(num, den)
  }

  def getBoolValue(ast: Z3AST) : Option[Boolean] = {
    val res = i2ob(Native.getBoolValue(this.ptr, ast.ptr))
    res
  }

  def getAbsFuncDecl(): Z3FuncDecl = {
    getFuncDecl(OpAbs, mkIntSort())
  }

  // Parser interface
  private def parseSMTLIB2(file: Boolean, str: String) : Z3AST = {
    if(file) {
      new Z3AST(Native.parseSmtlib2File(this.ptr, str, 0, null, null, 0, null, null), this)
    } else {
      new Z3AST(Native.parseSmtlib2String(this.ptr, str, 0, null, null, 0, null, null), this)
    }
  }

  private def parseSMTLIB2(file: Boolean, str: String, sorts: Map[Z3Symbol,Z3Sort], decls: Map[Z3Symbol,Z3FuncDecl]) : Z3AST = {
    val (sortNames, z3Sorts) = sorts.unzip
    val (declNames, z3Decls) = decls.unzip
    if(file) {
      new Z3AST(Native.parseSmtlib2File(this.ptr, str, sorts.size, toPtrArray(sortNames), toPtrArray(z3Sorts), decls.size, toPtrArray(declNames), toPtrArray(z3Decls)), this)
    } else {
      new Z3AST(Native.parseSmtlib2String(this.ptr, str, sorts.size, toPtrArray(sortNames), toPtrArray(z3Sorts), decls.size, toPtrArray(declNames), toPtrArray(z3Decls)), this)
    }
  }

  /** Uses the SMT-LIB 2 parser to read in a benchmark file.
   */
  def parseSMTLIB2File(fileName: String) : Z3AST = parseSMTLIB2(true, fileName)

  /** Uses the SMT-LIB 2 parser to read in a benchmark string.
   */
  def parseSMTLIB2String(str: String) : Z3AST = parseSMTLIB2(false, str)

  /** Uses the SMT-LIB 2 parser to read in a benchmark file. The maps are used to override symbols that would otherwise be created by the parser.
   */
  def parseSMTLIB2File(fileName: String, sorts: Map[Z3Symbol,Z3Sort], decls: Map[Z3Symbol,Z3FuncDecl]) : Z3AST = parseSMTLIB2(true, fileName, sorts, decls)

  /** Uses the SMT-LIB 2 parser to read in a benchmark string. The maps are used to override symbols that would otherwise be created by the parser.
   */
  def parseSMTLIB2String(str: String, sorts: Map[Z3Symbol,Z3Sort], decls: Map[Z3Symbol,Z3FuncDecl]) : Z3AST = parseSMTLIB2(false, str, sorts, decls)

  def substitute(ast : Z3AST, from : Array[Z3AST], to : Array[Z3AST]) : Z3AST = {
    if (from.length != to.length)
      throw new IllegalArgumentException("from and to must have the same length");
    return new Z3AST(Native.substitute(this.ptr, ast.ptr, from.length, from.map(_.ptr), to.map(_.ptr)), this);
  }

  def setAstPrintMode(printMode : Z3Context.AstPrintMode.AstPrintMode) = {
    var mode : Int = 0
    printMode match {
      case Z3Context.AstPrintMode.Z3_PRINT_SMTLIB_FULL => mode = 0
      case Z3Context.AstPrintMode.Z3_PRINT_LOW_LEVEL => mode = 1
      case Z3Context.AstPrintMode.Z3_PRINT_SMTLIB_COMPLIANT => mode = 2
      case Z3Context.AstPrintMode.Z3_PRINT_SMTLIB2_COMPLIANT => mode = 3
    }
    Native.setAstPrintMode(this.ptr, mode);
  }

  def simplifyAst(ast : Z3AST) : Z3AST = {
    new Z3AST(Native.simplify(this.ptr, ast.ptr), this);
  }

  def mkForAllConst(weight: Int, patterns: Seq[Z3Pattern], bounds: Seq[Z3AST], body: Z3AST) : Z3AST = mkQuantifierConst(true, weight, patterns, bounds, body)

  def mkExistsConst(weight: Int, patterns: Seq[Z3Pattern], bounds: Seq[Z3AST], body: Z3AST) : Z3AST = mkQuantifierConst(false, weight, patterns, bounds, body)

  def mkQuantifierConst(isForAll: Boolean, weight: Int, patterns: Seq[Z3Pattern], bounds: Seq[Z3AST], body: Z3AST) : Z3AST = {
    new Z3AST(
      Native.mkQuantifierConst(
        this.ptr,
        isForAll,
        weight,
        bounds.size,
        toPtrArray(bounds),
        patterns.size,
        toPtrArray(patterns),
        body.ptr),
      this
    )
  }

  def mkTactic(name: String) : Z3Tactic = {
    new Z3Tactic(Native.mkTactic(this.ptr, name), this)
  }

  def mkTacticAndThen(tactic1: Z3Tactic, tactic2: Z3Tactic) : Z3Tactic = {
    new Z3Tactic(Native.tacticAndThen(this.ptr, tactic1.ptr, tactic2.ptr), this)
  }

  def mkSolver() : Z3Solver = {
    new Z3Solver(Native.mkSolver(this.ptr), this)
  }

  def mkSimpleSolver() : Z3Solver = {
    new Z3Solver(Native.mkSimpleSolver(this.ptr), this)
  }

  def mkSolver(tactic: Z3Tactic) : Z3Solver = {
    new Z3Solver(Native.mkSolverFromTactic(this.ptr, tactic.ptr), this)
  }

  def mkOptimizer() : Z3Optimizer = {
    new Z3Optimizer(Native.mkOptimize(this.ptr), this)
  }

  def interrupt() = {
    Native.interrupt(this.ptr)
  }
}
