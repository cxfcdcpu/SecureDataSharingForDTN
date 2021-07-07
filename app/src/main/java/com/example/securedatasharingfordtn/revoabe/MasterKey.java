package com.example.securedatasharingfordtn.revoabe;

import it.unisa.dia.gas.jpbc.Element;

public class MasterKey{
	Element g1_alpha;
	Element beta;
	
	public MasterKey(Element gg1_alpha, Element b) {
		g1_alpha = gg1_alpha;
		beta = b;
	}
	
	public void printMasterKey() {
		System.out.println("g1_alpha: "+g1_alpha.toString());
		System.out.println("beta: "+beta.toString());
	}
	
	public byte[] getG1_alpha() {
		return g1_alpha.toBytes();
	}
	
	public byte[] getBeta() {
		return beta.toBytes();
	}
	
}
