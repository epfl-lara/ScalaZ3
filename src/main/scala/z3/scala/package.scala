package z3

import com.microsoft.z3.Native
import _root_.scala.language.implicitConversions

package object scala {
  // make sure Z3Wrapper has been loaded!
  Z3Wrapper.withinJar()

  def toggleWarningMessages(enabled: Boolean) : Unit = {
    Native.toggleWarningMessages(enabled)
  }

  def resetMemory : Unit = {
    Native.resetMemory()
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

  given astVectorToSeq: Conversion[Z3ASTVector, Seq[Z3AST]] = _.toSeq
}
