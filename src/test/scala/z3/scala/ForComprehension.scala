package z3

import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers

class ForComprehension extends FunSuite with ShouldMatchers {
  import z3.scala.dsl._

  def isPrime(i : Int) : Boolean = {
    ! (2 to i-1).exists(i % _ == 0)
  }

  test("ForComprehension") {
    val results = for(
      (x,y) <- findAll[Int,Int]((x: Val[Int], y: Val[Int]) => x > 0 && y > x && x * 2 + y * 3 <= 40);
      if(isPrime(y));
      z <- findAll((z: Val[Int]) => z * x === 3 * y * y))
    yield (x, y, z)

    results.size should equal (8)
  }
}
