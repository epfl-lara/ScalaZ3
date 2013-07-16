package z3

package object scala {

  @deprecated("Use Z3NumeralIntAST instead.", "4.0a")
  val Z3NumeralAST = Z3NumeralIntAST

  def toggleWarningMessages(enabled: Boolean) : Unit = {
    Z3Wrapper.toggleWarningMessages(enabled)
  }

  def resetMemory : Unit = {
    Z3Wrapper.resetMemory()
  }

  /** A string representation of the version numbers for Z3, and the API (including bindings) */
  lazy val version : String = {
    Z3Wrapper.z3VersionString() + ", " + Z3Wrapper.wrapperVersionString()
  }

  protected[z3] def toPtrArray(ptrs : Iterable[Z3Pointer]) : Array[Long] = {
    ptrs.map(_.ptr).toArray
  }

  protected[z3] def i2ob(value: Int) : Option[Boolean] = value match {
    case -1 => Some(false)
    case 0 => None
    case _ => Some(true)
  }


  def error(any : Any) : Nothing = {
    //Predef.error(any.toString)
    sys.error(any.toString) // 2.9
  }
}
