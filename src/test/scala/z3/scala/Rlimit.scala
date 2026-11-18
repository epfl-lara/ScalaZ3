package z3.scala

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class Rlimit extends AnyFunSuite with Matchers {

  /** The pigeonhole principle: `pigeons` pigeons do not fit in `pigeons - 1` holes.
    * Unsatisfiable, and notoriously hard for SAT solvers.
    */
  private def pigeonhole(z3: Z3Context, pigeons: Int): Seq[Z3AST] = {
    val holes = pigeons - 1
    val in = Seq.tabulate(pigeons, holes)((p, h) => z3.mkBoolConst(s"p${p}_h$h"))
    val eachPigeonHasAHole = in.map(hs => z3.mkOr(hs: _*))
    val noHoleIsShared = for {
      h <- 0 until holes
      p1 <- 0 until pigeons
      p2 <- p1 + 1 until pigeons
    } yield z3.mkOr(z3.mkNot(in(p1)(h)), z3.mkNot(in(p2)(h)))
    eachPigeonHasAHole ++ noHoleIsShared
  }

  private def pigeonholeSolver(z3: Z3Context, pigeons: Int): Z3Solver = {
    val solver = z3.mkSolver()
    pigeonhole(z3, pigeons).foreach(solver.assertCnstr)
    solver
  }

  test("Without rlimit, the problem is solved") {
    val z3 = new Z3Context()
    val solver = pigeonholeSolver(z3, 7)
    solver.check() should equal (Some(false))
    z3.delete()
  }

  test("check gives up once rlimit is exhausted") {
    val z3 = new Z3Context()
    val solver = pigeonholeSolver(z3, 11)
    solver.setRlimit(1000)
    solver.check() should equal (None)
    Seq("canceled", "max. resource limit exceeded") should contain (solver.getReasonUnknown())
    z3.delete()
  }

  test("A large enough rlimit does not get in the way") {
    val z3 = new Z3Context()
    val solver = pigeonholeSolver(z3, 7)
    solver.setRlimit(0xFFFFFFFFL)
    solver.check() should equal (Some(false))
    z3.delete()
  }

  test("rlimit applies to each check, and 0 removes it") {
    val z3 = new Z3Context()
    val solver = pigeonholeSolver(z3, 8)
    solver.setRlimit(1000)
    solver.check() should equal (None)
    solver.check() should equal (None)
    solver.setRlimit(0)
    solver.check() should equal (Some(false))
    z3.delete()
  }

  test("rlimit can be set as a regular solver parameter") {
    val z3 = new Z3Context()
    val solver = pigeonholeSolver(z3, 11)
    solver.set(Map("rlimit" -> 1000))
    solver.check() should equal (None)
    z3.delete()
  }

  test("rlimit is deterministic") {
    def consumed(rlimit: Long): (Option[Boolean], Option[Long]) = {
      val z3 = new Z3Context()
      val solver = pigeonholeSolver(z3, 8)
      solver.setRlimit(rlimit)
      val res = (solver.check(), solver.getRlimitCount())
      z3.delete()
      res
    }

    val (unlimited, Some(needed)) = consumed(0): @unchecked
    unlimited should equal (Some(false))
    needed should be > 1000L

    consumed(0) should equal ((Some(false), Some(needed)))
    // The outcome only depends on the limit, not on the time it takes.
    consumed(needed / 2)._1 should equal (None)
    consumed(needed / 2)._1 should equal (None)
    consumed(needed * 2) should equal ((Some(false), Some(needed)))
  }

  test("Statistics report the resources consumed") {
    val z3 = new Z3Context()
    val solver = pigeonholeSolver(z3, 7)
    solver.getRlimitCount().getOrElse(0L) should be < 1000L
    solver.check() should equal (Some(false))
    val stats = solver.getStatistics()
    stats.keySet should contain ("rlimit count")
    stats("rlimit count") should be > 0.0
    solver.getRlimitCount() should equal (Some(stats("rlimit count").toLong))
    z3.delete()
  }

  test("rlimit is rejected when out of bounds") {
    val z3 = new Z3Context()
    val solver = z3.mkSolver()
    an [IllegalArgumentException] should be thrownBy solver.setRlimit(-1)
    an [IllegalArgumentException] should be thrownBy solver.setRlimit(0x100000000L)
    z3.delete()
  }

  test("rlimit can be set globally") {
    try {
      Z3Context.setGlobalParam("rlimit", 1000)
      val z3 = new Z3Context()
      pigeonholeSolver(z3, 11).check() should equal (None)
      z3.delete()
    } finally {
      Z3Context.resetGlobalParams()
    }

    val z3 = new Z3Context()
    pigeonholeSolver(z3, 7).check() should equal (Some(false))
    z3.delete()
  }

  test("rlimit can be set when creating a context") {
    val z3 = new Z3Context("rlimit" -> 1000)
    pigeonholeSolver(z3, 11).check() should equal (None)
    z3.delete()
  }

  test("The optimizer honours rlimit") {
    val z3 = new Z3Context()
    val opt = z3.mkOptimizer()
    pigeonhole(z3, 11).foreach(opt.assertCnstr)
    opt.setRlimit(1000)
    opt.check() should equal (None)
    opt.getRlimitCount() should not be (empty)

    val easy = z3.mkOptimizer()
    pigeonhole(z3, 5).foreach(easy.assertCnstr)
    easy.setRlimit(0xFFFFFFFFL)
    easy.check() should equal (Some(false))
    z3.delete()
  }
}
