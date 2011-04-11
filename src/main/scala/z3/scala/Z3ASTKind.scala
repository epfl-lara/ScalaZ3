package z3.scala

// models the Z3_ast_kind enum
sealed abstract class Z3ASTKind

case class Z3NumeralAST private[z3](value: Option[Int]) extends Z3ASTKind
case class Z3AppAST private[z3](decl: Z3FuncDecl, args: Seq[Z3AST]) extends Z3ASTKind
case object Z3VarAST extends Z3ASTKind
case object Z3QuantifierAST extends Z3ASTKind
case object Z3UnknownAST extends Z3ASTKind
