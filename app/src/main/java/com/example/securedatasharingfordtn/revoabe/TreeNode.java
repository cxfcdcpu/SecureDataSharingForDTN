package com.example.securedatasharingfordtn.revoabe;
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
		this.y_i = yi;
		this.g_y_i = gyi;
		this.left = null;
		this.right = null;
		this.parent = par;
		this.color = GREEN;	
		this.user_id = -1;
	}
	
	public void printNode() {
		String parentID = this.parent==null?"None":""+this.parent.user_id;
		System.out.println("UserID: "+this.user_id+" y_i: "+this.y_i +" color: "+ this.color+ " parID: "+parentID+ " element: "+this.g_y_i);
	}
	
	
}
