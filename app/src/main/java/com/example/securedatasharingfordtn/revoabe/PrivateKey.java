package com.example.securedatasharingfordtn.revoabe;

import java.nio.ByteBuffer;
import java.util.ArrayList;
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
	
	public List<String> getAttributes() {
		return this.attr_list;
	}
	
	public byte[] getL() {
		return this.L.toBytes();
		
	}
	
	public HashMap<String, Element> getKI(){
		return this.k_i;
	}
	
	public HashMap<Integer,Element> getKY(){
		return this.k_y;
	}
	
	public List<String> getAttrSizes(){
		List<String> ret = new ArrayList<String>();
		for(String attr: attr_list) {
			ret.add(k_i.get(attr).toBytes().length+"");
		}
		return ret;
	}
	public int getKISize() {
		int ret = 0;
		for(String sizeStr : this.getAttrSizes()) {
			int size = Integer.parseInt(sizeStr);
			ret+=size;
		}
		return ret;
	}
	
	public byte[] getKIs() {
		ByteBuffer bf = ByteBuffer.allocate(this.getKISize());
		for(String attr: attr_list) {
			bf.put(k_i.get(attr).toBytes());
		}
		return bf.array();
		
	}
	
	public List<String> getReVoNodes(){
		List<String> ret = new ArrayList<String>();
		for(int node : this.k_y.keySet()) {
			ret.add(""+node);
		}
		return ret;
	}
	
	public List<String> getReVoNodeSizes(){
		List<String> ret = new ArrayList<String>();
		for(String node : this.getReVoNodes()) {
			ret.add(this.k_y.get(Integer.parseInt(node)).toBytes().length+"");
		}
		return ret;
	}
	
	public int getKYSize() {
		int ret = 0;
		for(String sizeStr : this.getReVoNodeSizes()) {
			int size = Integer.parseInt(sizeStr);
			ret+=size;
		}
		return ret;
	}
	
	public byte[] getKYs() {
		ByteBuffer bf = ByteBuffer.allocate(this.getKYSize());
		for(String node: this.getReVoNodes()) {
			bf.put(k_y.get(Integer.parseInt(node)).toBytes());
		}
		return bf.array();
	}
}
