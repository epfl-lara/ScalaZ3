package z3.scala

import z3.{Z3Wrapper,Pointer}

class Z3Config(params: (String,Any)*) extends Pointer(Z3Wrapper.mkConfig()) {
  for((k,v) <- params) {
    Z3Wrapper.setParamValue(this.ptr, k, v.toString)
  }

  def delete() : Unit = {
    Z3Wrapper.delConfig(this.ptr)
    this.ptr = 0L
  }

  def setParamValue(paramID: String, paramValue: String) : Z3Config = {
    Z3Wrapper.setParamValue(this.ptr, paramID, paramValue)
    this
  }
}
