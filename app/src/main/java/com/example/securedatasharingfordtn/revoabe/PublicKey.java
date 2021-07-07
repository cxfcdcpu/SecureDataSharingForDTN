package com.example.securedatasharingfordtn.revoabe;

import it.unisa.dia.gas.jpbc.Element;
import com.example.securedatasharingfordtn.tree_type.MembershipTree;

public class PublicKey{
	MembershipTree membership_tree;
	Element g1;
	Element g2;
	Element g2_beta;
	Element e_gg_alpha;
	Element g1_a;
	
	public PublicKey(MembershipTree mt, Element gg1, Element gg2,
			Element gg2_beta, Element eg_alpha, Element gg1_a) {
		membership_tree = mt;
		g1 = gg1;
		g2 = gg2;
		g2_beta = gg2_beta;
		e_gg_alpha = eg_alpha;
		g1_a = gg1_a;
	}

	public void printPublicKey() {
		System.out.println("MembershipTree: "+membership_tree.toString());
		System.out.println("g1: "+g1.toString());
		System.out.println("g2: "+g2.toString());
		System.out.println("g2_beta: "+g2_beta.toString());
		
		System.out.println("e_gg_alpha: "+e_gg_alpha.toString());
		System.out.println("g1_a: "+g1_a.toString());
	}
	
	public byte[] getG1() {
		return g1.toBytes();
	}
	
	public byte[] getG2() {
		return g2.toBytes();
	}
	public byte[] getG1a() {
		return g1_a.toBytes();
	}
	public byte[] getG2_beta() {
		return g2_beta.toBytes();
	}
	public byte[] getE_gg_alpha() {
		return e_gg_alpha.toBytes();
	}

	
}
