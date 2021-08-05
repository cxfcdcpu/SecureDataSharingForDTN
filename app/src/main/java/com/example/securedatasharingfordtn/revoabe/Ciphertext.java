package com.example.securedatasharingfordtn.revoabe;

import java.util.HashMap;

import it.unisa.dia.gas.jpbc.Element;
import com.example.securedatasharingfordtn.policy_msp.*;

public class Ciphertext {

	//TODO: need to change to String rather than obj as cipher text are send as bytearray
	BinNode policy;
	Element C;
	Element C_prime;
	Element D;
	HashMap<Integer, Element> C_y;
	HashMap<String, Element> C_i;
	
	
	public Ciphertext(BinNode policy, Element C, Element C_prime,Element D, 
			HashMap<Integer, Element> C_y, HashMap<String, Element> C_i) {
		this.policy = policy;
		this.C = C;
		this.C_prime = C_prime;
		this.D = D;
		this.C_y = C_y;
		this.C_i = C_i;
	}
	
	public HashMap<Integer, Element> getCy(){
		return C_y;
	}
	
}
