import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers

import z3.scala._
import z3.scala.dsl._

class Calendar extends Z3Application with FunSuite with ShouldMatchers {

  test("Calendar") {
    val totalDays = 732

    val years = ctx.mkIntConst("ys")
    val leapYears = ctx.mkIntConst("ls")
    val year  = ctx.mkIntConst("y")
    val days  = ctx.mkIntConst("ds")

    val cnstr =
      leapYears === ((year - 1) / 4) - ((year - 1) / 100) + ((year - 1) / 400) - (1979/4 - 1979/100 + 1979/400) &&
      totalDays === days + years * 365 + leapYears &&
      years === year - 1980 &&
      days >= 0 &&
      days <= 366 

    ctx.assertCnstr(cnstr)

    val (res, model) = ctx.checkAndGetModel

    res should equal (Some(true))

    res match {
      case Some(true) => 
        val evalYear = model.evalAs[Int](year)
        val evalDays = model.evalAs[Int](days)
        println("Sat.")
        println("Year          : " + evalYear)
        println("Remaining Days: " + evalDays)
        evalYear should equal (Some(1982))
        evalDays should equal (Some(1))
      case Some(false) => println("Unsat.")
      case _ => println("Unknown!")
    }
  }
}

