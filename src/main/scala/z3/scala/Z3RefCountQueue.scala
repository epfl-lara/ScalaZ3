package z3.scala

class Z3RefCountQueue[T <: Z3Object] {
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
