package com.example.securedatasharingfordtn.policy_msp;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import it.unisa.dia.gas.jpbc.Pairing;
import com.example.securedatasharingfordtn.policy_msp.BinNode.OpType;
import com.example.securedatasharingfordtn.policy_msp.BinNode;
import java.util.*;
public class MSP_Builder {

	int len_longest_row;
	Pairing group;
	
	public MSP_Builder() {
		this.len_longest_row = 1;
	}
	
	public BinNode createPolicy(String policyString) {
		CharStream cs = CharStreams.fromString(policyString);
		
		PolicyLexer lexer = new PolicyLexer(cs);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		PolicyParser parser = new PolicyParser(tokens);
		ParseTree tree = parser.policy();
		
		MSP_Listener listener = new MSP_Listener();
		ParseTreeWalker walker = new ParseTreeWalker();
		walker.walk(listener, tree);
		listener.findDuplications(listener.parseTreeRoot);
		listener.updateLabelDict();
		listener.labelDuplicates(listener.parseTreeRoot);
		return listener.parseTreeRoot;
	}
	
	public Hashtable<String,List<Integer>> convert_policy_to_msp(BinNode tree){
		List<Integer> root_vector= new ArrayList<Integer>();
		root_vector.add(1);
		this.len_longest_row = 1;
		return _convert_policy_to_msp(tree, root_vector);
	}
	
	public Hashtable<String,List<Integer>> _convert_policy_to_msp(BinNode subtree, List<Integer> curr_vector){
		
		if (subtree == null) {
			return null;
		}
		
		OpType type = subtree.getNodeType();
		//System.out.println(type);
		if (type == OpType.ATTR) {
			Hashtable<String,List<Integer>> ret = new Hashtable<String,List<Integer>>();
			ret.put(subtree.getAttributeAndIndex(), curr_vector);
			return ret;
		}
		
		if (type == OpType.OR) {
			Hashtable<String,List<Integer>> left_list = this._convert_policy_to_msp(subtree.getLeft(), curr_vector);
			Hashtable<String,List<Integer>> right_list = this._convert_policy_to_msp(subtree.getRight(), curr_vector);
			left_list.putAll(right_list);
			return left_list;
		}
		
		if (type == OpType.AND) {
			int length = curr_vector.size();
			List<Integer> left_vector = createLeftVector(curr_vector,length);
			List<Integer> right_vector = createRightVector(curr_vector,length);
			this.len_longest_row+=1;
			Hashtable<String,List<Integer>> left_list = this._convert_policy_to_msp(subtree.getLeft(), left_vector);
			Hashtable<String,List<Integer>> right_list = this._convert_policy_to_msp(subtree.getRight(), right_vector);
			left_list.putAll(right_list);
			return left_list;
		}
		
		return null;
	}
	
	public List<Integer> createLeftVector(List<Integer> curr_vector,int length){
		List<Integer> ret = new ArrayList<Integer>();
		ret.addAll(curr_vector);
		for (int i = 0; i<this.len_longest_row-length;i++) {
			ret.add(0);
		}
		ret.add(1);
		return ret;		
	}
	
	public List<Integer> createRightVector(List<Integer> curr_vector,int length){
		List<Integer> ret = new ArrayList<Integer>();
		for (int i = 0; i<this.len_longest_row; i++) {
			ret.add(0);
		}
		ret.add(-1);
		return ret;		
	}
	
	public static String strip_index(String attr) {
		if(attr.lastIndexOf("_")!=-1) {
			return attr.substring(0, attr.lastIndexOf("_"));
		}
		return attr;
	}
	
	
	public int getLongestRow() {
		return this.len_longest_row;
	}
	
	private static class RA{
		boolean result;
		List<BinNode> resultList;
		public RA(boolean r, List<BinNode> rl) {
			result = r;
			resultList = rl;
		}
		
		public RA() {
			result = false;
			resultList = null;
		}
	}
	
	public static List<BinNode> prune(BinNode tree, List<String> attributes){
		
		if (tree == null) return null;
		
		RA ra = requiredAttributes( tree, attributes);
		if(ra == null)return null;
		if(ra.result)return ra.resultList;
		return null;
	}
	
	public static RA requiredAttributes(BinNode tree, List<String> attrList){
		
		if(tree == null)return null;
		BinNode left = tree.getLeft();
		BinNode right = tree.getRight();
		RA leftRa=null;
		RA rightRa=null;
		if(left!=null) {
			leftRa = requiredAttributes(left,attrList);	
		}
		if(right!=null) {
			rightRa = requiredAttributes(right,attrList);	
		}
		
		if(tree.getNodeType() == OpType.OR) {
			List<BinNode> sendThis;
			boolean result = false;
			if(leftRa!=null && leftRa.result) {
				sendThis = leftRa.resultList;
				result = true;
			}
			else if(rightRa!=null && rightRa.result) {
				sendThis = rightRa.resultList;
				result = true;
			}
			else sendThis = null;
			RA resultRA = new RA(result,sendThis);
			return resultRA;
		}
		
		if(tree.getNodeType() == OpType.AND) {
			List<BinNode> sendThis;
			boolean result = false;
			if(leftRa!=null && leftRa.result && rightRa!=null && rightRa.result) {
				sendThis = leftRa.resultList;
				sendThis.addAll(rightRa.resultList);
				result = true;
			}
			else if(leftRa!=null && leftRa.result) {
				sendThis = leftRa.resultList;
				result = true;
				if(rightRa!=null) {
					result = false;
				}
			}
			else if(rightRa!=null && rightRa.result) {
				sendThis = rightRa.resultList;
				result = true;
				if(leftRa!=null) {
					result = false;
				}
			}
			else {
				sendThis = null;
				result = false;
			}
			
			
			RA resultRA = new RA(result,sendThis);
			return resultRA;
		}
		
		if(tree.getNodeType() == OpType.ATTR) {
			if(attrList.contains(tree.getAttribute())) {
				List<BinNode> rl = new ArrayList<BinNode>();
				rl.add(tree);
				return new RA(true,rl);
			}
			else	
				return new RA();
		}
		
		
		
		return null;
	}
	
	
}
