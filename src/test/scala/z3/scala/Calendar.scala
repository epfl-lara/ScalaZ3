package z3.scala

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import z3.scala.dsl.Operands.*
import z3.scala.dsl.{*, given}

import scala.language.implicitConversions

class Calendar extends AnyFunSuite with Matchers {

  test("Calendar") {
    val totalDays: IntOperand = 10593
    val originYear: IntOperand = 1980

    val (year, day) = choose[Int, Int] { (year, day) =>
      def leapDaysUntil(y: Tree[IntSort]): IntOperand = (y - 1) / 4 - (y - 1) / 100 + (y - 1) / 400

      totalDays === (year - originYear) * 365 + leapDaysUntil(year) - leapDaysUntil(originYear) + day && day > 0 && day <= 366
    }

    year should equal(2008)
    day should equal(366)
  }
}

