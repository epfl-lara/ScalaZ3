package z3.scala

class Z3RefCountQueue[T <: Z3Object] {
  private val drQueue = collection.mutable.Queue[T]()

  protected[z3] def track(t: T): Unit = {
    t.incRef()
    synchronized {
      drQueue += t
    }
  }

  protected[z3] def clearQueue(): Unit = {
    synchronized {
      for (t <- drQueue) {
        t.decRef()
      }
      drQueue.clear()
    }
  }
}
