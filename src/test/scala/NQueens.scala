import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers

class NQueens extends FunSuite with ShouldMatchers {

import z3.scala._
import z3.scala.dsl._

  test("NQueens") {

    val numCols = 8
    val ctx = new Z3Context("MODEL" -> true)

    /* Declaring column variables */
    val columns = (0 until numCols).toList.map{
      j => IntVar()
    }

    /* All queens are on different columns */
    val diffCnstr = ctx.mkDistinct(columns map (_.ast(ctx)): _*)

    /* Columns are within the bounds */
    val boundsCnstr = for (c <- columns) yield (c >= 0 && c < numCols)

    /* No two queens are on same diagonal */
    val diagonalsCnstr =
        for (i <- 0 until numCols; j <- 0 until i) yield
          ((columns(i) - columns(j) !== i - j) &&
           (columns(i) - columns(j) !== j - i))

    /* We assert all of the above */
    ctx.assertCnstr(diffCnstr)
    boundsCnstr map (ctx.assertCnstr(_))
    diagonalsCnstr map (ctx.assertCnstr(_))

    val models = scala.collection.mutable.Set[Z3Model]()
    for (model <- ctx.checkAndGetAllModels) {
      models += model
      model.delete
    }

    println("Total number of models: " + models.size)
    models.size should equal (92)

    ctx.delete
  }
}
