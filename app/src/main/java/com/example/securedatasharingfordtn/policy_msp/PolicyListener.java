package com.example.securedatasharingfordtn.policy_msp;

// Generated from Policy.g4 by ANTLR 4.9.2
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link PolicyParser}.
 */
public interface PolicyListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link PolicyParser#policy}.
	 * @param ctx the parse tree
	 */
	void enterPolicy(PolicyParser.PolicyContext ctx);
	/**
	 * Exit a parse tree produced by {@link PolicyParser#policy}.
	 * @param ctx the parse tree
	 */
	void exitPolicy(PolicyParser.PolicyContext ctx);
	/**
	 * Enter a parse tree produced by {@link PolicyParser#atom}.
	 * @param ctx the parse tree
	 */
	void enterAtom(PolicyParser.AtomContext ctx);
	/**
	 * Exit a parse tree produced by {@link PolicyParser#atom}.
	 * @param ctx the parse tree
	 */
	void exitAtom(PolicyParser.AtomContext ctx);
	/**
	 * Enter a parse tree produced by {@link PolicyParser#term}.
	 * @param ctx the parse tree
	 */
	void enterTerm(PolicyParser.TermContext ctx);
	/**
	 * Exit a parse tree produced by {@link PolicyParser#term}.
	 * @param ctx the parse tree
	 */
	void exitTerm(PolicyParser.TermContext ctx);
	/**
	 * Enter a parse tree produced by {@link PolicyParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterExpr(PolicyParser.ExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link PolicyParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitExpr(PolicyParser.ExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link PolicyParser#leafNode}.
	 * @param ctx the parse tree
	 */
	void enterLeafNode(PolicyParser.LeafNodeContext ctx);
	/**
	 * Exit a parse tree produced by {@link PolicyParser#leafNode}.
	 * @param ctx the parse tree
	 */
	void exitLeafNode(PolicyParser.LeafNodeContext ctx);
	/**
	 * Enter a parse tree produced by {@link PolicyParser#leafCondition}.
	 * @param ctx the parse tree
	 */
	void enterLeafCondition(PolicyParser.LeafConditionContext ctx);
	/**
	 * Exit a parse tree produced by {@link PolicyParser#leafCondition}.
	 * @param ctx the parse tree
	 */
	void exitLeafCondition(PolicyParser.LeafConditionContext ctx);
	/**
	 * Enter a parse tree produced by {@link PolicyParser#node}.
	 * @param ctx the parse tree
	 */
	void enterNode(PolicyParser.NodeContext ctx);
	/**
	 * Exit a parse tree produced by {@link PolicyParser#node}.
	 * @param ctx the parse tree
	 */
	void exitNode(PolicyParser.NodeContext ctx);
	/**
	 * Enter a parse tree produced by {@link PolicyParser#operator}.
	 * @param ctx the parse tree
	 */
	void enterOperator(PolicyParser.OperatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link PolicyParser#operator}.
	 * @param ctx the parse tree
	 */
	void exitOperator(PolicyParser.OperatorContext ctx);
}