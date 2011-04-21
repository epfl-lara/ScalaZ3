import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers

class QuickPA extends FunSuite with ShouldMatchers {
  import z3.scala._
  import z3.scala.dsl._

  def check(z3: Z3Context, exp: String)(block : => Unit) : Option[Boolean] = {
    z3.push
    block
    println(exp)
    val res = z3.check
    res match {
      case None => println(" -> unknown")
      case Some(true) => println(" -> sat")
      case Some(false) => println(" -> unsat")
    }
    z3.pop(1)
    res
  }

  test("QuickPA") {
    val z3 = new Z3Context("MODEL" -> true)

    val z3Strings = new ProceduralAttachment[String](z3) {
      val concat = function((s1,s2) => s1 + s2)
      val substr = predicate((s1,s2) => s2.contains(s1))
      val oddLength = predicate(_.length % 2 == 1)
    }

    import z3Strings._
    
    check(z3, "Sat?") {
      z3.assertCnstr(oddLength("hello"))
    } should equal (Some(true))

    check(z3, "Unsat?") {
      z3.assertCnstr(!oddLength("world"))
    } should equal (Some(false))

    check(z3, "Unsat?") {
      z3.assertCnstr(!(concat("hello", "world") === concat("hel", "loworld")))
    } should equal (Some(false))

    check(z3, "Unsat?") {
      val s1 = variable
      val s2 = variable
      z3.assertCnstr(
           s1 === "hello"
        && (s2 === "world" || s2 === "moon")
        && oddLength(concat(s2, s1))
        && substr(concat(s1, s2), "low")
      )
    } should equal (Some(false))
  }
}
