package z3

package object scala {
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

  protected[z3] def toPtrArray(ptrs : Iterable[{ def ptr : Long }]) : Array[Long] = {
    ptrs.map(_.ptr).toArray
  }
}
