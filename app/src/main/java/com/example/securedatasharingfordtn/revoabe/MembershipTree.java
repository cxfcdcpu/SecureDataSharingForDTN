package com.example.securedatasharingfordtn.revoabe;
import it.unisa.dia.gas.jpbc.*;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
public class MembershipTree{

	int m; //the maximum number of users for the mission
	int user_id_counter; //help to mark user's id through iteration
	Element g1; //points of the given elliptical curve
	Pairing group; //the pairing group
	TreeNode root; //root of the tree
	HashMap<Integer,TreeNode> user_id_to_leaf; //all leafnodes and ids
	Random rd;
	
	
	public MembershipTree(int maxNodes, Element g, Pairing pairingFactory) {
		this.m = maxNodes;
		this.user_id_to_leaf = new HashMap<Integer, TreeNode>();
		this.g1 = g.getImmutable();
		this.group = pairingFactory;
		this.root = createTree();
	}
	
	public MembershipTree(int maxNodes, Element g, Pairing pairingFactory, long seed) {
		this.m = maxNodes;
		this.user_id_to_leaf = new HashMap<Integer, TreeNode>();
		this.g1 = g;
		this.group = pairingFactory;
		this.root = createTree(seed);
	}
	
	public TreeNode createTree() {
		if(this.m<1) return null;
		this.user_id_counter = 1;
		return dfs(null,1,log2(this.m));
	}
	
	public TreeNode createTree(long seed) {
		if(this.m<1) return null;
		this.user_id_counter = 1;
		this.rd = new Random(seed);
		return dfs(null,1,log2(this.m), true);
	}
	
	
	
	private TreeNode createTreeNode(TreeNode parent, int y_i) {
		Element currentRandom = this.group.getZr().newRandomElement().getImmutable();
		Element curr = this.g1.powZn(currentRandom).getImmutable();
		TreeNode node = new TreeNode(y_i,curr ,parent);
		return node;
	}
	
	private TreeNode createTreeNode(TreeNode parent, int y_i, boolean withSeed) {
		byte[] rdBytes = new byte[4];
		
		rd.nextBytes(rdBytes);
		Element currentRandom = this.group.getZr().newElementFromBytes(rdBytes).getImmutable();
		Element curr = this.g1.powZn(currentRandom).getImmutable();
		TreeNode node = new TreeNode(y_i,curr ,parent);
		return node;
	}
	
	public void testRandom(TreeNode node) {
		if(node==null)return;
		System.out.println(node.g_y_i);
		testRandom(node.left);
		testRandom(node.right);
		
	}
	
	public void testLeaf() {
		for(Entry<Integer, TreeNode> leafset: this.user_id_to_leaf.entrySet()) {
			leafset.getValue().printNode();
		}
	}
	
	
	private TreeNode dfs(TreeNode parent, int y_i, int h) {
		TreeNode node = createTreeNode(parent, y_i);
		//node.printNode();
		if (h == 0) {
			node.user_id = this.user_id_counter;
			this.user_id_to_leaf.put(this.user_id_counter, node);
			this.user_id_counter+=1;
			//return node;
		}
		else {
			node.left = dfs(node,2*y_i,h-1);
			node.right = dfs(node, 2*y_i+1,h-1);
			//return node; 
		}
		return node;
	}
	
	private TreeNode dfs(TreeNode parent, int y_i, int h, boolean withSeed) {
		TreeNode node = createTreeNode(parent, y_i, withSeed);
		//node.printNode();
		if (h == 0) {
			node.user_id = this.user_id_counter;
			this.user_id_to_leaf.put(this.user_id_counter, node);
			this.user_id_counter+=1;
			//return node;
		}
		else {
			node.left = dfs(node,2*y_i,h-1,withSeed);
			node.right = dfs(node, 2*y_i+1,h-1,withSeed);
			//return node; 
		}
		return node;
	}
	
	
	
	public List<TreeNode> getUserPath(int user_id){
		List<TreeNode> ret = new ArrayList<TreeNode>();
		if (!(user_id>=1 && user_id<= this.m)) {
			return ret;
		}
		TreeNode node= this.user_id_to_leaf.get(user_id);
		while (node!=null) {
			ret.add(node);
			node = node.parent;
		}
		return ret;
	}
	
	private void colorRED(TreeNode node) {
		while(node !=null && node.color == node.GREEN) {
			node.color = node.RED;
			node = node.parent;
		}
	}
	
	private List<TreeNode> getSubsetCoverNodesAndResetColor(TreeNode node) {
		
		LinkedList<TreeNode> bfsList = new LinkedList<TreeNode>();
		List<TreeNode> ret = new ArrayList<TreeNode>();
		if(node == null)return ret;
		bfsList.add(node);
		while(bfsList.size()!=0) {
			TreeNode curNode = bfsList.poll();
			if (curNode.color == curNode.GREEN) {
				ret.add(curNode);
			}
			else {
				curNode.color = curNode.GREEN;
				if(curNode.left != null)
					bfsList.add(curNode.left);
				if(curNode.right != null)
					bfsList.add(curNode.right);
			}
		}
		return ret;
				
	}
	
	public List<TreeNode> getSubsetCover(List<Integer> RL){
		List<TreeNode> res = new ArrayList<TreeNode>();
		
		if ( RL != null && RL.size() != 0) {
			for (int user_id : RL) {
				if(user_id>=1 && user_id <= m) {
					colorRED(this.user_id_to_leaf.get(user_id));
				}
			}
		}
		
		return getSubsetCoverNodesAndResetColor(this.root);
		
	}
	
	
	
	
	

	
	@Override
	public String toString() {
		return group.toString();
		
	}
	public TreeNode getRoot() {
		return this.root;
	}
	
	
	
	public int log2(int m) {
		return (int)Math.ceil(Math.log(m)/Math.log(2));
	}
	
	public void printAllNodesBFS() {
		LinkedList<TreeNode> treeList = new LinkedList<TreeNode>();
		treeList.add(root);
		while(treeList.size()!=0) {
			TreeNode cur = treeList.poll();
			cur.printNode();
			if(cur.left != null) {
				treeList.add(cur.left);
			}
			if(cur.right != null) {
				treeList.add(cur.right);
			}
		}
	}
	
}
