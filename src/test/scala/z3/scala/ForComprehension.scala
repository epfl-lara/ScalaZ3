package z3.scala

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

import z3.scala.dsl.{*, given}

import scala.language.implicitConversions

class ForComprehension extends AnyFunSuite with Matchers {

  import dsl._

  def isPrime(i: Int): Boolean = {
    !(2 until i).exists(i % _ == 0)
  }

  test("ForComprehension") {
    val results = for {
      (x, y) <- findAll[Int, Int]((x, y) => x > 0 && y > x && x * 2 + y * 3 <= 40)
      if isPrime(y)
      z <- findAll[Int](z => z * x === 3 * y * y)
    } yield (x, y, z)

    results.size should equal(8)
  }
}
