package z3

import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers

class QuickPA extends FunSuite with ShouldMatchers {
  import z3.scala._
  import z3.scala.dsl._

  def check(z3: Z3Context, solver: Z3Solver, exp: String)(block : => Unit) : Option[Boolean] = {
    solver.push
    block
    println(exp)
    val res = solver.checkAndGetModel
    res match {
      case (None, _) => println(" -> unknown")
      case (Some(true), mod) => println(" -> sat : "+mod)
      case (Some(false), mod) => println(" -> unsat")
    }
    solver.pop(1)
    res._1
  }

  test("QuickPA") {
    val z3 = new Z3Context("MODEL" -> true)

    val solver = z3.mkSolver

    val z3Strings = new ProceduralAttachment[String](z3) {
      val concat = function((s1,s2) => s1 + s2)
      val substr = predicate((s1,s2) => s2.contains(s1))
      val oddLength = predicate(_.length % 2 == 1)
    }

    import z3Strings._
    
    check(z3, solver, "Sat?") {
      solver.assertCnstr(oddLength("hello"))
    } should equal (Some(true))

    check(z3, solver, "Unsat?") {
      solver.assertCnstr(!oddLength("world"))
    } should equal (Some(false))

    check(z3, solver, "Unsat?") {
      solver.assertCnstr(!(concat("hello", "world") === concat("hel", "loworld")))
    } should equal (Some(false))

    check(z3, solver, "Unsat?") {
      val s1 = variable
      val s2 = variable
      solver.assertCnstr(
           s1 === "hello"
        && (s2 === "world" || s2 === "moon")
        && oddLength(concat(s2, s1))
        && substr(concat(s1, s2), "low")
      )
    } should equal (Some(false))
  }
}
