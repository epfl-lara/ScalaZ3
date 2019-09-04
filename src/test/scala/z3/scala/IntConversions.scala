package z3.scala

import org.scalatest.{FunSuite, Matchers}

class IntConversions extends FunSuite with Matchers {

  testIntToBigInt(0)
  testIntToBigInt(42)
  testIntToBigInt(-42)
  testIntToBigInt(Int.MinValue)
  testIntToBigInt(Int.MaxValue)

  testBigIntToInt(0)
  testBigIntToInt(42)
  testBigIntToInt(-42)
  testBigIntToInt(Int.MinValue)
  testBigIntToInt(Int.MaxValue)

  private def testIntToBigInt(value: Int): Unit = test(s"Int -> BigInt: $value") {
    val z3 = new Z3Context("MODEL" -> true)

    val Int  = z3.mkIntSort
    val BV32 = z3.mkBVSort(32)

    val in  = z3.mkConst(z3.mkStringSymbol("in"), BV32)
    val out = z3.mkConst(z3.mkStringSymbol("out"), Int)

    val solver = z3.mkSolver

    solver.assertCnstr(z3.mkEq(in,  z3.mkInt(value, BV32)))
    solver.assertCnstr(z3.mkEq(out, z3.mkBV2Int(in, true)))

    solver.assertCnstr(z3.mkNot(
      z3.mkEq(out, z3.mkInt(value, Int))
    ))

    val (sol, model) = solver.checkAndGetModel

    (sol, model) should equal((Some(false), null))

    z3.delete
  }

  private def testBigIntToInt(value: Int): Unit = test(s"BigInt -> Int: $value") {
    val z3 = new Z3Context("MODEL" -> true)

    val Int  = z3.mkIntSort
    val BV32 = z3.mkBVSort(32)

    val in  = z3.mkConst(z3.mkStringSymbol("in"), Int)
    val out = z3.mkConst(z3.mkStringSymbol("out"), BV32)

    val solver = z3.mkSolver

    solver.assertCnstr(z3.mkEq(in,  z3.mkInt(value, Int)))
    solver.assertCnstr(z3.mkEq(out, z3.mkInt2BV(32, in)))

    solver.assertCnstr(z3.mkNot(
      z3.mkEq(out, z3.mkInt(value, BV32))
    ))

    val (sol, model) = solver.checkAndGetModel

    (sol, model) should equal((Some(false), null))

    z3.delete
  }
}

