package com.example.securedatasharingfordtn.revoabe;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.example.securedatasharingfordtn.database.EntityHelper;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.util.Arrays;

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
	
	//	The following is how the bytes generated
	//	os.write(EntityHelper.int_to_bytes(L.length));
	//	os.write(L);
	//	os.write(EntityHelper.stringList_to_bytes(attributes));
	//	os.write(EntityHelper.stringList_to_bytes(attrSizes));
	//	os.write(EntityHelper.int_to_bytes(k_is.length));
	//	os.write(k_is);
	//	os.write(EntityHelper.stringList_to_bytes(revoNodes));
	//	os.write(EntityHelper.stringList_to_bytes(revoNodeSizes));
	//	os.write(EntityHelper.int_to_bytes(k_ys.length));
	//	os.write(k_ys);
	//	The following constructor construct the privatekey from bytes
	public PrivateKey(byte[] prikBytes, Pairing pair) {
		ByteBuffer bf = ByteBuffer.wrap(prikBytes, 0, 4).order(ByteOrder.nativeOrder());
		int start_l = 4;
		int end_l = start_l + bf.getInt();
		bf = ByteBuffer.wrap(prikBytes, end_l, 4).order(ByteOrder.nativeOrder());
		int start_attr = end_l + 4;
		int end_attr = start_attr +bf.getInt();
		bf = ByteBuffer.wrap(prikBytes, end_attr, 4).order(ByteOrder.nativeOrder());
		int start_attrSize = end_attr + 4;
		int end_attrSize = start_attrSize +bf.getInt();
		bf = ByteBuffer.wrap(prikBytes, end_attrSize, 4).order(ByteOrder.nativeOrder());
		int start_kis = end_attrSize+4;
		int end_kis = start_kis + bf.getInt();
		this.L = pair.getG2().newElementFromBytes(Arrays.copyOfRange(prikBytes, start_l, end_l)).getImmutable();
		this.attr_list = EntityHelper.bytes_to_stringList(Arrays.copyOfRange(prikBytes, start_attr, end_attr));
		List<String> attrSize = EntityHelper.bytes_to_stringList(Arrays.copyOfRange(prikBytes, start_attrSize, end_attrSize));
		this.k_i = bytesToKis(this.attr_list,attrSize,Arrays.copyOfRange(prikBytes, start_kis, end_kis),pair);
		
		bf = ByteBuffer.wrap(prikBytes, end_kis, 4).order(ByteOrder.nativeOrder());
		int start_nodes = end_kis + 4;
		int end_nodes = start_nodes +bf.getInt();
		bf = ByteBuffer.wrap(prikBytes, end_nodes, 4).order(ByteOrder.nativeOrder());
		int start_nodeSize = end_nodes + 4;
		int end_nodeSize = start_nodeSize +bf.getInt();
		bf = ByteBuffer.wrap(prikBytes, end_nodeSize, 4).order(ByteOrder.nativeOrder());
		int start_kys = end_nodeSize+4;
		int end_kys = start_kys + bf.getInt();
		List<String> node_list = EntityHelper.bytes_to_stringList(Arrays.copyOfRange(prikBytes, start_nodes, end_nodes));
		List<String> nodeSize = EntityHelper.bytes_to_stringList(Arrays.copyOfRange(prikBytes, start_nodeSize, end_nodeSize));
		this.k_y = bytesToKys(node_list,nodeSize,Arrays.copyOfRange(prikBytes, start_kys, end_kys),pair);
	}
	
	public static HashMap<String,Element> bytesToKis(List<String> attrs, 
			List<String> attrSizes, byte[] kisBytes, Pairing pair){
		HashMap<String, Element> ret = new HashMap<String,Element>();
		int startPosition = 0;
		int counter = 0;
		for(String attr: attrs) {
			int curSize = Integer.parseInt(attrSizes.get(counter));
			counter++;
			Element curElement = pair.getG1().newElementFromBytes(
					Arrays.copyOfRange(kisBytes, startPosition, startPosition+curSize)).getImmutable();
			ret.put(attr, curElement);
			startPosition+=curSize;
		}
		return ret;
	}
	
	public static HashMap<Integer,Element> bytesToKys(List<String> nodes, 
			List<String> nodeSizes, byte[] kysBytes, Pairing pair){
		HashMap<Integer, Element> ret = new HashMap<Integer,Element>();
		int startPosition = 0;
		int counter = 0;
		for(String node: nodes) {
			int nodeID = Integer.parseInt(node);
			int curSize = Integer.parseInt(nodeSizes.get(counter));
			counter++;
			Element curElement = pair.getG1().newElementFromBytes(
					Arrays.copyOfRange(kysBytes, startPosition, startPosition+curSize)).getImmutable();
			ret.put(nodeID, curElement);
			startPosition+=curSize;
		}
		return ret;
	}	
	
	
	
	
	public void printPrivateKey() {
		System.out.println("attribute list: "+attr_list.toString());
		System.out.println("L: "+L.toString());
		System.out.println("K_i: "+k_i.toString());
		System.out.println("K_y: "+k_y.toString());
	}

	public String getString() {
		String ret = "";
		ret+= "attribute list: "+attr_list.toString()+"\n";
		ret+= "L: "+L.toString()+"\n";
		ret+= "K_i: "+k_i.toString()+"\n";
		ret+= "K_y: "+k_y.toString()+"\n";
		return ret;
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
