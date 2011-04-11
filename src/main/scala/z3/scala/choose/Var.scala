package z3.scala.choose

import z3.scala._
import Z3ASTTypes._

class Var[A](val ctx: Z3Context, val ast : Z3AST) {
  def +(other: Int) : TypedZ3AST[NumeralType] = {
    ctx.mkAdd(ast, ctx.mkInt(other, ctx.mkIntSort))
  }

  def +(other: Var[Int]) : TypedZ3AST[NumeralType] = {
    ctx.mkAdd(ast, other.ast)
  }

  def +(other: Z3AST) : TypedZ3AST[NumeralType] = {
    ctx.mkAdd(ast, other)
  }

  def -(other: Int) : TypedZ3AST[NumeralType] = {
    ctx.mkSub(ast, ctx.mkInt(other, ctx.mkIntSort))
  }

  def -(other: Var[Int]) : TypedZ3AST[NumeralType] = {
    ctx.mkSub(ast, other.ast)
  }

  def -(other: Z3AST) : TypedZ3AST[NumeralType] = {
    ctx.mkSub(ast, other)
  }

  def *(other: Int) : TypedZ3AST[NumeralType] = {
    ctx.mkMul(ast, ctx.mkInt(other, ctx.mkIntSort))
  }

  def *(other: Var[Int]) : TypedZ3AST[NumeralType] = {
    ctx.mkMul(ast, other.ast)
  }

  def *(other: Z3AST) : TypedZ3AST[NumeralType] = {
    ctx.mkMul(ast, other)
  }

  def >(other: Int) : TypedZ3AST[BoolType] = {
    ctx.mkGT(ast, ctx.mkInt(other, ctx.mkIntSort))
  }

  def >(other: Var[Int]) : TypedZ3AST[BoolType] = {
    ctx.mkGT(ast, other.ast)
  }

  def >(other: Z3AST) : TypedZ3AST[BoolType] = {
    ctx.mkGT(ast, other)
  }

  def >=(other: Int) : TypedZ3AST[BoolType] = {
    ctx.mkGE(ast, ctx.mkInt(other, ctx.mkIntSort))
  }

  def >=(other: Var[Int]) : TypedZ3AST[BoolType] = {
    ctx.mkGE(ast, other.ast)
  }

  def >=(other: Z3AST) : TypedZ3AST[BoolType] = {
    ctx.mkGE(ast, other)
  }

  def <(other: Int) : TypedZ3AST[BoolType] = {
    ctx.mkLT(ast, ctx.mkInt(other, ctx.mkIntSort))
  }

  def <(other: Var[Int]) : TypedZ3AST[BoolType] = {
    ctx.mkLT(ast, other.ast)
  }

  def <(other: Z3AST) : TypedZ3AST[BoolType] = {
    ctx.mkLT(ast, other)
  }

  def <=(other: Int) : TypedZ3AST[BoolType] = {
    ctx.mkLE(ast, ctx.mkInt(other, ctx.mkIntSort))
  }

  def <=(other: Var[Int]) : TypedZ3AST[BoolType] = {
    ctx.mkLE(ast, other.ast)
  }

  def <=(other: Z3AST) : TypedZ3AST[BoolType] = {
    ctx.mkLE(ast, other)
  }

  def ===(other: Int) : TypedZ3AST[BoolType] = {
    ctx.mkEq(ast, ctx.mkInt(other, ctx.mkIntSort))
  }

  def ===(other: Var[Int]) : TypedZ3AST[BoolType] = {
    ctx.mkEq(ast, other.ast)
  }

  def ===(other: Z3AST) : TypedZ3AST[BoolType] = {
    ctx.mkEq(ast, other)
  }
}

abstract class VarHandler[A] {
  def construct(context : Z3Context) : Z3AST
  def convert(model : Z3Model, ast: Z3AST) : A
}


