package z3

package object scala {
  def toggleWarningMessages(enabled: Boolean) : Unit = {
    Z3Wrapper.toggleWarningMessages(enabled)
  }

  def resetMemory : Unit = {
    Z3Wrapper.resetMemory()
  }

  /** A string representation of the version numbers for Z3, the JNI bindings
   * and the Scala API. */
  lazy val version : String = {
    Z3Wrapper.z3VersionString() + "\n" + Z3Wrapper.wrapperVersionString() + "\n" + scalaAPIVersionString
  }

  private val scalaAPIVersionString = "z3.scala 1.2  (in devel. )"
  // private val scalaAPIVersionString = "z3.scala 1.1  (2010-12-03)"
  // private val scalaAPIVersionString = "z3.scala 1.0  (2010-09-16)"

  // type Z3AST = TypedZ3AST[Z3ASTTypes.TopType]
}
