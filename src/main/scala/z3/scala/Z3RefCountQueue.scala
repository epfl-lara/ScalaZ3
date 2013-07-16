package z3.scala

import z3.{Z3Wrapper,Pointer}

class Z3RefCountQueue[T <: Z3Object](maxSize: Int = 512) {
  private val drQueue = collection.mutable.Queue[T]()

  protected[z3] def track(t: T) {
    t.incRef()
    synchronized {
      drQueue += t
    }
  }

  protected[z3] def clearQueue() {
    synchronized {
      for (t <- drQueue) {
        t.decRef()
      }
      drQueue.clear()
    }
  }
}
