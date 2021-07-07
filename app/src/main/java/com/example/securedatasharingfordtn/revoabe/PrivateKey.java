package com.example.securedatasharingfordtn.revoabe;

import java.util.HashMap;
import java.util.List;

import it.unisa.dia.gas.jpbc.Element;

public class PrivateKey {
	List<String> attr_list; //List of attributes
	Element L; //An element of the pairing group
	HashMap<String,Element> k_i; //Map of valid attributes and element
	HashMap<Integer,Element> k_y;//Map of valid memberID and element
	
	public PrivateKey(List<String> al, HashMap<String,Element> ki, 
			Element l, HashMap<Integer,Element> ky) {
		attr_list = al;
		k_i = ki;
		L = l;
		k_y = ky;
	}
	
	public void printPrivateKey() {
		System.out.println("attribute list: "+attr_list.toString());
		System.out.println("L: "+L.toString());
		System.out.println("K_i: "+k_i.toString());
		System.out.println("K_y: "+k_y.toString());
	}
	
}
