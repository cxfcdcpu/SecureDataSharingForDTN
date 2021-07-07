package com.example.securedatasharingfordtn.tree_type;
import it.unisa.dia.gas.jpbc.*;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
//import java.lang.Math.*;
public class TreeNode{

	public int y_i;
	public Element g_y_i;
	TreeNode left;
	TreeNode right;
	TreeNode parent;
	int user_id;
	Boolean GREEN = true;
	Boolean RED = false;
	//True is green, False is red.
	Boolean color;
	
	public TreeNode(int yi, Element gyi, TreeNode par) {
		y_i = yi;
		g_y_i = gyi;
		left = null;
		right = null;
		parent = par;
		color = GREEN;	
		user_id = -1;
	}
	
	public void printNode() {
		System.out.println("UserID: "+user_id+" y_i: "+y_i);
	}
	
	
}
