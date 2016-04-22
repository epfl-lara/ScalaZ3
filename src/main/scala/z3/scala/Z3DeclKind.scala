package z3.scala

object Z3DeclKind extends Enumeration {
  type Z3DeclKind = Value

  // Basic
  val OpTrue = Value
  val OpFalse = Value
  val OpEq = Value
  val OpDistinct = Value
  val OpITE = Value
  val OpAnd = Value
  val OpOr = Value
  val OpIff = Value
  val OpXor = Value
  val OpNot = Value
  val OpImplies = Value
  val OpOEq = Value      // NEW in ScalaZ3 3.0
  val OpInterp = Value   // NEW in ScalaZ3 3.0

  // Arithmetic
  val OpANum = Value
  val OpAGNum = Value  // NEW in ScalaZ3 3.0
  val OpLE = Value
  val OpGE = Value
  val OpLT = Value
  val OpGT = Value
  val OpAdd = Value
  val OpSub = Value
  val OpUMinus = Value
  val OpMul = Value
  val OpDiv = Value
  val OpIDiv = Value
  val OpRem = Value
  val OpMod = Value
  val OpToReal = Value
  val OpToInt = Value
  val OpIsInt = Value
  val OpPower = Value  // NEW in ScalaZ3 3.0

  // Arrays & Sets
  val OpStore = Value
  val OpSelect = Value
  val OpConstArray = Value
  val OpArrayMap = Value
  val OpArrayDefault = Value
  val OpSetUnion = Value
  val OpSetIntersect = Value
  val OpSetDifference = Value
  val OpSetComplement = Value
  val OpSetSubset = Value
  val OpAsArray = Value
  val OpArrayExt = Value      // NEW in ScalaZ3 3.0

  // Bit-vectors
  val OpBNum = Value // NEW in ScalaZ3 3.0
  val OpBit1 = Value // NEW in ScalaZ3 3.0
  val OpBit0 = Value // NEW in ScalaZ3 3.0
  val OpBNeg = Value // NEW in ScalaZ3 3.0
  val OpBAdd = Value // NEW in ScalaZ3 3.0
  val OpBSub = Value // NEW in ScalaZ3 3.0
  val OpBMul = Value // NEW in ScalaZ3 3.0

  val OpBSDiv = Value // NEW in ScalaZ3 3.0
  val OpBUDiv = Value // NEW in ScalaZ3 3.0
  val OpBSRem = Value // NEW in ScalaZ3 3.0
  val OpBURem = Value // NEW in ScalaZ3 3.0
  val OpBSMod = Value // NEW in ScalaZ3 3.0

  val OpULE = Value // NEW in ScalaZ3 3.0
  val OpSLE = Value // NEW in ScalaZ3 3.0
  val OpUGE = Value // NEW in ScalaZ3 3.0
  val OpSGE = Value // NEW in ScalaZ3 3.0
  val OpULT = Value // NEW in ScalaZ3 3.0
  val OpSLT = Value // NEW in ScalaZ3 3.0
  val OpUGT = Value // NEW in ScalaZ3 3.0
  val OpSGT = Value // NEW in ScalaZ3 3.0

  val OpBAnd = Value  // NEW in ScalaZ3 3.0
  val OpBOr = Value   // NEW in ScalaZ3 3.0
  val OpBNot = Value  // NEW in ScalaZ3 3.0
  val OpBXor = Value  // NEW in ScalaZ3 3.0
  val OpBNand = Value // NEW in ScalaZ3 3.0
  val OpBNor = Value  // NEW in ScalaZ3 3.0
  val OpBXnor = Value // NEW in ScalaZ3 3.0

  val OpConcat = Value  // NEW in ScalaZ3 3.0
  val OpSignExt = Value // NEW in ScalaZ3 3.0
  val OpZeroExt = Value // NEW in ScalaZ3 3.0
  val OpExtract = Value // NEW in ScalaZ3 3.0
  val OpRepeat = Value  // NEW in ScalaZ3 3.0

  val OpBRedAnd = Value // NEW in ScalaZ3 3.0
  val OpBRedOr = Value  // NEW in ScalaZ3 3.0
  val OpBComp = Value   // NEW in ScalaZ3 3.0

  val OpBShl = Value           // NEW in ScalaZ3 3.0
  val OpBLshr = Value          // NEW in ScalaZ3 3.0
  val OpBAshr = Value          // NEW in ScalaZ3 3.0
  val OpRotateLeft = Value     // NEW in ScalaZ3 3.0
  val OpRotateRight = Value    // NEW in ScalaZ3 3.0
  val OpExtRotateLeft = Value  // NEW in ScalaZ3 3.0
  val OpExtRotateRight = Value // NEW in ScalaZ3 3.0

  val OpIntToBV = Value // NEW in ScalaZ3 3.0
  val OpBVToInt = Value // NEW in ScalaZ3 3.0
  val OpCarry = Value   // NEW in ScalaZ3 3.0
  val OpXor3 = Value    // NEW in ScalaZ3 3.0

  // Proofs
  val OpPrUndef = Value            // NEW in ScalaZ3 3.0
  val OpPrTrue = Value             // NEW in ScalaZ3 3.0
  val OpPrAsserted = Value         // NEW in ScalaZ3 3.0
  val OpPrGoal = Value             // NEW in ScalaZ3 3.0
  val OpPrModusPonens = Value      // NEW in ScalaZ3 3.0
  val OpPrReflexivity = Value      // NEW in ScalaZ3 3.0
  val OpPrSymmetry = Value         // NEW in ScalaZ3 3.0
  val OpPrTransitivity = Value     // NEW in ScalaZ3 3.0
  val OpPrTransitivityStar = Value // NEW in ScalaZ3 3.0
  val OpPrMonotonicity = Value     // NEW in ScalaZ3 3.0
  val OpPrQuantIntro = Value       // NEW in ScalaZ3 3.0
  val OpPrDistributivity = Value   // NEW in ScalaZ3 3.0
  val OpPrAndElim = Value          // NEW in ScalaZ3 3.0
  val OpPrNotOrElim = Value        // NEW in ScalaZ3 3.0
  val OpPrRewrite = Value          // NEW in ScalaZ3 3.0
  val OpPrRewriteStar = Value      // NEW in ScalaZ3 3.0
  val OpPrPullQuant = Value        // NEW in ScalaZ3 3.0
  val OpPrPullQuantStar = Value    // NEW in ScalaZ3 3.0
  val OpPrPushQuant = Value        // NEW in ScalaZ3 3.0
  val OpPrElimUnusedVars = Value   // NEW in ScalaZ3 3.0
  val OpPrDER = Value              // NEW in ScalaZ3 3.0
  val OpPrQuantInst = Value        // NEW in ScalaZ3 3.0
  val OpPrHypothesis = Value       // NEW in ScalaZ3 3.0
  val OpPrLemma = Value            // NEW in ScalaZ3 3.0
  val OpPrUnitResolution = Value   // NEW in ScalaZ3 3.0
  val OpPrIffTrue = Value          // NEW in ScalaZ3 3.0
  val OpPrIffFalse = Value         // NEW in ScalaZ3 3.0
  val OpPrCommutativity = Value    // NEW in ScalaZ3 3.0
  val OpPrDefAxiom = Value         // NEW in ScalaZ3 3.0
  val OpPrDefIntro = Value         // NEW in ScalaZ3 3.0
  val OpPrApplyDef = Value         // NEW in ScalaZ3 3.0
  val OpPrIffOEq = Value           // NEW in ScalaZ3 3.0
  val OpPrNNFPos = Value           // NEW in ScalaZ3 3.0
  val OpPrNNFNeg = Value           // NEW in ScalaZ3 3.0
  val OpPrNNFStar = Value          // NEW in ScalaZ3 3.0
  val OpPrCNFStar = Value          // NEW in ScalaZ3 3.0
  val OpPrSkolemize = Value        // NEW in ScalaZ3 3.0
  val OpPrModusPonensOEq = Value   // NEW in ScalaZ3 3.0
  val OpPrThLemma = Value          // NEW in ScalaZ3 3.0
  val OpPrHyperResolve = Value     // NEW in ScalaZ3 3.0

  // Relational algebra
  val OpRAStore = Value          // NEW in ScalaZ3 3.0
  val OpRAEmpty = Value          // NEW in ScalaZ3 3.0
  val OpRAIsEmpty = Value        // NEW in ScalaZ3 3.0
  val OpRAJoin = Value           // NEW in ScalaZ3 3.0
  val OpRAUnion = Value          // NEW in ScalaZ3 3.0
  val OpRAWiden = Value          // NEW in ScalaZ3 3.0
  val OpRAProject = Value        // NEW in ScalaZ3 3.0
  val OpRAFilter = Value         // NEW in ScalaZ3 3.0
  val OpRANegationFilter = Value // NEW in ScalaZ3 3.0
  val OpRARename = Value         // NEW in ScalaZ3 3.0
  val OpRAComplement = Value     // NEW in ScalaZ3 3.0
  val OpRASelect = Value         // NEW in ScalaZ3 3.0
  val OpRAClone = Value          // NEW in ScalaZ3 3.0
  val OpFdConstant = Value       // NEW in ScalaZ3 3.0
  val OpFdLT = Value             // NEW in ScalaZ3 3.0

  // Sequences
  val OpSeqUnit = Value     // NEW in ScalaZ3 3.0
  val OpSeqEmpty = Value    // NEW in ScalaZ3 3.0
  val OpSeqConcat = Value   // NEW in ScalaZ3 3.0
  val OpSeqPrefix = Value   // NEW in ScalaZ3 3.0
  val OpSeqSuffix = Value   // NEW in ScalaZ3 3.0
  val OpSeqContains = Value // NEW in ScalaZ3 3.0
  val OpSeqExtract = Value  // NEW in ScalaZ3 3.0
  val OpSeqReplace = Value  // NEW in ScalaZ3 3.0
  val OpSeqAt = Value       // NEW in ScalaZ3 3.0
  val OpSeqLength = Value   // NEW in ScalaZ3 3.0
  val OpSeqIndex = Value    // NEW in ScalaZ3 3.0
  val OpSeqToRE = Value     // NEW in ScalaZ3 3.0
  val OpSeqInRE = Value     // NEW in ScalaZ3 3.0

  // Regular expressions
  val OpREPlus = Value   // NEW in ScalaZ3 3.0
  val OpREStar = Value   // NEW in ScalaZ3 3.0
  val OpREOption = Value // NEW in ScalaZ3 3.0
  val OpREConcat = Value // NEW in ScalaZ3 3.0
  val OpREUnion = Value  // NEW in ScalaZ3 3.0

  // Auxiliary
  val OpLabel = Value    // NEW in ScalaZ3 3.0
  val OpLabelLit = Value // NEW in ScalaZ3 3.0

  // Datatypes
  val OpDTConstructor = Value // NEW in ScalaZ3 3.0
  val OpDTRecogniser = Value  // NEW in ScalaZ3 3.0
  val OpDTAccessor = Value    // NEW in ScalaZ3 3.0
  val OpDTUpdateField = Value // NEW in ScalaZ3 3.0

  // Pseudo booleans
  val OpPBAtMost = Value // NEW in ScalaZ3 3.0
  val OpPBLE = Value     // NEW in ScalaZ3 3.0
  val OpPBGE = Value     // NEW in ScalaZ3 3.0

  // Floating-point arithmetic
  val OpFPARmNearestTiesToEven = Value // NEW in ScalaZ3 3.0
  val OpFPARmNearestTiesToAway = Value // NEW in ScalaZ3 3.0
  val OpFPARmTowardPositive = Value    // NEW in ScalaZ3 3.0
  val OpFPARmTowardNegative = Value    // NEW in ScalaZ3 3.0
  val OpFPARmTowardZero = Value        // NEW in ScalaZ3 3.0

  val OpFPANum = Value       // NEW in ScalaZ3 3.0
  val OpFPAPlusInf = Value   // NEW in ScalaZ3 3.0
  val OpFPAMinusInf = Value  // NEW in ScalaZ3 3.0
  val OpFPANaN = Value       // NEW in ScalaZ3 3.0
  val OpFPAPlusZero = Value  // NEW in ScalaZ3 3.0
  val OpFPAMinusZero = Value // NEW in ScalaZ3 3.0

  val OpFPAAdd = Value             // NEW in ScalaZ3 3.0
  val OpFPASub = Value             // NEW in ScalaZ3 3.0
  val OpFPANeg = Value             // NEW in ScalaZ3 3.0
  val OpFPAMul = Value             // NEW in ScalaZ3 3.0
  val OpFPADiv = Value             // NEW in ScalaZ3 3.0
  val OpFPARem = Value             // NEW in ScalaZ3 3.0
  val OpFPAAbs = Value             // NEW in ScalaZ3 3.0
  val OpFPAMin = Value             // NEW in ScalaZ3 3.0
  val OpFPAMax = Value             // NEW in ScalaZ3 3.0
  val OpFPAFMA = Value             // NEW in ScalaZ3 3.0
  val OpFPASqrt = Value            // NEW in ScalaZ3 3.0
  val OpFPARoundToIntegral = Value // NEW in ScalaZ3 3.0

  val OpFPAEq = Value          // NEW in ScalaZ3 3.0
  val OpFPALT = Value          // NEW in ScalaZ3 3.0
  val OpFPAGT = Value          // NEW in ScalaZ3 3.0
  val OpFPALE = Value          // NEW in ScalaZ3 3.0
  val OpFPAGE = Value          // NEW in ScalaZ3 3.0
  val OpFPAIsNaN = Value       // NEW in ScalaZ3 3.0
  val OpFPAIsInf = Value       // NEW in ScalaZ3 3.0
  val OpFPAIsZero = Value      // NEW in ScalaZ3 3.0
  val OpFPAIsNormal = Value    // NEW in ScalaZ3 3.0
  val OpFPAIsSubnormal = Value // NEW in ScalaZ3 3.0
  val OpFPAIsNegative = Value  // NEW in ScalaZ3 3.0
  val OpFPAIsPositive = Value  // NEW in ScalaZ3 3.0

  val OpFPAFP = Value           // NEW in ScalaZ3 3.0
  val OpFPAToFP = Value         // NEW in ScalaZ3 3.0
  val OpFPAToFPUnsigned = Value // NEW in ScalaZ3 3.0
  val OpFPAToUBV = Value        // NEW in ScalaZ3 3.0
  val OpFPAToSBV = Value        // NEW in ScalaZ3 3.0
  val OpFPAToReal = Value       // NEW in ScalaZ3 3.0

  val OpFPAToIEEEBV = Value // NEW in ScalaZ3 3.0

  val OpUninterpreted = Value

  val Other = Value
}
