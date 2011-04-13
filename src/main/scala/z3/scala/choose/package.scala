// package z3.scala
// 
// import Z3ASTTypes._
// 
// package object choose {
//   //implicit def var2ast[A](v: Var[A]) : Z3AST[_] = v.ast
//   implicit object IntVarHandler extends VarHandler[Int] {
//     def construct(context : Z3Context) : TypedZ3AST[BottomType] = context.mkFreshConst("i", context.mkIntSort)
//     // We assume this is called only when there is a model. It could be that
//     // the model doesn't define the variable, hence we're free to choose it.
//     def convert(model : Z3Model, ast: Z3AST) : Int = model.evalAs[Int](ast).getOrElse(0) 
//   }
//   
//   implicit object BoolVarHandler extends VarHandler[Boolean] {
//     def construct(context : Z3Context) : TypedZ3AST[BottomType] = context.mkFreshConst("b", context.mkBoolSort)
//     def convert(model : Z3Model, ast: Z3AST) : Boolean = model.evalAs[Boolean](ast).getOrElse(true)
//   }
// 
//   def choose[T](predicate : Var[T] => Z3AST)(implicit varHandler : VarHandler[T]) : T = {
//     val z3 = new Z3Context(new Z3Config("MODEL" -> true))
//     val ast : Z3AST = varHandler.construct(z3)
//     val wrapper : Var[T] = new Var[T](z3, ast)
//     val constraint : Z3AST = predicate(wrapper)
// 
//     z3.assertCnstr(constraint)
//     z3.checkAndGetModel match {
//       case (Some(true), m) => {
//         val result = varHandler.convert(m, ast)
//         m.delete
//         z3.delete
//         result
//       }
//       case _ => throw new Exception("Could not solve the constraint.")
//     }
//   }
// 
//   def findAll[T](predicate : Var[T] => Z3AST)(implicit varHandler : VarHandler[T]) : Iterator[T] = {
//     val z3 = new Z3Context(new Z3Config("MODEL" -> true))
//     val ast : Z3AST = varHandler.construct(z3)
//     val wrapper : Var[T] = new Var[T](z3, ast)
//     val constraint : Z3AST = predicate(wrapper)
// 
//     z3.assertCnstr(constraint)
//     z3.checkAndGetAllModels.map(m => {
//       val result = varHandler.convert(m, ast)
//       result
//     })
//   }
// 
//   def choose[T1,T2](predicate : (Var[T1],Var[T2]) => Z3AST)(implicit varHandler1 : VarHandler[T1], varHandler2 : VarHandler[T2]) : (T1,T2) = {
//     val z3 = new Z3Context(new Z3Config("MODEL" -> true))
//     val ast1 : Z3AST = varHandler1.construct(z3)
//     val ast2 : Z3AST = varHandler2.construct(z3)
//     val wrapper1 : Var[T1] = new Var[T1](z3, ast1)
//     val wrapper2 : Var[T2] = new Var[T2](z3, ast2)
//     val constraint : Z3AST = predicate(wrapper1, wrapper2)
// 
//     z3.assertCnstr(constraint)
//     z3.checkAndGetModel match {
//       case (Some(true), m) => {
//         val result1 = varHandler1.convert(m, ast1)
//         val result2 = varHandler2.convert(m, ast2)
//         m.delete
//         z3.delete
//         (result1, result2)
//       }
//       case _ => throw new Exception("Could not solve the constraint.")
//     }
//   }
// 
//   def findAll[T1,T2](predicate : (Var[T1],Var[T2]) => Z3AST)(implicit varHandler1 : VarHandler[T1], varHandler2 : VarHandler[T2]) : Iterator[(T1,T2)] = {
//     val z3 = new Z3Context(new Z3Config("MODEL" -> true))
//     val ast1 : Z3AST = varHandler1.construct(z3)
//     val ast2 : Z3AST = varHandler2.construct(z3)
//     val wrapper1 : Var[T1] = new Var[T1](z3, ast1)
//     val wrapper2 : Var[T2] = new Var[T2](z3, ast2)
//     val constraint : Z3AST = predicate(wrapper1, wrapper2)
// 
//     z3.assertCnstr(constraint)
//     z3.checkAndGetAllModels.map(m => {
//       val result1 = varHandler1.convert(m, ast1)
//       val result2 = varHandler2.convert(m, ast2)
//       (result1, result2)
//     })
//   }
// 
//   def choose[T1,T2,T3](predicate : (Var[T1],Var[T2],Var[T3]) => Z3AST)(implicit varHandler1 : VarHandler[T1], varHandler2 : VarHandler[T2], varHandler3 : VarHandler[T3]) : (T1,T2,T3) = {
//     val z3 = new Z3Context(new Z3Config("MODEL" -> true))
//     val ast1 : Z3AST = varHandler1.construct(z3)
//     val ast2 : Z3AST = varHandler2.construct(z3)
//     val ast3 : Z3AST = varHandler3.construct(z3)
//     val wrapper1 : Var[T1] = new Var[T1](z3, ast1)
//     val wrapper2 : Var[T2] = new Var[T2](z3, ast2)
//     val wrapper3 : Var[T3] = new Var[T3](z3, ast3)
//     val constraint : Z3AST = predicate(wrapper1, wrapper2, wrapper3)
// 
//     z3.assertCnstr(constraint)
//     z3.checkAndGetModel match {
//       case (Some(true), m) => {
//         val result1 = varHandler1.convert(m, ast1)
//         val result2 = varHandler2.convert(m, ast2)
//         val result3 = varHandler3.convert(m, ast3)
//         m.delete
//         z3.delete
//         (result1, result2, result3)
//       }
//       case _ => throw new Exception("Could not solve the constraint.")
//     }
//   }
// 
//   def findAll[T1,T2,T3](predicate : (Var[T1],Var[T2],Var[T3]) => Z3AST)(implicit varHandler1 : VarHandler[T1], varHandler2 : VarHandler[T2], varHandler3 : VarHandler[T3]) : Iterator[(T1,T2,T3)] = {
//     val z3 = new Z3Context(new Z3Config("MODEL" -> true))
//     val ast1 : Z3AST = varHandler1.construct(z3)
//     val ast2 : Z3AST = varHandler2.construct(z3)
//     val ast3 : Z3AST = varHandler3.construct(z3)
//     val wrapper1 : Var[T1] = new Var[T1](z3, ast1)
//     val wrapper2 : Var[T2] = new Var[T2](z3, ast2)
//     val wrapper3 : Var[T3] = new Var[T3](z3, ast3)
//     val constraint : Z3AST = predicate(wrapper1, wrapper2, wrapper3)
// 
//     z3.assertCnstr(constraint)
//     z3.checkAndGetAllModels.map(m => {
//       val result1 = varHandler1.convert(m, ast1)
//       val result2 = varHandler2.convert(m, ast2)
//       val result3 = varHandler3.convert(m, ast3)
//       (result1, result2, result3)
//     })
//   }
// }
