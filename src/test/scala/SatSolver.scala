import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers

class SatSolver extends FunSuite with ShouldMatchers {
  import z3.scala._
  import z3.scala.Z3ASTTypes._

  test("Sat solver") {
    case class Literal(name: String, polarity: Boolean)
    type Clause = Set[Literal]

    def DPLL(f : Set[Clause]) : (Boolean, Map[String,Boolean]) =
      if(f.isEmpty) (true, Map.empty[String,Boolean])
      else if(f.exists(clause => clause.isEmpty)) (false, Map.empty[String,Boolean])
      else {
        val z3 = new Z3Context((new Z3Config).setParamValue("MODEL", "true"))
        val b = z3.mkBoolSort()

        val literals = f.reduceLeft((a,b) => a ++ b)
        val litMap: scala.collection.mutable.Map[String,TypedZ3AST[BoolType]] =
          scala.collection.mutable.Map.empty[String,TypedZ3AST[BoolType]]
        
        literals.foreach(lit => {
          if(!litMap.keySet.contains(lit.name)) {
            val ast = z3.mkConst[BoolType](z3.mkStringSymbol(lit.name), b)
            litMap(lit.name) = ast
          }
        })

        f.foreach(clause => {
          if (clause.size > 1) {
            val nc: Array[TypedZ3AST[BoolType]] = new Array[TypedZ3AST[BoolType]](clause.size) 
            var c: Int = 0
            clause.foreach(lit => {
              nc(c) = if(lit.polarity) litMap(lit.name) else z3.mkNot(litMap(lit.name))
              c = c + 1
            })
            z3.assertCnstr(nc.reduceLeft(z3.mkOr(_, _)))
          } else {
            val singleLit = clause.head
            if(singleLit.polarity)
              z3.assertCnstr(litMap(singleLit.name))
            else
              z3.assertCnstr(z3.mkNot(litMap(singleLit.name))) 
          }
        })

        val (result, model) = z3.checkAndGetModel()
          
        result match {
          case None => println("There was an error with Z3."); (false, Map.empty[String,Boolean])
          case Some(false) => (false, Map.empty[String,Boolean]) // formula was unsat
          case Some(true) => (true, Map.empty[String,Boolean] ++ litMap.map(p => (p._1, model.evalAs[Boolean](p._2).get)))
        }
      }

    // DIMACS parser by Hossein Hojjat
    var form = Set.empty[Clause]
    var clause = Set.empty[Literal]
    var tok:String = ""
    val benchfilename = "example.cnf"
    scala.io.Source.fromFile(new java.io.File(benchfilename)).getLines().foreach {
      line => if( !line.startsWith("c") && !line.startsWith("p")) {
        val scanner = new java.util.Scanner(line)
          while(scanner.hasNext) {
          tok = scanner.next
          if (tok != "0") 
            {if( tok.startsWith("-")) clause += Literal(tok.drop(1), false) else clause += Literal(tok, true)} 
          else {
            form += clause
            clause = Set.empty[Literal]
          }}}}
    if (clause.size != 0) form += clause
    val (rs,rm) = DPLL(form)

    // s SATISFIABLE
    // v -12 -8 -19 -4 15 -11 9 -13 -16 -5 -10 6 1 17 14 20 -2 -18 -7 -3
    val pos = rm.filter(p => p._2).map(p => p._1.toInt).toSet
    val neg = rm.filter(p => !p._2).map(p => p._1.toInt).toSet

    rs should equal(true)
    pos should equal(Set(15, 9, 6, 1, 17, 14, 20))
    neg should equal(Set(12, 8, 19, 4, 11, 13, 16, 5, 10, 2, 18, 7, 3))
  }
}

