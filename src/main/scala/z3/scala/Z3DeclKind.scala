package z3.scala

import com.microsoft.z3.enumerations._

sealed abstract class Z3DeclKind(val value: Int)

// Basic
object OpTrue     extends Z3DeclKind (Z3_decl_kind.Z3_OP_TRUE.toInt)
object OpFalse    extends Z3DeclKind (Z3_decl_kind.Z3_OP_FALSE.toInt)
object OpEq       extends Z3DeclKind (Z3_decl_kind.Z3_OP_EQ.toInt)
object OpDistinct extends Z3DeclKind (Z3_decl_kind.Z3_OP_DISTINCT.toInt)
object OpITE      extends Z3DeclKind (Z3_decl_kind.Z3_OP_ITE.toInt)
object OpAnd      extends Z3DeclKind (Z3_decl_kind.Z3_OP_AND.toInt)
object OpOr       extends Z3DeclKind (Z3_decl_kind.Z3_OP_OR.toInt)
object OpIff      extends Z3DeclKind (Z3_decl_kind.Z3_OP_IFF.toInt)
object OpXor      extends Z3DeclKind (Z3_decl_kind.Z3_OP_XOR.toInt)
object OpNot      extends Z3DeclKind (Z3_decl_kind.Z3_OP_NOT.toInt)
object OpImplies  extends Z3DeclKind (Z3_decl_kind.Z3_OP_IMPLIES.toInt)
object OpOEq      extends Z3DeclKind (Z3_decl_kind.Z3_OP_OEQ.toInt)      // NEW in ScalaZ3 3.0

// Arithmetic
object OpANum   extends Z3DeclKind (Z3_decl_kind.Z3_OP_ANUM.toInt)
object OpAGNum  extends Z3DeclKind (Z3_decl_kind.Z3_OP_AGNUM.toInt)  // NEW in ScalaZ3 3.0
object OpLE     extends Z3DeclKind (Z3_decl_kind.Z3_OP_LE.toInt)
object OpGE     extends Z3DeclKind (Z3_decl_kind.Z3_OP_GE.toInt)
object OpLT     extends Z3DeclKind (Z3_decl_kind.Z3_OP_LT.toInt)
object OpGT     extends Z3DeclKind (Z3_decl_kind.Z3_OP_GT.toInt)
object OpAdd    extends Z3DeclKind (Z3_decl_kind.Z3_OP_ADD.toInt)
object OpSub    extends Z3DeclKind (Z3_decl_kind.Z3_OP_SUB.toInt)
object OpUMinus extends Z3DeclKind (Z3_decl_kind.Z3_OP_UMINUS.toInt)
object OpMul    extends Z3DeclKind (Z3_decl_kind.Z3_OP_MUL.toInt)
object OpDiv    extends Z3DeclKind (Z3_decl_kind.Z3_OP_DIV.toInt)
object OpIDiv   extends Z3DeclKind (Z3_decl_kind.Z3_OP_IDIV.toInt)
object OpRem    extends Z3DeclKind (Z3_decl_kind.Z3_OP_REM.toInt)
object OpMod    extends Z3DeclKind (Z3_decl_kind.Z3_OP_MOD.toInt)
object OpToReal extends Z3DeclKind (Z3_decl_kind.Z3_OP_TO_REAL.toInt)
object OpToInt  extends Z3DeclKind (Z3_decl_kind.Z3_OP_TO_INT.toInt)
object OpIsInt  extends Z3DeclKind (Z3_decl_kind.Z3_OP_IS_INT.toInt)
object OpPower  extends Z3DeclKind (Z3_decl_kind.Z3_OP_POWER.toInt)  // NEW in ScalaZ3 3.0
object OpAbs    extends Z3DeclKind (Z3_decl_kind.Z3_OP_ABS.toInt)  // NEW in ScalaZ3 4.8.12
object OpComplex  extends Z3DeclKind (Z3_decl_kind.Z3_OP_COMPLEX.toInt) // NEW in ScalaZ3 5.4.1
object OpRealPart extends Z3DeclKind (Z3_decl_kind.Z3_OP_REAL_PART.toInt) // NEW in ScalaZ3 5.4.1
object OpImagPart extends Z3DeclKind (Z3_decl_kind.Z3_OP_IMAG_PART.toInt) // NEW in ScalaZ3 5.4.1

// Arrays & Sets
object OpStore         extends Z3DeclKind (Z3_decl_kind.Z3_OP_STORE.toInt)
object OpSelect        extends Z3DeclKind (Z3_decl_kind.Z3_OP_SELECT.toInt)
object OpConstArray    extends Z3DeclKind (Z3_decl_kind.Z3_OP_CONST_ARRAY.toInt)
object OpArrayMap      extends Z3DeclKind (Z3_decl_kind.Z3_OP_ARRAY_MAP.toInt)
object OpArrayDefault  extends Z3DeclKind (Z3_decl_kind.Z3_OP_ARRAY_DEFAULT.toInt)
object OpSetUnion      extends Z3DeclKind (Z3_decl_kind.Z3_OP_SET_UNION.toInt)
object OpSetIntersect  extends Z3DeclKind (Z3_decl_kind.Z3_OP_SET_INTERSECT.toInt)
object OpSetDifference extends Z3DeclKind (Z3_decl_kind.Z3_OP_SET_DIFFERENCE.toInt)
object OpSetComplement extends Z3DeclKind (Z3_decl_kind.Z3_OP_SET_COMPLEMENT.toInt)
object OpSetSubset     extends Z3DeclKind (Z3_decl_kind.Z3_OP_SET_SUBSET.toInt)
object OpAsArray       extends Z3DeclKind (Z3_decl_kind.Z3_OP_AS_ARRAY.toInt)
object OpArrayExt      extends Z3DeclKind (Z3_decl_kind.Z3_OP_ARRAY_EXT.toInt) // NEW in ScalaZ3 3.0

// Bit-vectors
object OpBNum extends Z3DeclKind (Z3_decl_kind.Z3_OP_BNUM.toInt) // NEW in ScalaZ3 3.0
object OpBit1 extends Z3DeclKind (Z3_decl_kind.Z3_OP_BIT1.toInt) // NEW in ScalaZ3 3.0
object OpBit0 extends Z3DeclKind (Z3_decl_kind.Z3_OP_BIT0.toInt) // NEW in ScalaZ3 3.0
object OpBNeg extends Z3DeclKind (Z3_decl_kind.Z3_OP_BNEG.toInt) // NEW in ScalaZ3 3.0
object OpBAdd extends Z3DeclKind (Z3_decl_kind.Z3_OP_BADD.toInt) // NEW in ScalaZ3 3.0
object OpBSub extends Z3DeclKind (Z3_decl_kind.Z3_OP_BSUB.toInt) // NEW in ScalaZ3 3.0
object OpBMul extends Z3DeclKind (Z3_decl_kind.Z3_OP_BMUL.toInt) // NEW in ScalaZ3 3.0

object OpBSDiv extends Z3DeclKind (Z3_decl_kind.Z3_OP_BSDIV.toInt) // NEW in ScalaZ3 3.0
object OpBUDiv extends Z3DeclKind (Z3_decl_kind.Z3_OP_BUDIV.toInt) // NEW in ScalaZ3 3.0
object OpBSRem extends Z3DeclKind (Z3_decl_kind.Z3_OP_BSREM.toInt) // NEW in ScalaZ3 3.0
object OpBURem extends Z3DeclKind (Z3_decl_kind.Z3_OP_BUREM.toInt) // NEW in ScalaZ3 3.0
object OpBSMod extends Z3DeclKind (Z3_decl_kind.Z3_OP_BSMOD.toInt) // NEW in ScalaZ3 3.0

object OpULE extends Z3DeclKind (Z3_decl_kind.Z3_OP_ULEQ.toInt) // NEW in ScalaZ3 3.0
object OpSLE extends Z3DeclKind (Z3_decl_kind.Z3_OP_SLEQ.toInt) // NEW in ScalaZ3 3.0
object OpUGE extends Z3DeclKind (Z3_decl_kind.Z3_OP_UGEQ.toInt) // NEW in ScalaZ3 3.0
object OpSGE extends Z3DeclKind (Z3_decl_kind.Z3_OP_SGEQ.toInt) // NEW in ScalaZ3 3.0
object OpULT extends Z3DeclKind (Z3_decl_kind.Z3_OP_ULT.toInt)  // NEW in ScalaZ3 3.0
object OpSLT extends Z3DeclKind (Z3_decl_kind.Z3_OP_SLT.toInt)  // NEW in ScalaZ3 3.0
object OpUGT extends Z3DeclKind (Z3_decl_kind.Z3_OP_UGT.toInt)  // NEW in ScalaZ3 3.0
object OpSGT extends Z3DeclKind (Z3_decl_kind.Z3_OP_SGT.toInt)  // NEW in ScalaZ3 3.0

object OpBAnd  extends Z3DeclKind (Z3_decl_kind.Z3_OP_BAND.toInt)  // NEW in ScalaZ3 3.0
object OpBOr   extends Z3DeclKind (Z3_decl_kind.Z3_OP_BOR.toInt)   // NEW in ScalaZ3 3.0
object OpBNot  extends Z3DeclKind (Z3_decl_kind.Z3_OP_BNOT.toInt)  // NEW in ScalaZ3 3.0
object OpBXor  extends Z3DeclKind (Z3_decl_kind.Z3_OP_BXOR.toInt)  // NEW in ScalaZ3 3.0
object OpBNand extends Z3DeclKind (Z3_decl_kind.Z3_OP_BNAND.toInt) // NEW in ScalaZ3 3.0
object OpBNor  extends Z3DeclKind (Z3_decl_kind.Z3_OP_BNOR.toInt)  // NEW in ScalaZ3 3.0
object OpBXnor extends Z3DeclKind (Z3_decl_kind.Z3_OP_BXNOR.toInt) // NEW in ScalaZ3 3.0

object OpConcat  extends Z3DeclKind (Z3_decl_kind.Z3_OP_CONCAT.toInt)   // NEW in ScalaZ3 3.0
object OpSignExt extends Z3DeclKind (Z3_decl_kind.Z3_OP_SIGN_EXT.toInt) // NEW in ScalaZ3 3.0
object OpZeroExt extends Z3DeclKind (Z3_decl_kind.Z3_OP_ZERO_EXT.toInt) // NEW in ScalaZ3 3.0
object OpExtract extends Z3DeclKind (Z3_decl_kind.Z3_OP_EXTRACT.toInt)  // NEW in ScalaZ3 3.0
object OpRepeat  extends Z3DeclKind (Z3_decl_kind.Z3_OP_REPEAT.toInt)   // NEW in ScalaZ3 3.0

object OpBRedAnd extends Z3DeclKind  (Z3_decl_kind.Z3_OP_BREDAND.toInt) // NEW in ScalaZ3 3.0
object OpBRedOr  extends Z3DeclKind  (Z3_decl_kind.Z3_OP_BREDOR.toInt)  // NEW in ScalaZ3 3.0
object OpBComp   extends Z3DeclKind  (Z3_decl_kind.Z3_OP_BCOMP.toInt)   // NEW in ScalaZ3 3.0

object OpBShl           extends Z3DeclKind (Z3_decl_kind.Z3_OP_BSHL.toInt)             // NEW in ScalaZ3 3.0
object OpBLshr          extends Z3DeclKind (Z3_decl_kind.Z3_OP_BLSHR.toInt)            // NEW in ScalaZ3 3.0
object OpBAshr          extends Z3DeclKind (Z3_decl_kind.Z3_OP_BASHR.toInt)            // NEW in ScalaZ3 3.0
object OpRotateLeft     extends Z3DeclKind (Z3_decl_kind.Z3_OP_ROTATE_LEFT.toInt)      // NEW in ScalaZ3 3.0
object OpRotateRight    extends Z3DeclKind (Z3_decl_kind.Z3_OP_ROTATE_RIGHT.toInt)     // NEW in ScalaZ3 3.0
object OpExtRotateLeft  extends Z3DeclKind (Z3_decl_kind.Z3_OP_EXT_ROTATE_LEFT.toInt)  // NEW in ScalaZ3 3.0
object OpExtRotateRight extends Z3DeclKind (Z3_decl_kind.Z3_OP_EXT_ROTATE_RIGHT.toInt) // NEW in ScalaZ3 3.0

object OpIntToBV extends Z3DeclKind (Z3_decl_kind.Z3_OP_INT2BV.toInt) // NEW in ScalaZ3 3.0
object OpBVToInt extends Z3DeclKind (Z3_decl_kind.Z3_OP_BV2INT.toInt) // NEW in ScalaZ3 3.0
object OpCarry   extends Z3DeclKind (Z3_decl_kind.Z3_OP_CARRY.toInt)  // NEW in ScalaZ3 3.0
object OpBSDiv0      extends Z3DeclKind (Z3_decl_kind.Z3_OP_BSDIV0.toInt) // NEW in ScalaZ3 5.4.1
object OpBUDiv0      extends Z3DeclKind (Z3_decl_kind.Z3_OP_BUDIV0.toInt) // NEW in ScalaZ3 5.4.1
object OpBSRem0      extends Z3DeclKind (Z3_decl_kind.Z3_OP_BSREM0.toInt) // NEW in ScalaZ3 5.4.1
object OpBURem0      extends Z3DeclKind (Z3_decl_kind.Z3_OP_BUREM0.toInt) // NEW in ScalaZ3 5.4.1
object OpBSMod0      extends Z3DeclKind (Z3_decl_kind.Z3_OP_BSMOD0.toInt) // NEW in ScalaZ3 5.4.1
object OpBSDivI      extends Z3DeclKind (Z3_decl_kind.Z3_OP_BSDIV_I.toInt) // NEW in ScalaZ3 5.4.1
object OpBUDivI      extends Z3DeclKind (Z3_decl_kind.Z3_OP_BUDIV_I.toInt) // NEW in ScalaZ3 5.4.1
object OpBSRemI      extends Z3DeclKind (Z3_decl_kind.Z3_OP_BSREM_I.toInt) // NEW in ScalaZ3 5.4.1
object OpBURemI      extends Z3DeclKind (Z3_decl_kind.Z3_OP_BUREM_I.toInt) // NEW in ScalaZ3 5.4.1
object OpBSModI      extends Z3DeclKind (Z3_decl_kind.Z3_OP_BSMOD_I.toInt) // NEW in ScalaZ3 5.4.1
object OpBitToBool   extends Z3DeclKind (Z3_decl_kind.Z3_OP_BIT2BOOL.toInt) // NEW in ScalaZ3 5.4.1
object OpSBVToInt    extends Z3DeclKind (Z3_decl_kind.Z3_OP_SBV2INT.toInt) // NEW in ScalaZ3 5.4.1
object OpMkBV        extends Z3DeclKind (Z3_decl_kind.Z3_OP_MKBV.toInt) // NEW in ScalaZ3 5.4.1
object OpBSMulNoOvfl extends Z3DeclKind (Z3_decl_kind.Z3_OP_BSMUL_NO_OVFL.toInt) // NEW in ScalaZ3 5.4.1
object OpBUMulNoOvfl extends Z3DeclKind (Z3_decl_kind.Z3_OP_BUMUL_NO_OVFL.toInt) // NEW in ScalaZ3 5.4.1
object OpBSMulNoUdfl extends Z3DeclKind (Z3_decl_kind.Z3_OP_BSMUL_NO_UDFL.toInt) // NEW in ScalaZ3 5.4.1
object OpXor3    extends Z3DeclKind (Z3_decl_kind.Z3_OP_XOR3.toInt)   // NEW in ScalaZ3 3.0

// Proofs
object OpPrUndef            extends Z3DeclKind (Z3_decl_kind.Z3_OP_PR_UNDEF.toInt)             // NEW in ScalaZ3 3.0
object OpPrTrue             extends Z3DeclKind (Z3_decl_kind.Z3_OP_PR_TRUE.toInt)              // NEW in ScalaZ3 3.0
object OpPrAsserted         extends Z3DeclKind (Z3_decl_kind.Z3_OP_PR_ASSERTED.toInt)          // NEW in ScalaZ3 3.0
object OpPrGoal             extends Z3DeclKind (Z3_decl_kind.Z3_OP_PR_GOAL.toInt)              // NEW in ScalaZ3 3.0
object OpPrModusPonens      extends Z3DeclKind (Z3_decl_kind.Z3_OP_PR_MODUS_PONENS.toInt)      // NEW in ScalaZ3 3.0
object OpPrReflexivity      extends Z3DeclKind (Z3_decl_kind.Z3_OP_PR_REFLEXIVITY.toInt)       // NEW in ScalaZ3 3.0
object OpPrSymmetry         extends Z3DeclKind (Z3_decl_kind.Z3_OP_PR_SYMMETRY.toInt)          // NEW in ScalaZ3 3.0
object OpPrTransitivity     extends Z3DeclKind (Z3_decl_kind.Z3_OP_PR_TRANSITIVITY.toInt)      // NEW in ScalaZ3 3.0
object OpPrTransitivityStar extends Z3DeclKind (Z3_decl_kind.Z3_OP_PR_TRANSITIVITY_STAR.toInt) // NEW in ScalaZ3 3.0
object OpPrMonotonicity     extends Z3DeclKind (Z3_decl_kind.Z3_OP_PR_MONOTONICITY.toInt)      // NEW in ScalaZ3 3.0
object OpPrQuantIntro       extends Z3DeclKind (Z3_decl_kind.Z3_OP_PR_QUANT_INTRO.toInt)       // NEW in ScalaZ3 3.0
object OpPrDistributivity   extends Z3DeclKind (Z3_decl_kind.Z3_OP_PR_DISTRIBUTIVITY.toInt)    // NEW in ScalaZ3 3.0
object OpPrAndElim          extends Z3DeclKind (Z3_decl_kind.Z3_OP_PR_AND_ELIM.toInt)          // NEW in ScalaZ3 3.0
object OpPrNotOrElim        extends Z3DeclKind (Z3_decl_kind.Z3_OP_PR_NOT_OR_ELIM.toInt)       // NEW in ScalaZ3 3.0
object OpPrRewrite          extends Z3DeclKind (Z3_decl_kind.Z3_OP_PR_REWRITE.toInt)           // NEW in ScalaZ3 3.0
object OpPrRewriteStar      extends Z3DeclKind (Z3_decl_kind.Z3_OP_PR_REWRITE_STAR.toInt)      // NEW in ScalaZ3 3.0
object OpPrPullQuant        extends Z3DeclKind (Z3_decl_kind.Z3_OP_PR_PULL_QUANT.toInt)        // NEW in ScalaZ3 3.0
object OpPrPushQuant        extends Z3DeclKind (Z3_decl_kind.Z3_OP_PR_PUSH_QUANT.toInt)        // NEW in ScalaZ3 3.0
object OpPrElimUnusedVars   extends Z3DeclKind (Z3_decl_kind.Z3_OP_PR_ELIM_UNUSED_VARS.toInt)  // NEW in ScalaZ3 3.0
object OpPrDER              extends Z3DeclKind (Z3_decl_kind.Z3_OP_PR_DER.toInt)               // NEW in ScalaZ3 3.0
object OpPrQuantInst        extends Z3DeclKind (Z3_decl_kind.Z3_OP_PR_QUANT_INST.toInt)        // NEW in ScalaZ3 3.0
object OpPrHypothesis       extends Z3DeclKind (Z3_decl_kind.Z3_OP_PR_HYPOTHESIS.toInt)        // NEW in ScalaZ3 3.0
object OpPrLemma            extends Z3DeclKind (Z3_decl_kind.Z3_OP_PR_LEMMA.toInt)             // NEW in ScalaZ3 3.0
object OpPrUnitResolution   extends Z3DeclKind (Z3_decl_kind.Z3_OP_PR_UNIT_RESOLUTION.toInt)   // NEW in ScalaZ3 3.0
object OpPrIffTrue          extends Z3DeclKind (Z3_decl_kind.Z3_OP_PR_IFF_TRUE.toInt)          // NEW in ScalaZ3 3.0
object OpPrIffFalse         extends Z3DeclKind (Z3_decl_kind.Z3_OP_PR_IFF_FALSE.toInt)         // NEW in ScalaZ3 3.0
object OpPrCommutativity    extends Z3DeclKind (Z3_decl_kind.Z3_OP_PR_COMMUTATIVITY.toInt)     // NEW in ScalaZ3 3.0
object OpPrDefAxiom         extends Z3DeclKind (Z3_decl_kind.Z3_OP_PR_DEF_AXIOM.toInt)         // NEW in ScalaZ3 3.0
object OpPrDefIntro         extends Z3DeclKind (Z3_decl_kind.Z3_OP_PR_DEF_INTRO.toInt)         // NEW in ScalaZ3 3.0
object OpPrApplyDef         extends Z3DeclKind (Z3_decl_kind.Z3_OP_PR_APPLY_DEF.toInt)         // NEW in ScalaZ3 3.0
object OpPrIffOEq           extends Z3DeclKind (Z3_decl_kind.Z3_OP_PR_IFF_OEQ.toInt)           // NEW in ScalaZ3 3.0
object OpPrNNFPos           extends Z3DeclKind (Z3_decl_kind.Z3_OP_PR_NNF_POS.toInt)           // NEW in ScalaZ3 3.0
object OpPrNNFNeg           extends Z3DeclKind (Z3_decl_kind.Z3_OP_PR_NNF_NEG.toInt)           // NEW in ScalaZ3 3.0
object OpPrSkolemize        extends Z3DeclKind (Z3_decl_kind.Z3_OP_PR_SKOLEMIZE.toInt)         // NEW in ScalaZ3 3.0
object OpPrModusPonensOEq   extends Z3DeclKind (Z3_decl_kind.Z3_OP_PR_MODUS_PONENS_OEQ.toInt)  // NEW in ScalaZ3 3.0
object OpPrThLemma          extends Z3DeclKind (Z3_decl_kind.Z3_OP_PR_TH_LEMMA.toInt)          // NEW in ScalaZ3 3.0
object OpPrBind            extends Z3DeclKind (Z3_decl_kind.Z3_OP_PR_BIND.toInt) // NEW in ScalaZ3 5.4.1
object OpPrAssumptionAdd   extends Z3DeclKind (Z3_decl_kind.Z3_OP_PR_ASSUMPTION_ADD.toInt) // NEW in ScalaZ3 5.4.1
object OpPrLemmaAdd        extends Z3DeclKind (Z3_decl_kind.Z3_OP_PR_LEMMA_ADD.toInt) // NEW in ScalaZ3 5.4.1
object OpPrRedundantDel    extends Z3DeclKind (Z3_decl_kind.Z3_OP_PR_REDUNDANT_DEL.toInt) // NEW in ScalaZ3 5.4.1
object OpPrClauseTrail     extends Z3DeclKind (Z3_decl_kind.Z3_OP_PR_CLAUSE_TRAIL.toInt) // NEW in ScalaZ3 5.4.1
object OpPrThAssumptionAdd extends Z3DeclKind (Z3_decl_kind.Z3_OP_PR_TH_ASSUMPTION_ADD.toInt) // NEW in ScalaZ3 5.4.1
object OpPrThLemmaAdd      extends Z3DeclKind (Z3_decl_kind.Z3_OP_PR_TH_LEMMA_ADD.toInt) // NEW in ScalaZ3 5.4.1
object OpPrHyperResolve     extends Z3DeclKind (Z3_decl_kind.Z3_OP_PR_HYPER_RESOLVE.toInt)     // NEW in ScalaZ3 3.0

// Relational algebra
object OpRAStore          extends Z3DeclKind (Z3_decl_kind.Z3_OP_RA_STORE.toInt)           // NEW in ScalaZ3 3.0
object OpRAEmpty          extends Z3DeclKind (Z3_decl_kind.Z3_OP_RA_EMPTY.toInt)           // NEW in ScalaZ3 3.0
object OpRAIsEmpty        extends Z3DeclKind (Z3_decl_kind.Z3_OP_RA_IS_EMPTY.toInt)        // NEW in ScalaZ3 3.0
object OpRAJoin           extends Z3DeclKind (Z3_decl_kind.Z3_OP_RA_JOIN.toInt)            // NEW in ScalaZ3 3.0
object OpRAUnion          extends Z3DeclKind (Z3_decl_kind.Z3_OP_RA_UNION.toInt)           // NEW in ScalaZ3 3.0
object OpRAWiden          extends Z3DeclKind (Z3_decl_kind.Z3_OP_RA_WIDEN.toInt)           // NEW in ScalaZ3 3.0
object OpRAProject        extends Z3DeclKind (Z3_decl_kind.Z3_OP_RA_PROJECT.toInt)         // NEW in ScalaZ3 3.0
object OpRAFilter         extends Z3DeclKind (Z3_decl_kind.Z3_OP_RA_FILTER.toInt)          // NEW in ScalaZ3 3.0
object OpRANegationFilter extends Z3DeclKind (Z3_decl_kind.Z3_OP_RA_NEGATION_FILTER.toInt) // NEW in ScalaZ3 3.0
object OpRARename         extends Z3DeclKind (Z3_decl_kind.Z3_OP_RA_RENAME.toInt)          // NEW in ScalaZ3 3.0
object OpRAComplement     extends Z3DeclKind (Z3_decl_kind.Z3_OP_RA_COMPLEMENT.toInt)      // NEW in ScalaZ3 3.0
object OpRASelect         extends Z3DeclKind (Z3_decl_kind.Z3_OP_RA_SELECT.toInt)          // NEW in ScalaZ3 3.0
object OpRAClone          extends Z3DeclKind (Z3_decl_kind.Z3_OP_RA_CLONE.toInt)           // NEW in ScalaZ3 3.0
object OpFdConstant       extends Z3DeclKind (Z3_decl_kind.Z3_OP_FD_CONSTANT.toInt)        // NEW in ScalaZ3 3.0
object OpFdLT             extends Z3DeclKind (Z3_decl_kind.Z3_OP_FD_LT.toInt)              // NEW in ScalaZ3 3.0

// Sequences
object OpSeqUnit     extends Z3DeclKind (Z3_decl_kind.Z3_OP_SEQ_UNIT.toInt)     // NEW in ScalaZ3 3.0
object OpSeqEmpty    extends Z3DeclKind (Z3_decl_kind.Z3_OP_SEQ_EMPTY.toInt)    // NEW in ScalaZ3 3.0
object OpSeqConcat   extends Z3DeclKind (Z3_decl_kind.Z3_OP_SEQ_CONCAT.toInt)   // NEW in ScalaZ3 3.0
object OpSeqPrefix   extends Z3DeclKind (Z3_decl_kind.Z3_OP_SEQ_PREFIX.toInt)   // NEW in ScalaZ3 3.0
object OpSeqSuffix   extends Z3DeclKind (Z3_decl_kind.Z3_OP_SEQ_SUFFIX.toInt)   // NEW in ScalaZ3 3.0
object OpSeqContains extends Z3DeclKind (Z3_decl_kind.Z3_OP_SEQ_CONTAINS.toInt) // NEW in ScalaZ3 3.0
object OpSeqExtract  extends Z3DeclKind (Z3_decl_kind.Z3_OP_SEQ_EXTRACT.toInt)  // NEW in ScalaZ3 3.0
object OpSeqReplace  extends Z3DeclKind (Z3_decl_kind.Z3_OP_SEQ_REPLACE.toInt)  // NEW in ScalaZ3 3.0
object OpSeqAt       extends Z3DeclKind (Z3_decl_kind.Z3_OP_SEQ_AT.toInt)       // NEW in ScalaZ3 3.0
object OpSeqLength   extends Z3DeclKind (Z3_decl_kind.Z3_OP_SEQ_LENGTH.toInt)   // NEW in ScalaZ3 3.0
object OpSeqIndex    extends Z3DeclKind (Z3_decl_kind.Z3_OP_SEQ_INDEX.toInt)    // NEW in ScalaZ3 3.0
object OpSeqToRE     extends Z3DeclKind (Z3_decl_kind.Z3_OP_SEQ_TO_RE.toInt)    // NEW in ScalaZ3 3.0
object OpSeqInRE     extends Z3DeclKind (Z3_decl_kind.Z3_OP_SEQ_IN_RE.toInt)    // NEW in ScalaZ3 3.0
object OpSeqReplaceRE    extends Z3DeclKind (Z3_decl_kind.Z3_OP_SEQ_REPLACE_RE.toInt) // NEW in ScalaZ3 5.4.1
object OpSeqReplaceREAll extends Z3DeclKind (Z3_decl_kind.Z3_OP_SEQ_REPLACE_RE_ALL.toInt) // NEW in ScalaZ3 5.4.1
object OpSeqReplaceAll   extends Z3DeclKind (Z3_decl_kind.Z3_OP_SEQ_REPLACE_ALL.toInt) // NEW in ScalaZ3 5.4.1
object OpSeqNth          extends Z3DeclKind (Z3_decl_kind.Z3_OP_SEQ_NTH.toInt) // NEW in ScalaZ3 5.4.1
object OpSeqLastIndex    extends Z3DeclKind (Z3_decl_kind.Z3_OP_SEQ_LAST_INDEX.toInt) // NEW in ScalaZ3 5.4.1
object OpSeqMap          extends Z3DeclKind (Z3_decl_kind.Z3_OP_SEQ_MAP.toInt) // NEW in ScalaZ3 5.4.1
object OpSeqMapI         extends Z3DeclKind (Z3_decl_kind.Z3_OP_SEQ_MAPI.toInt) // NEW in ScalaZ3 5.4.1
object OpSeqFoldL        extends Z3DeclKind (Z3_decl_kind.Z3_OP_SEQ_FOLDL.toInt) // NEW in ScalaZ3 5.4.1
object OpSeqFoldLI       extends Z3DeclKind (Z3_decl_kind.Z3_OP_SEQ_FOLDLI.toInt) // NEW in ScalaZ3 5.4.1
object OpSeqPower        extends Z3DeclKind (Z3_decl_kind.Z3_OP_SEQ_POWER.toInt) // NEW in ScalaZ3 5.4.1
object OpStrToInt        extends Z3DeclKind (Z3_decl_kind.Z3_OP_STR_TO_INT.toInt) // NEW in ScalaZ3 5.4.1
object OpIntToStr        extends Z3DeclKind (Z3_decl_kind.Z3_OP_INT_TO_STR.toInt) // NEW in ScalaZ3 5.4.1
object OpUBVToStr        extends Z3DeclKind (Z3_decl_kind.Z3_OP_UBV_TO_STR.toInt) // NEW in ScalaZ3 5.4.1
object OpSBVToStr        extends Z3DeclKind (Z3_decl_kind.Z3_OP_SBV_TO_STR.toInt) // NEW in ScalaZ3 5.4.1
object OpStrToCode       extends Z3DeclKind (Z3_decl_kind.Z3_OP_STR_TO_CODE.toInt) // NEW in ScalaZ3 5.4.1
object OpStrFromCode     extends Z3DeclKind (Z3_decl_kind.Z3_OP_STR_FROM_CODE.toInt) // NEW in ScalaZ3 5.4.1
object OpStringLT        extends Z3DeclKind (Z3_decl_kind.Z3_OP_STRING_LT.toInt) // NEW in ScalaZ3 5.4.1
object OpStringLE        extends Z3DeclKind (Z3_decl_kind.Z3_OP_STRING_LE.toInt) // NEW in ScalaZ3 5.4.1

// Regular expressions
object OpREPlus   extends Z3DeclKind (Z3_decl_kind.Z3_OP_RE_PLUS.toInt)   // NEW in ScalaZ3 3.0
object OpREStar   extends Z3DeclKind (Z3_decl_kind.Z3_OP_RE_STAR.toInt)   // NEW in ScalaZ3 3.0
object OpREOption extends Z3DeclKind (Z3_decl_kind.Z3_OP_RE_OPTION.toInt) // NEW in ScalaZ3 3.0
object OpREConcat extends Z3DeclKind (Z3_decl_kind.Z3_OP_RE_CONCAT.toInt) // NEW in ScalaZ3 3.0
object OpREUnion  extends Z3DeclKind (Z3_decl_kind.Z3_OP_RE_UNION.toInt)  // NEW in ScalaZ3 3.0
object OpRERange       extends Z3DeclKind (Z3_decl_kind.Z3_OP_RE_RANGE.toInt) // NEW in ScalaZ3 5.4.1
object OpREDiff        extends Z3DeclKind (Z3_decl_kind.Z3_OP_RE_DIFF.toInt) // NEW in ScalaZ3 5.4.1
object OpREIntersect   extends Z3DeclKind (Z3_decl_kind.Z3_OP_RE_INTERSECT.toInt) // NEW in ScalaZ3 5.4.1
object OpRELoop        extends Z3DeclKind (Z3_decl_kind.Z3_OP_RE_LOOP.toInt) // NEW in ScalaZ3 5.4.1
object OpREPower       extends Z3DeclKind (Z3_decl_kind.Z3_OP_RE_POWER.toInt) // NEW in ScalaZ3 5.4.1
object OpREComplement  extends Z3DeclKind (Z3_decl_kind.Z3_OP_RE_COMPLEMENT.toInt) // NEW in ScalaZ3 5.4.1
object OpREEmptySet    extends Z3DeclKind (Z3_decl_kind.Z3_OP_RE_EMPTY_SET.toInt) // NEW in ScalaZ3 5.4.1
object OpREFullSet     extends Z3DeclKind (Z3_decl_kind.Z3_OP_RE_FULL_SET.toInt) // NEW in ScalaZ3 5.4.1
object OpREFullCharSet extends Z3DeclKind (Z3_decl_kind.Z3_OP_RE_FULL_CHAR_SET.toInt) // NEW in ScalaZ3 5.4.1
object OpREOfPred      extends Z3DeclKind (Z3_decl_kind.Z3_OP_RE_OF_PRED.toInt) // NEW in ScalaZ3 5.4.1
object OpREReverse     extends Z3DeclKind (Z3_decl_kind.Z3_OP_RE_REVERSE.toInt) // NEW in ScalaZ3 5.4.1
object OpREDerivative  extends Z3DeclKind (Z3_decl_kind.Z3_OP_RE_DERIVATIVE.toInt) // NEW in ScalaZ3 5.4.1
object OpCharConst     extends Z3DeclKind (Z3_decl_kind.Z3_OP_CHAR_CONST.toInt) // NEW in ScalaZ3 5.4.1
object OpCharLE        extends Z3DeclKind (Z3_decl_kind.Z3_OP_CHAR_LE.toInt) // NEW in ScalaZ3 5.4.1
object OpCharToInt     extends Z3DeclKind (Z3_decl_kind.Z3_OP_CHAR_TO_INT.toInt) // NEW in ScalaZ3 5.4.1
object OpCharToBV      extends Z3DeclKind (Z3_decl_kind.Z3_OP_CHAR_TO_BV.toInt) // NEW in ScalaZ3 5.4.1
object OpCharFromBV    extends Z3DeclKind (Z3_decl_kind.Z3_OP_CHAR_FROM_BV.toInt) // NEW in ScalaZ3 5.4.1
object OpCharIsDigit   extends Z3DeclKind (Z3_decl_kind.Z3_OP_CHAR_IS_DIGIT.toInt) // NEW in ScalaZ3 5.4.1

// Auxiliary
object OpLabel    extends Z3DeclKind (Z3_decl_kind.Z3_OP_LABEL.toInt)     // NEW in ScalaZ3 3.0
object OpLabelLit extends Z3DeclKind (Z3_decl_kind.Z3_OP_LABEL_LIT.toInt) // NEW in ScalaZ3 3.0

// Datatypes
object OpDTConstructor extends Z3DeclKind (Z3_decl_kind.Z3_OP_DT_CONSTRUCTOR.toInt)  // NEW in ScalaZ3 3.0
object OpDTRecogniser  extends Z3DeclKind (Z3_decl_kind.Z3_OP_DT_RECOGNISER.toInt)   // NEW in ScalaZ3 3.0
object OpDTAccessor    extends Z3DeclKind (Z3_decl_kind.Z3_OP_DT_ACCESSOR.toInt)     // NEW in ScalaZ3 3.0
object OpDTUpdateField extends Z3DeclKind (Z3_decl_kind.Z3_OP_DT_UPDATE_FIELD.toInt) // NEW in ScalaZ3 3.0
object OpDTIs extends Z3DeclKind (Z3_decl_kind.Z3_OP_DT_IS.toInt) // NEW in ScalaZ3 5.4.1

// Pseudo booleans
object OpPBAtMost extends Z3DeclKind (Z3_decl_kind.Z3_OP_PB_AT_MOST.toInt) // NEW in ScalaZ3 3.0
object OpPBLE     extends Z3DeclKind (Z3_decl_kind.Z3_OP_PB_LE.toInt)      // NEW in ScalaZ3 3.0
object OpPBGE     extends Z3DeclKind (Z3_decl_kind.Z3_OP_PB_GE.toInt)      // NEW in ScalaZ3 3.0
object OpPBAtLeast          extends Z3DeclKind (Z3_decl_kind.Z3_OP_PB_AT_LEAST.toInt) // NEW in ScalaZ3 5.4.1
object OpPBEq               extends Z3DeclKind (Z3_decl_kind.Z3_OP_PB_EQ.toInt) // NEW in ScalaZ3 5.4.1
object OpSpecialRelationLO  extends Z3DeclKind (Z3_decl_kind.Z3_OP_SPECIAL_RELATION_LO.toInt) // NEW in ScalaZ3 5.4.1
object OpSpecialRelationPO  extends Z3DeclKind (Z3_decl_kind.Z3_OP_SPECIAL_RELATION_PO.toInt) // NEW in ScalaZ3 5.4.1
object OpSpecialRelationPLO extends Z3DeclKind (Z3_decl_kind.Z3_OP_SPECIAL_RELATION_PLO.toInt) // NEW in ScalaZ3 5.4.1
object OpSpecialRelationTO  extends Z3DeclKind (Z3_decl_kind.Z3_OP_SPECIAL_RELATION_TO.toInt) // NEW in ScalaZ3 5.4.1
object OpSpecialRelationTC  extends Z3DeclKind (Z3_decl_kind.Z3_OP_SPECIAL_RELATION_TC.toInt) // NEW in ScalaZ3 5.4.1
object OpSpecialRelationTRC extends Z3DeclKind (Z3_decl_kind.Z3_OP_SPECIAL_RELATION_TRC.toInt) // NEW in ScalaZ3 5.4.1

// Floating-point arithmetic
object OpFPARmNearestTiesToEven extends Z3DeclKind (Z3_decl_kind.Z3_OP_FPA_RM_NEAREST_TIES_TO_EVEN.toInt) // NEW in ScalaZ3 3.0
object OpFPARmNearestTiesToAway extends Z3DeclKind (Z3_decl_kind.Z3_OP_FPA_RM_NEAREST_TIES_TO_AWAY.toInt) // NEW in ScalaZ3 3.0
object OpFPARmTowardPositive    extends Z3DeclKind (Z3_decl_kind.Z3_OP_FPA_RM_TOWARD_POSITIVE.toInt)      // NEW in ScalaZ3 3.0
object OpFPARmTowardNegative    extends Z3DeclKind (Z3_decl_kind.Z3_OP_FPA_RM_TOWARD_NEGATIVE.toInt)      // NEW in ScalaZ3 3.0
object OpFPARmTowardZero        extends Z3DeclKind (Z3_decl_kind.Z3_OP_FPA_RM_TOWARD_ZERO.toInt)          // NEW in ScalaZ3 3.0

object OpFPANum       extends Z3DeclKind (Z3_decl_kind.Z3_OP_FPA_NUM.toInt)        // NEW in ScalaZ3 3.0
object OpFPAPlusInf   extends Z3DeclKind (Z3_decl_kind.Z3_OP_FPA_PLUS_INF.toInt)   // NEW in ScalaZ3 3.0
object OpFPAMinusInf  extends Z3DeclKind (Z3_decl_kind.Z3_OP_FPA_MINUS_INF.toInt)  // NEW in ScalaZ3 3.0
object OpFPANaN       extends Z3DeclKind (Z3_decl_kind.Z3_OP_FPA_NAN.toInt)        // NEW in ScalaZ3 3.0
object OpFPAPlusZero  extends Z3DeclKind (Z3_decl_kind.Z3_OP_FPA_PLUS_ZERO.toInt)  // NEW in ScalaZ3 3.0
object OpFPAMinusZero extends Z3DeclKind (Z3_decl_kind.Z3_OP_FPA_MINUS_ZERO.toInt) // NEW in ScalaZ3 3.0

object OpFPAAdd             extends Z3DeclKind (Z3_decl_kind.Z3_OP_FPA_ADD.toInt)               // NEW in ScalaZ3 3.0
object OpFPASub             extends Z3DeclKind (Z3_decl_kind.Z3_OP_FPA_SUB.toInt)               // NEW in ScalaZ3 3.0
object OpFPANeg             extends Z3DeclKind (Z3_decl_kind.Z3_OP_FPA_NEG.toInt)               // NEW in ScalaZ3 3.0
object OpFPAMul             extends Z3DeclKind (Z3_decl_kind.Z3_OP_FPA_MUL.toInt)               // NEW in ScalaZ3 3.0
object OpFPADiv             extends Z3DeclKind (Z3_decl_kind.Z3_OP_FPA_DIV.toInt)               // NEW in ScalaZ3 3.0
object OpFPARem             extends Z3DeclKind (Z3_decl_kind.Z3_OP_FPA_REM.toInt)               // NEW in ScalaZ3 3.0
object OpFPAAbs             extends Z3DeclKind (Z3_decl_kind.Z3_OP_FPA_ABS.toInt)               // NEW in ScalaZ3 3.0
object OpFPAMin             extends Z3DeclKind (Z3_decl_kind.Z3_OP_FPA_MIN.toInt)               // NEW in ScalaZ3 3.0
object OpFPAMax             extends Z3DeclKind (Z3_decl_kind.Z3_OP_FPA_MAX.toInt)               // NEW in ScalaZ3 3.0
object OpFPAFMA             extends Z3DeclKind (Z3_decl_kind.Z3_OP_FPA_FMA.toInt)               // NEW in ScalaZ3 3.0
object OpFPASqrt            extends Z3DeclKind (Z3_decl_kind.Z3_OP_FPA_SQRT.toInt)              // NEW in ScalaZ3 3.0
object OpFPARoundToIntegral extends Z3DeclKind (Z3_decl_kind.Z3_OP_FPA_ROUND_TO_INTEGRAL.toInt) // NEW in ScalaZ3 3.0

object OpFPAEq          extends Z3DeclKind (Z3_decl_kind.Z3_OP_FPA_EQ.toInt)           // NEW in ScalaZ3 3.0
object OpFPALT          extends Z3DeclKind (Z3_decl_kind.Z3_OP_FPA_LT.toInt)           // NEW in ScalaZ3 3.0
object OpFPAGT          extends Z3DeclKind (Z3_decl_kind.Z3_OP_FPA_GT.toInt)           // NEW in ScalaZ3 3.0
object OpFPALE          extends Z3DeclKind (Z3_decl_kind.Z3_OP_FPA_LE.toInt)           // NEW in ScalaZ3 3.0
object OpFPAGE          extends Z3DeclKind (Z3_decl_kind.Z3_OP_FPA_GE.toInt)           // NEW in ScalaZ3 3.0
object OpFPAIsNaN       extends Z3DeclKind (Z3_decl_kind.Z3_OP_FPA_IS_NAN.toInt)       // NEW in ScalaZ3 3.0
object OpFPAIsInf       extends Z3DeclKind (Z3_decl_kind.Z3_OP_FPA_IS_INF.toInt)       // NEW in ScalaZ3 3.0
object OpFPAIsZero      extends Z3DeclKind (Z3_decl_kind.Z3_OP_FPA_IS_ZERO.toInt)      // NEW in ScalaZ3 3.0
object OpFPAIsNormal    extends Z3DeclKind (Z3_decl_kind.Z3_OP_FPA_IS_NORMAL.toInt)    // NEW in ScalaZ3 3.0
object OpFPAIsSubnormal extends Z3DeclKind (Z3_decl_kind.Z3_OP_FPA_IS_SUBNORMAL.toInt) // NEW in ScalaZ3 3.0
object OpFPAIsNegative  extends Z3DeclKind (Z3_decl_kind.Z3_OP_FPA_IS_NEGATIVE.toInt)  // NEW in ScalaZ3 3.0
object OpFPAIsPositive  extends Z3DeclKind (Z3_decl_kind.Z3_OP_FPA_IS_POSITIVE.toInt)  // NEW in ScalaZ3 3.0

object OpFPAFP           extends Z3DeclKind (Z3_decl_kind.Z3_OP_FPA_FP.toInt)             // NEW in ScalaZ3 3.0
object OpFPAToFP         extends Z3DeclKind (Z3_decl_kind.Z3_OP_FPA_TO_FP.toInt)          // NEW in ScalaZ3 3.0
object OpFPAToFPUnsigned extends Z3DeclKind (Z3_decl_kind.Z3_OP_FPA_TO_FP_UNSIGNED.toInt) // NEW in ScalaZ3 3.0
object OpFPAToUBV        extends Z3DeclKind (Z3_decl_kind.Z3_OP_FPA_TO_UBV.toInt)         // NEW in ScalaZ3 3.0
object OpFPAToSBV        extends Z3DeclKind (Z3_decl_kind.Z3_OP_FPA_TO_SBV.toInt)         // NEW in ScalaZ3 3.0
object OpFPAToReal       extends Z3DeclKind (Z3_decl_kind.Z3_OP_FPA_TO_REAL.toInt)        // NEW in ScalaZ3 3.0

object OpFPAToIEEEBV extends Z3DeclKind (Z3_decl_kind.Z3_OP_FPA_TO_IEEE_BV.toInt) // NEW in ScalaZ3 3.0
object OpFPABVWrap           extends Z3DeclKind (Z3_decl_kind.Z3_OP_FPA_BVWRAP.toInt) // NEW in ScalaZ3 5.4.1
object OpFPABVToRM           extends Z3DeclKind (Z3_decl_kind.Z3_OP_FPA_BV2RM.toInt) // NEW in ScalaZ3 5.4.1
object OpFiniteSetEmpty      extends Z3DeclKind (Z3_decl_kind.Z3_OP_FINITE_SET_EMPTY.toInt) // NEW in ScalaZ3 5.4.1
object OpFiniteSetSingleton  extends Z3DeclKind (Z3_decl_kind.Z3_OP_FINITE_SET_SINGLETON.toInt) // NEW in ScalaZ3 5.4.1
object OpFiniteSetUnion      extends Z3DeclKind (Z3_decl_kind.Z3_OP_FINITE_SET_UNION.toInt) // NEW in ScalaZ3 5.4.1
object OpFiniteSetIntersect  extends Z3DeclKind (Z3_decl_kind.Z3_OP_FINITE_SET_INTERSECT.toInt) // NEW in ScalaZ3 5.4.1
object OpFiniteSetDifference extends Z3DeclKind (Z3_decl_kind.Z3_OP_FINITE_SET_DIFFERENCE.toInt) // NEW in ScalaZ3 5.4.1
object OpFiniteSetIn         extends Z3DeclKind (Z3_decl_kind.Z3_OP_FINITE_SET_IN.toInt) // NEW in ScalaZ3 5.4.1
object OpFiniteSetSize       extends Z3DeclKind (Z3_decl_kind.Z3_OP_FINITE_SET_SIZE.toInt) // NEW in ScalaZ3 5.4.1
object OpFiniteSetSubset     extends Z3DeclKind (Z3_decl_kind.Z3_OP_FINITE_SET_SUBSET.toInt) // NEW in ScalaZ3 5.4.1
object OpFiniteSetMap        extends Z3DeclKind (Z3_decl_kind.Z3_OP_FINITE_SET_MAP.toInt) // NEW in ScalaZ3 5.4.1
object OpFiniteSetFilter     extends Z3DeclKind (Z3_decl_kind.Z3_OP_FINITE_SET_FILTER.toInt) // NEW in ScalaZ3 5.4.1
object OpFiniteSetRange      extends Z3DeclKind (Z3_decl_kind.Z3_OP_FINITE_SET_RANGE.toInt) // NEW in ScalaZ3 5.4.1
object OpFiniteSetExt        extends Z3DeclKind (Z3_decl_kind.Z3_OP_FINITE_SET_EXT.toInt) // NEW in ScalaZ3 5.4.1
object OpFiniteSetMapInverse extends Z3DeclKind (Z3_decl_kind.Z3_OP_FINITE_SET_MAP_INVERSE.toInt) // NEW in ScalaZ3 5.4.1
object OpRecursive           extends Z3DeclKind (Z3_decl_kind.Z3_OP_RECURSIVE.toInt) // NEW in ScalaZ3 5.4.1
object OpFFNum               extends Z3DeclKind (Z3_decl_kind.Z3_OP_FF_NUM.toInt) // NEW in ScalaZ3 5.4.1
object OpFFAdd               extends Z3DeclKind (Z3_decl_kind.Z3_OP_FF_ADD.toInt) // NEW in ScalaZ3 5.4.1
object OpFFMul               extends Z3DeclKind (Z3_decl_kind.Z3_OP_FF_MUL.toInt) // NEW in ScalaZ3 5.4.1
object OpFFNeg               extends Z3DeclKind (Z3_decl_kind.Z3_OP_FF_NEG.toInt) // NEW in ScalaZ3 5.4.1
object OpFFBitSum            extends Z3DeclKind (Z3_decl_kind.Z3_OP_FF_BITSUM.toInt) // NEW in ScalaZ3 5.4.1

object OpInternal extends Z3DeclKind (Z3_decl_kind.Z3_OP_INTERNAL.toInt)
object OpUninterpreted extends Z3DeclKind (Z3_decl_kind.Z3_OP_UNINTERPRETED.toInt)

object Other extends Z3DeclKind (9999)

object Z3DeclKind {

  def fromInt(i: Int): Z3DeclKind = {
    if (i == 9999) Other
    else fromZ3(Z3_decl_kind.fromInt(i))
  }

  def fromZ3(kind: Z3_decl_kind): Z3DeclKind = kind match {
    case Z3_decl_kind.Z3_OP_TRUE  => OpTrue
    case Z3_decl_kind.Z3_OP_FALSE => OpFalse
    case Z3_decl_kind.Z3_OP_EQ => OpEq
    case Z3_decl_kind.Z3_OP_DISTINCT => OpDistinct
    case Z3_decl_kind.Z3_OP_ITE => OpITE
    case Z3_decl_kind.Z3_OP_AND => OpAnd
    case Z3_decl_kind.Z3_OP_OR => OpOr
    case Z3_decl_kind.Z3_OP_IFF => OpIff
    case Z3_decl_kind.Z3_OP_XOR => OpXor
    case Z3_decl_kind.Z3_OP_NOT => OpNot
    case Z3_decl_kind.Z3_OP_IMPLIES => OpImplies
    case Z3_decl_kind.Z3_OP_OEQ => OpOEq

    case Z3_decl_kind.Z3_OP_ANUM => OpANum
    case Z3_decl_kind.Z3_OP_AGNUM => OpAGNum
    case Z3_decl_kind.Z3_OP_LE => OpLE
    case Z3_decl_kind.Z3_OP_GE => OpGE
    case Z3_decl_kind.Z3_OP_LT => OpLT
    case Z3_decl_kind.Z3_OP_GT => OpGT
    case Z3_decl_kind.Z3_OP_ADD => OpAdd
    case Z3_decl_kind.Z3_OP_SUB => OpSub
    case Z3_decl_kind.Z3_OP_UMINUS => OpUMinus
    case Z3_decl_kind.Z3_OP_MUL => OpMul
    case Z3_decl_kind.Z3_OP_DIV => OpDiv
    case Z3_decl_kind.Z3_OP_IDIV => OpIDiv
    case Z3_decl_kind.Z3_OP_REM => OpRem
    case Z3_decl_kind.Z3_OP_MOD => OpMod
    case Z3_decl_kind.Z3_OP_TO_REAL => OpToReal
    case Z3_decl_kind.Z3_OP_TO_INT => OpToInt
    case Z3_decl_kind.Z3_OP_IS_INT => OpIsInt
    case Z3_decl_kind.Z3_OP_POWER => OpPower
    case Z3_decl_kind.Z3_OP_ABS => OpAbs

    case Z3_decl_kind.Z3_OP_STORE => OpStore
    case Z3_decl_kind.Z3_OP_SELECT => OpSelect
    case Z3_decl_kind.Z3_OP_CONST_ARRAY => OpConstArray
    case Z3_decl_kind.Z3_OP_ARRAY_MAP => OpArrayMap
    case Z3_decl_kind.Z3_OP_ARRAY_DEFAULT => OpArrayDefault
    case Z3_decl_kind.Z3_OP_SET_UNION => OpSetUnion
    case Z3_decl_kind.Z3_OP_SET_INTERSECT => OpSetIntersect
    case Z3_decl_kind.Z3_OP_SET_DIFFERENCE => OpSetDifference
    case Z3_decl_kind.Z3_OP_SET_COMPLEMENT => OpSetComplement
    case Z3_decl_kind.Z3_OP_SET_SUBSET => OpSetSubset
    case Z3_decl_kind.Z3_OP_AS_ARRAY => OpAsArray
    case Z3_decl_kind.Z3_OP_ARRAY_EXT => OpArrayExt

    case Z3_decl_kind.Z3_OP_BNUM => OpBNum
    case Z3_decl_kind.Z3_OP_BIT1 => OpBit1
    case Z3_decl_kind.Z3_OP_BIT0 => OpBit0
    case Z3_decl_kind.Z3_OP_BNEG => OpBNeg
    case Z3_decl_kind.Z3_OP_BADD => OpBAdd
    case Z3_decl_kind.Z3_OP_BSUB => OpBSub
    case Z3_decl_kind.Z3_OP_BMUL => OpBMul

    case Z3_decl_kind.Z3_OP_BSDIV => OpBSDiv
    case Z3_decl_kind.Z3_OP_BUDIV => OpBUDiv
    case Z3_decl_kind.Z3_OP_BSREM => OpBSRem
    case Z3_decl_kind.Z3_OP_BUREM => OpBURem
    case Z3_decl_kind.Z3_OP_BSMOD => OpBSMod

    case Z3_decl_kind.Z3_OP_ULEQ => OpULE
    case Z3_decl_kind.Z3_OP_SLEQ => OpSLE
    case Z3_decl_kind.Z3_OP_UGEQ => OpUGE
    case Z3_decl_kind.Z3_OP_SGEQ => OpSGE
    case Z3_decl_kind.Z3_OP_ULT => OpULT
    case Z3_decl_kind.Z3_OP_SLT => OpSLT
    case Z3_decl_kind.Z3_OP_UGT => OpUGT
    case Z3_decl_kind.Z3_OP_SGT => OpSGT

    case Z3_decl_kind.Z3_OP_BAND => OpBAnd
    case Z3_decl_kind.Z3_OP_BOR => OpBOr
    case Z3_decl_kind.Z3_OP_BNOT => OpBNot
    case Z3_decl_kind.Z3_OP_BXOR => OpBXor
    case Z3_decl_kind.Z3_OP_BNAND => OpBNand
    case Z3_decl_kind.Z3_OP_BNOR => OpBNor
    case Z3_decl_kind.Z3_OP_BXNOR => OpBXnor

    case Z3_decl_kind.Z3_OP_CONCAT => OpConcat
    case Z3_decl_kind.Z3_OP_SIGN_EXT => OpSignExt
    case Z3_decl_kind.Z3_OP_ZERO_EXT => OpZeroExt
    case Z3_decl_kind.Z3_OP_EXTRACT => OpExtract
    case Z3_decl_kind.Z3_OP_REPEAT => OpRepeat

    case Z3_decl_kind.Z3_OP_BREDOR => OpBRedOr
    case Z3_decl_kind.Z3_OP_BREDAND => OpBRedAnd
    case Z3_decl_kind.Z3_OP_BCOMP => OpBComp

    case Z3_decl_kind.Z3_OP_BSHL => OpBShl
    case Z3_decl_kind.Z3_OP_BLSHR => OpBLshr
    case Z3_decl_kind.Z3_OP_BASHR => OpBAshr
    case Z3_decl_kind.Z3_OP_ROTATE_LEFT => OpRotateLeft
    case Z3_decl_kind.Z3_OP_ROTATE_RIGHT => OpRotateRight
    case Z3_decl_kind.Z3_OP_EXT_ROTATE_LEFT => OpExtRotateLeft
    case Z3_decl_kind.Z3_OP_EXT_ROTATE_RIGHT => OpExtRotateRight

    case Z3_decl_kind.Z3_OP_INT2BV => OpIntToBV
    case Z3_decl_kind.Z3_OP_BV2INT => OpBVToInt
    case Z3_decl_kind.Z3_OP_CARRY => OpCarry
    case Z3_decl_kind.Z3_OP_XOR3 => OpXor3

    case Z3_decl_kind.Z3_OP_PR_UNDEF => OpPrUndef
    case Z3_decl_kind.Z3_OP_PR_TRUE => OpPrTrue
    case Z3_decl_kind.Z3_OP_PR_ASSERTED => OpPrAsserted
    case Z3_decl_kind.Z3_OP_PR_GOAL => OpPrGoal
    case Z3_decl_kind.Z3_OP_PR_MODUS_PONENS => OpPrModusPonens
    case Z3_decl_kind.Z3_OP_PR_REFLEXIVITY => OpPrReflexivity
    case Z3_decl_kind.Z3_OP_PR_SYMMETRY => OpPrSymmetry
    case Z3_decl_kind.Z3_OP_PR_TRANSITIVITY => OpPrTransitivity
    case Z3_decl_kind.Z3_OP_PR_TRANSITIVITY_STAR => OpPrTransitivityStar
    case Z3_decl_kind.Z3_OP_PR_MONOTONICITY => OpPrMonotonicity
    case Z3_decl_kind.Z3_OP_PR_QUANT_INTRO => OpPrQuantIntro
    case Z3_decl_kind.Z3_OP_PR_DISTRIBUTIVITY => OpPrDistributivity
    case Z3_decl_kind.Z3_OP_PR_AND_ELIM => OpPrAndElim
    case Z3_decl_kind.Z3_OP_PR_NOT_OR_ELIM => OpPrNotOrElim
    case Z3_decl_kind.Z3_OP_PR_REWRITE => OpPrRewrite
    case Z3_decl_kind.Z3_OP_PR_REWRITE_STAR => OpPrRewriteStar
    case Z3_decl_kind.Z3_OP_PR_PULL_QUANT => OpPrPullQuant
    case Z3_decl_kind.Z3_OP_PR_PUSH_QUANT => OpPrPushQuant
    case Z3_decl_kind.Z3_OP_PR_ELIM_UNUSED_VARS => OpPrElimUnusedVars
    case Z3_decl_kind.Z3_OP_PR_DER => OpPrDER
    case Z3_decl_kind.Z3_OP_PR_QUANT_INST => OpPrQuantInst
    case Z3_decl_kind.Z3_OP_PR_HYPOTHESIS => OpPrHypothesis
    case Z3_decl_kind.Z3_OP_PR_LEMMA => OpPrLemma
    case Z3_decl_kind.Z3_OP_PR_UNIT_RESOLUTION => OpPrUnitResolution
    case Z3_decl_kind.Z3_OP_PR_IFF_TRUE => OpPrIffTrue
    case Z3_decl_kind.Z3_OP_PR_IFF_FALSE => OpPrIffFalse
    case Z3_decl_kind.Z3_OP_PR_COMMUTATIVITY => OpPrCommutativity
    case Z3_decl_kind.Z3_OP_PR_DEF_AXIOM => OpPrDefAxiom
    case Z3_decl_kind.Z3_OP_PR_DEF_INTRO => OpPrDefIntro
    case Z3_decl_kind.Z3_OP_PR_APPLY_DEF => OpPrApplyDef
    case Z3_decl_kind.Z3_OP_PR_IFF_OEQ => OpPrIffOEq
    case Z3_decl_kind.Z3_OP_PR_NNF_POS => OpPrNNFPos
    case Z3_decl_kind.Z3_OP_PR_NNF_NEG => OpPrNNFNeg
    case Z3_decl_kind.Z3_OP_PR_SKOLEMIZE => OpPrSkolemize
    case Z3_decl_kind.Z3_OP_PR_MODUS_PONENS_OEQ => OpPrModusPonensOEq
    case Z3_decl_kind.Z3_OP_PR_TH_LEMMA => OpPrThLemma
    case Z3_decl_kind.Z3_OP_PR_HYPER_RESOLVE => OpPrHyperResolve

    case Z3_decl_kind.Z3_OP_RA_STORE => OpRAStore
    case Z3_decl_kind.Z3_OP_RA_EMPTY => OpRAEmpty
    case Z3_decl_kind.Z3_OP_RA_IS_EMPTY => OpRAIsEmpty
    case Z3_decl_kind.Z3_OP_RA_JOIN => OpRAJoin
    case Z3_decl_kind.Z3_OP_RA_UNION => OpRAUnion
    case Z3_decl_kind.Z3_OP_RA_WIDEN => OpRAWiden
    case Z3_decl_kind.Z3_OP_RA_PROJECT => OpRAProject
    case Z3_decl_kind.Z3_OP_RA_FILTER => OpRAFilter
    case Z3_decl_kind.Z3_OP_RA_NEGATION_FILTER => OpRANegationFilter
    case Z3_decl_kind.Z3_OP_RA_RENAME => OpRARename
    case Z3_decl_kind.Z3_OP_RA_COMPLEMENT => OpRAComplement
    case Z3_decl_kind.Z3_OP_RA_SELECT => OpRASelect
    case Z3_decl_kind.Z3_OP_RA_CLONE => OpRAClone
    case Z3_decl_kind.Z3_OP_FD_CONSTANT => OpFdConstant
    case Z3_decl_kind.Z3_OP_FD_LT => OpFdLT

    case Z3_decl_kind.Z3_OP_SEQ_UNIT => OpSeqUnit
    case Z3_decl_kind.Z3_OP_SEQ_EMPTY => OpSeqEmpty
    case Z3_decl_kind.Z3_OP_SEQ_CONCAT => OpSeqConcat
    case Z3_decl_kind.Z3_OP_SEQ_PREFIX => OpSeqPrefix
    case Z3_decl_kind.Z3_OP_SEQ_SUFFIX => OpSeqSuffix
    case Z3_decl_kind.Z3_OP_SEQ_CONTAINS => OpSeqContains
    case Z3_decl_kind.Z3_OP_SEQ_EXTRACT => OpSeqExtract
    case Z3_decl_kind.Z3_OP_SEQ_REPLACE => OpSeqReplace
    case Z3_decl_kind.Z3_OP_SEQ_AT => OpSeqAt
    case Z3_decl_kind.Z3_OP_SEQ_LENGTH => OpSeqLength
    case Z3_decl_kind.Z3_OP_SEQ_INDEX => OpSeqIndex
    case Z3_decl_kind.Z3_OP_SEQ_TO_RE => OpSeqToRE
    case Z3_decl_kind.Z3_OP_SEQ_IN_RE => OpSeqInRE

    case Z3_decl_kind.Z3_OP_RE_PLUS => OpREPlus
    case Z3_decl_kind.Z3_OP_RE_STAR => OpREStar
    case Z3_decl_kind.Z3_OP_RE_OPTION => OpREOption
    case Z3_decl_kind.Z3_OP_RE_CONCAT => OpREConcat
    case Z3_decl_kind.Z3_OP_RE_UNION => OpREUnion

    case Z3_decl_kind.Z3_OP_LABEL => OpLabel
    case Z3_decl_kind.Z3_OP_LABEL_LIT => OpLabelLit

    case Z3_decl_kind.Z3_OP_DT_CONSTRUCTOR => OpDTConstructor
    case Z3_decl_kind.Z3_OP_DT_RECOGNISER => OpDTRecogniser
    case Z3_decl_kind.Z3_OP_DT_ACCESSOR => OpDTAccessor
    case Z3_decl_kind.Z3_OP_DT_UPDATE_FIELD => OpDTUpdateField

    case Z3_decl_kind.Z3_OP_PB_AT_MOST => OpPBAtMost
    case Z3_decl_kind.Z3_OP_PB_LE => OpPBLE
    case Z3_decl_kind.Z3_OP_PB_GE => OpPBGE

    case Z3_decl_kind.Z3_OP_FPA_RM_NEAREST_TIES_TO_EVEN => OpFPARmNearestTiesToEven
    case Z3_decl_kind.Z3_OP_FPA_RM_NEAREST_TIES_TO_AWAY => OpFPARmNearestTiesToAway
    case Z3_decl_kind.Z3_OP_FPA_RM_TOWARD_POSITIVE => OpFPARmTowardPositive
    case Z3_decl_kind.Z3_OP_FPA_RM_TOWARD_NEGATIVE => OpFPARmTowardNegative
    case Z3_decl_kind.Z3_OP_FPA_RM_TOWARD_ZERO => OpFPARmTowardZero

    case Z3_decl_kind.Z3_OP_FPA_NUM => OpFPANum
    case Z3_decl_kind.Z3_OP_FPA_PLUS_INF => OpFPAPlusInf
    case Z3_decl_kind.Z3_OP_FPA_MINUS_INF => OpFPAMinusInf
    case Z3_decl_kind.Z3_OP_FPA_NAN => OpFPANaN
    case Z3_decl_kind.Z3_OP_FPA_PLUS_ZERO => OpFPAPlusZero
    case Z3_decl_kind.Z3_OP_FPA_MINUS_ZERO => OpFPAMinusZero

    case Z3_decl_kind.Z3_OP_FPA_ADD => OpFPAAdd
    case Z3_decl_kind.Z3_OP_FPA_SUB => OpFPASub
    case Z3_decl_kind.Z3_OP_FPA_NEG => OpFPANeg
    case Z3_decl_kind.Z3_OP_FPA_MUL => OpFPAMul
    case Z3_decl_kind.Z3_OP_FPA_DIV => OpFPADiv
    case Z3_decl_kind.Z3_OP_FPA_REM => OpFPARem
    case Z3_decl_kind.Z3_OP_FPA_ABS => OpFPAAbs
    case Z3_decl_kind.Z3_OP_FPA_MIN => OpFPAMin
    case Z3_decl_kind.Z3_OP_FPA_MAX => OpFPAMax
    case Z3_decl_kind.Z3_OP_FPA_FMA => OpFPAFMA
    case Z3_decl_kind.Z3_OP_FPA_SQRT => OpFPASqrt
    case Z3_decl_kind.Z3_OP_FPA_ROUND_TO_INTEGRAL => OpFPARoundToIntegral

    case Z3_decl_kind.Z3_OP_FPA_EQ => OpFPAEq
    case Z3_decl_kind.Z3_OP_FPA_LT => OpFPALT
    case Z3_decl_kind.Z3_OP_FPA_GT => OpFPAGT
    case Z3_decl_kind.Z3_OP_FPA_LE => OpFPALE
    case Z3_decl_kind.Z3_OP_FPA_GE => OpFPAGE
    case Z3_decl_kind.Z3_OP_FPA_IS_NAN => OpFPAIsNaN
    case Z3_decl_kind.Z3_OP_FPA_IS_INF => OpFPAIsInf
    case Z3_decl_kind.Z3_OP_FPA_IS_ZERO => OpFPAIsZero
    case Z3_decl_kind.Z3_OP_FPA_IS_NORMAL => OpFPAIsNormal
    case Z3_decl_kind.Z3_OP_FPA_IS_SUBNORMAL => OpFPAIsSubnormal
    case Z3_decl_kind.Z3_OP_FPA_IS_NEGATIVE => OpFPAIsNegative
    case Z3_decl_kind.Z3_OP_FPA_IS_POSITIVE => OpFPAIsPositive

    case Z3_decl_kind.Z3_OP_FPA_FP => OpFPAFP
    case Z3_decl_kind.Z3_OP_FPA_TO_FP => OpFPAToFP
    case Z3_decl_kind.Z3_OP_FPA_TO_FP_UNSIGNED => OpFPAToFPUnsigned
    case Z3_decl_kind.Z3_OP_FPA_TO_UBV => OpFPAToUBV
    case Z3_decl_kind.Z3_OP_FPA_TO_SBV => OpFPAToSBV
    case Z3_decl_kind.Z3_OP_FPA_TO_REAL => OpFPAToReal

    case Z3_decl_kind.Z3_OP_FPA_TO_IEEE_BV => OpFPAToIEEEBV

    // NEW in ScalaZ3 5.4.1
    case Z3_decl_kind.Z3_OP_COMPLEX => OpComplex
    case Z3_decl_kind.Z3_OP_REAL_PART => OpRealPart
    case Z3_decl_kind.Z3_OP_IMAG_PART => OpImagPart
    case Z3_decl_kind.Z3_OP_BSDIV0 => OpBSDiv0
    case Z3_decl_kind.Z3_OP_BUDIV0 => OpBUDiv0
    case Z3_decl_kind.Z3_OP_BSREM0 => OpBSRem0
    case Z3_decl_kind.Z3_OP_BUREM0 => OpBURem0
    case Z3_decl_kind.Z3_OP_BSMOD0 => OpBSMod0
    case Z3_decl_kind.Z3_OP_BSDIV_I => OpBSDivI
    case Z3_decl_kind.Z3_OP_BUDIV_I => OpBUDivI
    case Z3_decl_kind.Z3_OP_BSREM_I => OpBSRemI
    case Z3_decl_kind.Z3_OP_BUREM_I => OpBURemI
    case Z3_decl_kind.Z3_OP_BSMOD_I => OpBSModI
    case Z3_decl_kind.Z3_OP_BIT2BOOL => OpBitToBool
    case Z3_decl_kind.Z3_OP_SBV2INT => OpSBVToInt
    case Z3_decl_kind.Z3_OP_MKBV => OpMkBV
    case Z3_decl_kind.Z3_OP_BSMUL_NO_OVFL => OpBSMulNoOvfl
    case Z3_decl_kind.Z3_OP_BUMUL_NO_OVFL => OpBUMulNoOvfl
    case Z3_decl_kind.Z3_OP_BSMUL_NO_UDFL => OpBSMulNoUdfl
    case Z3_decl_kind.Z3_OP_PR_BIND => OpPrBind
    case Z3_decl_kind.Z3_OP_PR_ASSUMPTION_ADD => OpPrAssumptionAdd
    case Z3_decl_kind.Z3_OP_PR_LEMMA_ADD => OpPrLemmaAdd
    case Z3_decl_kind.Z3_OP_PR_REDUNDANT_DEL => OpPrRedundantDel
    case Z3_decl_kind.Z3_OP_PR_CLAUSE_TRAIL => OpPrClauseTrail
    case Z3_decl_kind.Z3_OP_PR_TH_ASSUMPTION_ADD => OpPrThAssumptionAdd
    case Z3_decl_kind.Z3_OP_PR_TH_LEMMA_ADD => OpPrThLemmaAdd
    case Z3_decl_kind.Z3_OP_SEQ_REPLACE_RE => OpSeqReplaceRE
    case Z3_decl_kind.Z3_OP_SEQ_REPLACE_RE_ALL => OpSeqReplaceREAll
    case Z3_decl_kind.Z3_OP_SEQ_REPLACE_ALL => OpSeqReplaceAll
    case Z3_decl_kind.Z3_OP_SEQ_NTH => OpSeqNth
    case Z3_decl_kind.Z3_OP_SEQ_LAST_INDEX => OpSeqLastIndex
    case Z3_decl_kind.Z3_OP_SEQ_MAP => OpSeqMap
    case Z3_decl_kind.Z3_OP_SEQ_MAPI => OpSeqMapI
    case Z3_decl_kind.Z3_OP_SEQ_FOLDL => OpSeqFoldL
    case Z3_decl_kind.Z3_OP_SEQ_FOLDLI => OpSeqFoldLI
    case Z3_decl_kind.Z3_OP_SEQ_POWER => OpSeqPower
    case Z3_decl_kind.Z3_OP_STR_TO_INT => OpStrToInt
    case Z3_decl_kind.Z3_OP_INT_TO_STR => OpIntToStr
    case Z3_decl_kind.Z3_OP_UBV_TO_STR => OpUBVToStr
    case Z3_decl_kind.Z3_OP_SBV_TO_STR => OpSBVToStr
    case Z3_decl_kind.Z3_OP_STR_TO_CODE => OpStrToCode
    case Z3_decl_kind.Z3_OP_STR_FROM_CODE => OpStrFromCode
    case Z3_decl_kind.Z3_OP_STRING_LT => OpStringLT
    case Z3_decl_kind.Z3_OP_STRING_LE => OpStringLE
    case Z3_decl_kind.Z3_OP_RE_RANGE => OpRERange
    case Z3_decl_kind.Z3_OP_RE_DIFF => OpREDiff
    case Z3_decl_kind.Z3_OP_RE_INTERSECT => OpREIntersect
    case Z3_decl_kind.Z3_OP_RE_LOOP => OpRELoop
    case Z3_decl_kind.Z3_OP_RE_POWER => OpREPower
    case Z3_decl_kind.Z3_OP_RE_COMPLEMENT => OpREComplement
    case Z3_decl_kind.Z3_OP_RE_EMPTY_SET => OpREEmptySet
    case Z3_decl_kind.Z3_OP_RE_FULL_SET => OpREFullSet
    case Z3_decl_kind.Z3_OP_RE_FULL_CHAR_SET => OpREFullCharSet
    case Z3_decl_kind.Z3_OP_RE_OF_PRED => OpREOfPred
    case Z3_decl_kind.Z3_OP_RE_REVERSE => OpREReverse
    case Z3_decl_kind.Z3_OP_RE_DERIVATIVE => OpREDerivative
    case Z3_decl_kind.Z3_OP_CHAR_CONST => OpCharConst
    case Z3_decl_kind.Z3_OP_CHAR_LE => OpCharLE
    case Z3_decl_kind.Z3_OP_CHAR_TO_INT => OpCharToInt
    case Z3_decl_kind.Z3_OP_CHAR_TO_BV => OpCharToBV
    case Z3_decl_kind.Z3_OP_CHAR_FROM_BV => OpCharFromBV
    case Z3_decl_kind.Z3_OP_CHAR_IS_DIGIT => OpCharIsDigit
    case Z3_decl_kind.Z3_OP_DT_IS => OpDTIs
    case Z3_decl_kind.Z3_OP_PB_AT_LEAST => OpPBAtLeast
    case Z3_decl_kind.Z3_OP_PB_EQ => OpPBEq
    case Z3_decl_kind.Z3_OP_SPECIAL_RELATION_LO => OpSpecialRelationLO
    case Z3_decl_kind.Z3_OP_SPECIAL_RELATION_PO => OpSpecialRelationPO
    case Z3_decl_kind.Z3_OP_SPECIAL_RELATION_PLO => OpSpecialRelationPLO
    case Z3_decl_kind.Z3_OP_SPECIAL_RELATION_TO => OpSpecialRelationTO
    case Z3_decl_kind.Z3_OP_SPECIAL_RELATION_TC => OpSpecialRelationTC
    case Z3_decl_kind.Z3_OP_SPECIAL_RELATION_TRC => OpSpecialRelationTRC
    case Z3_decl_kind.Z3_OP_FPA_BVWRAP => OpFPABVWrap
    case Z3_decl_kind.Z3_OP_FPA_BV2RM => OpFPABVToRM
    case Z3_decl_kind.Z3_OP_FINITE_SET_EMPTY => OpFiniteSetEmpty
    case Z3_decl_kind.Z3_OP_FINITE_SET_SINGLETON => OpFiniteSetSingleton
    case Z3_decl_kind.Z3_OP_FINITE_SET_UNION => OpFiniteSetUnion
    case Z3_decl_kind.Z3_OP_FINITE_SET_INTERSECT => OpFiniteSetIntersect
    case Z3_decl_kind.Z3_OP_FINITE_SET_DIFFERENCE => OpFiniteSetDifference
    case Z3_decl_kind.Z3_OP_FINITE_SET_IN => OpFiniteSetIn
    case Z3_decl_kind.Z3_OP_FINITE_SET_SIZE => OpFiniteSetSize
    case Z3_decl_kind.Z3_OP_FINITE_SET_SUBSET => OpFiniteSetSubset
    case Z3_decl_kind.Z3_OP_FINITE_SET_MAP => OpFiniteSetMap
    case Z3_decl_kind.Z3_OP_FINITE_SET_FILTER => OpFiniteSetFilter
    case Z3_decl_kind.Z3_OP_FINITE_SET_RANGE => OpFiniteSetRange
    case Z3_decl_kind.Z3_OP_FINITE_SET_EXT => OpFiniteSetExt
    case Z3_decl_kind.Z3_OP_FINITE_SET_MAP_INVERSE => OpFiniteSetMapInverse
    case Z3_decl_kind.Z3_OP_RECURSIVE => OpRecursive
    case Z3_decl_kind.Z3_OP_FF_NUM => OpFFNum
    case Z3_decl_kind.Z3_OP_FF_ADD => OpFFAdd
    case Z3_decl_kind.Z3_OP_FF_MUL => OpFFMul
    case Z3_decl_kind.Z3_OP_FF_NEG => OpFFNeg
    case Z3_decl_kind.Z3_OP_FF_BITSUM => OpFFBitSum

    case Z3_decl_kind.Z3_OP_INTERNAL => OpInternal
    case Z3_decl_kind.Z3_OP_UNINTERPRETED => OpUninterpreted
    case null => error("Unhandled int code for Z3KindDecl: null")
  }
}
