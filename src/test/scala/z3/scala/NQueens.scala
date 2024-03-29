package z3.scala

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

import z3.scala.dsl.{*, given}

import scala.language.implicitConversions

class NQueens extends AnyFunSuite with Matchers {
  import dsl._

  test("NQueens") {

    val numCols = 8
    val ctx = new Z3Context("MODEL" -> true)

    /* Declaring column variables */
    val columns = (0 until numCols).map{ _ => IntVar() }

    /* All queens are on different columns */
    val diffCnstr = Distinct(columns: _*)

    /* Columns are within the bounds */
    val boundsCnstr = for (c <- columns) yield (c >= 0 && c < numCols)

    /* No two queens are on same diagonal */
    val diagonalsCnstr =
        for (i <- 0 until numCols; j <- 0 until i) yield
          ((columns(i) - columns(j) !== i - j) &&
           (columns(i) - columns(j) !== j - i))

    /* We assert all of the above */
    val solver = ctx.mkSolver()
    solver.assertCnstr(diffCnstr)
    boundsCnstr map (solver.assertCnstr(_))
    diagonalsCnstr map (solver.assertCnstr(_))

    val nbModels = solver.checkAndGetAllModels().size

    //println("Total number of models: " + nbModels)
    nbModels should equal (92)

    ctx.delete()
  }
}
