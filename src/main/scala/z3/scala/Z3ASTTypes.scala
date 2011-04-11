package z3.scala

object Z3ASTTypes {
  sealed trait Z3ASTType
  trait TopType extends Z3ASTType

  trait NumeralType extends TopType
  trait BVType extends TopType
  trait BoolType extends TopType
  trait ArrayType extends TopType

  trait BottomType extends NumeralType with BVType with BoolType with ArrayType
}
