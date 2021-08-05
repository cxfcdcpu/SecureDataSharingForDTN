package com.example.securedatasharingfordtn.policy_msp;



public class BinNode {
	
	public enum OpType {
		OR,
		AND,
		ATTR,
		THRESHOLD,
		CONDITIONAL,
		NONE
	}
	
	boolean negated;
	int index;
	OpType type;
	String attribute;
	BinNode left;
	BinNode right;
	
	public BinNode(OpType value) {
		this.init();
		this.assignType(value);
	}
	
	public BinNode(OpType value, String attr) {
		this.init();
		this.assignType(value);
		this.assignAttr(attr);
	}
	
	public BinNode(BinNode n1, BinNode n2, BinNode n3) {
		this.init();
		this.assignType(n1.type);
		this.left = n2;
		this.right = n3;
		
	}
	
	public BinNode(OpType value, String attr, BinNode left, BinNode right) {
		this.init();
		this.assignType(value);
		this.assignAttr(attr);
		this.addSubNode(left, right);
		
	}
	
	public BinNode(OpType value, String attr, BinNode left) {
		this.init();
		this.assignType(value);
		this.assignAttr(attr);
		this.addSubNode(left, null);
		
	}
	
	public void init() {
		this.negated = false;
		this.index = -1;
		this.type = null;
		this.attribute = "";
		this.left = null;
		this.right = null;
				
	}
	
	public void assignType(OpType value) {
		if (value.compareTo(OpType.OR) >= 0 &&value.compareTo(OpType.NONE)<0) {
			this.type = value;
		}else {
			this.type = OpType.NONE;
		}
	}
	
	public void assignAttr(String attr) {
		if(this.type.equals(OpType.ATTR)) {
			String value = attr;
			if(attr.charAt(0)=='!') {
				value = value.substring(1);
				this.negated = true;
			}
			if(value.contains("_")) {
				this.index = Integer.parseInt(value.split("_")[1]);
				value = value.split("_")[0];
			}
			this.attribute = value.toUpperCase();
		}
	}
	
	public boolean equal(BinNode other) {
		if (other == null)return false;
		
		if(this.type != other.type) {
			return false;
		}
		if(this.negated != other.negated) {
			return false;
		}
		if(this.index != other.index) {
			return false;
		}
		return this.attribute.equals(other.attribute);
		
	}
	
	public String getAttribute() {
		if (this.type == OpType.ATTR) {
			String prefix = "";
			if(this.negated) {
				prefix = "!";
			}

			return prefix+this.attribute;
			
		}
		return "";
	}
	
	public String getAttributeAndIndex() {
		if (this.type == OpType.ATTR) {
			String prefix = "";
			String postfix = "";
			if(this.negated) {
				prefix = "!";
			}
			if(this.index!=-1) {
				postfix = "_"+this.index;
			}
			return prefix+this.attribute+postfix;
			
		}
		return "";
	}
	
	public BinNode getLeft() {
		return this.left;
	}
	
	public BinNode getRight() {
		return this.right;
	}
	
	public OpType getNodeType() {
		return this.type;
	}
	
	public void addSubNode(BinNode left, BinNode right) {
		this.left = left!=null?left:null;
		this.right = right!=null?right:null;
	}
	

	
	public void printTree() {
		System.out.println(this.type +":"+ this.attribute);
		if (this.left == null) {
			System.out.println("None");
		}
		else {
			this.left.printTree();
		}
		if (this.right == null) {
			System.out.println("None");
		}
		else {
			this.right.printTree();
		}
	}
	
	

	
}
