import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers

import z3.scala._
import z3.scala.dsl._

class Calendar extends FunSuite with ShouldMatchers {

  test("Calendar") {
    val totalDays = 10593
    val originYear = 1980

    val (year, day, _) = choose((year: Val[Int], day: Val[Int], leapDays: Val[Int]) =>
      leapDays === (year - 1) / 4 - (year - 1) / 100 + (year - 1) / 400 - ((originYear - 1) / 4 - (originYear - 1) / 100 + (originYear - 1) / 400)
      && totalDays === (year - originYear) * 365 + leapDays + day
      && day > 0 && day <= 366)

    year should equal (2008)
    day should equal (366)

    // val (year, days, years, leapYears) = choose((year: Val[Int], days: Val[Int], years: Val[Int], leapYears: Val[Int]) =>
    //   leapYears === ((year - 1) / 4) - ((year - 1) / 100) + ((year - 1) / 400) - (1979/4 - 1979/100 + 1979/400) &&
    //   totalDays === days + years * 365 + leapYears &&
    //   years === year - 1980 &&
    //   days >= 0 &&
    //   days <= 366)

  }
}

