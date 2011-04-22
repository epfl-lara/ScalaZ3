import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers

import z3.scala._
import z3.scala.dsl._

class Calendar extends FunSuite with ShouldMatchers {

  test("Calendar") {
    val totalDays = 10593
    val originYear = 1980

    val (year, day) = choose((year: Val[Int], day: Val[Int]) => {
      def leapDaysUntil(y : Tree[IntSort]) = (y - 1) / 4 - (y - 1) / 100 + (y - 1) / 400

      totalDays === (year - originYear) * 365 + leapDaysUntil(year) - leapDaysUntil(originYear) + day && 
      day > 0 && day <= 366
    })

    year should equal (2008)
    day should equal (366)
  }
}

