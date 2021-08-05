package com.example.securedatasharingfordtn.policy_msp;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import com.example.securedatasharingfordtn.policy_msp.BinNode.*;
import com.example.securedatasharingfordtn.policy_msp.PolicyParser.*;

public class MSP_Listener implements PolicyListener {

	BinNode parseTreeRoot;
	Hashtable<String, Integer> dupNodes;
	Hashtable<String, Integer> labelDict;
	@Override
	public void enterEveryRule(ParserRuleContext arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void exitEveryRule(ParserRuleContext arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitErrorNode(ErrorNode arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitTerminal(TerminalNode arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void enterPolicy(PolicyContext ctx) {
		// TODO Auto-generated method stub
		System.out.println("currentln parsing: " + ctx.getText());
		this.dupNodes = new Hashtable<String, Integer>();
		this.labelDict = new Hashtable<String, Integer>();
		this.parseTreeRoot = parseCtx(ctx.getChild(0));
		
		//this.parseTreeRoot.printTree();

	}
	
	
	
	
	public BinNode parseCtx(ParseTree parseTree) {
		int numberOfChildren = parseTree.getChildCount();
		
		//if(numberOfChildren == 0) return null;
		int currentRuleIndex =-1;
		try {
		    currentRuleIndex = ((RuleContext) parseTree).getRuleIndex();
		}catch(Exception e) {
			return null;
		}
		//String currentRule = PolicyParser.ruleNames[currentRuleIndex];
		//System.out.println(""+currentRuleIndex+currentRule+numberOfChildren);
		if(currentRuleIndex == 4 || currentRuleIndex ==5) {
			String currentAttr = parseTree.getText();
			BinNode currentNode = new BinNode(OpType.ATTR,currentAttr);
			
			return currentNode;
		}
		
		
		if(currentRuleIndex == 7) {
			OpType currentType;
			if(parseTree.getText().toUpperCase().equals("AND")) {
				currentType = OpType.AND;
			}
			else {
				currentType = OpType.OR;
			}
			return new BinNode(currentType,"");
		}
		if(numberOfChildren == 3) {
			if (((RuleContext) parseTree.getChild(1)).getRuleIndex() == 7)
				return new BinNode(parseCtx(parseTree.getChild(1)), parseCtx(parseTree.getChild(0)),parseCtx(parseTree.getChild(2)));
			else
				return parseCtx(parseTree.getChild(1));
		}
		
		return parseCtx(parseTree.getChild(0));
	}
	
	public void findDuplications(BinNode tree) {
		if (tree == null)return;
		if (tree.left != null)this.findDuplications(tree.left);
		if (tree.right != null)this.findDuplications(tree.right);
		if (tree.getNodeType()==OpType.ATTR) {
			String key = tree.getAttribute();
			if(this.dupNodes.containsKey(key)) {
				this.dupNodes.put(key, this.dupNodes.get(key)+1);
			}
			else {
				this.dupNodes.put(key, 1);
			}
			
		}
	}
	
	public void updateLabelDict() {
		Iterator dupSet = this.dupNodes.entrySet().iterator();
		while(dupSet.hasNext()) {
			Entry<String, Integer> curSet = (Entry<String, Integer>) dupSet.next();
			if (curSet.getValue()>1) {
				this.labelDict.put(curSet.getKey(), 0);
			}
		}
		this.dupNodes.clear();
	}
	
	public void labelDuplicates(BinNode tree) {
		if (tree == null)return;
		if (tree.left != null)this.labelDuplicates(tree.left);
		if (tree.right != null)this.labelDuplicates(tree.right);
		if (tree.getNodeType()==OpType.ATTR) {
			String key = tree.getAttribute();
			if(this.labelDict.containsKey(key)) {
				tree.index=this.labelDict.get(key);
				this.labelDict.put(key, this.labelDict.get(key)+1);
			}			
		}
	}
	
	
	

	@Override
	public void exitPolicy(PolicyContext ctx) {
		// TODO Auto-generated method stub

	}

	@Override
	public void enterAtom(AtomContext ctx) {
		// TODO Auto-generated method stub

	}

	@Override
	public void exitAtom(AtomContext ctx) {
		// TODO Auto-generated method stub

	}

	@Override
	public void enterTerm(TermContext ctx) {
		// TODO Auto-generated method stub
		//System.out.println(ctx.getChildCount());
//		if(ctx.getChildCount()>1) {
//			System.out.println(ctx.getChild(1).getText());
//		}
		

	}

	@Override
	public void exitTerm(TermContext ctx) {
		// TODO Auto-generated method stub

	}

	@Override
	public void enterExpr(ExprContext ctx) {
		// TODO Auto-generated method stub

	}

	@Override
	public void exitExpr(ExprContext ctx) {
		// TODO Auto-generated method stub

	}

	@Override
	public void enterLeafNode(LeafNodeContext ctx) {
		// TODO Auto-generated method stub
		//System.out.println(ctx.getText());
	}

	@Override
	public void exitLeafNode(LeafNodeContext ctx) {
		// TODO Auto-generated method stub

	}

	@Override
	public void enterLeafCondition(LeafConditionContext ctx) {
		// TODO Auto-generated method stub
		//System.out.println(ctx.getChildCount());
	}

	@Override
	public void exitLeafCondition(LeafConditionContext ctx) {
		// TODO Auto-generated method stub

	}

	@Override
	public void enterNode(NodeContext ctx) {
		// TODO Auto-generated method stub
		//System.out.println(ctx.getChild(0).getText());
	}

	@Override
	public void exitNode(NodeContext ctx) {
		// TODO Auto-generated method stub

	}

	@Override
	public void enterOperator(OperatorContext ctx) {
		// TODO Auto-generated method stub
		//System.out.println(ctx.getText());
		///System.out.println(ctx.getText());
	}

	@Override
	public void exitOperator(OperatorContext ctx) {
		// TODO Auto-generated method stub

	}

}
