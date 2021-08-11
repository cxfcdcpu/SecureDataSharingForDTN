package com.example.securedatasharingfordtn.revoabe;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;
import com.example.securedatasharingfordtn.database.EntityHelper;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import com.example.securedatasharingfordtn.policy_msp.BinNode;
import com.example.securedatasharingfordtn.policy_msp.MSP_Builder;

public class Ciphertext {

	//TODO: need to change to String rather than obj as cipher text are send as bytearray
	protected BinNode policy;
	protected Element C;
	protected Element C_prime;
	protected Element D;
	protected HashMap<Integer, Element> C_y;
	protected HashMap<String, Element> C_i;
	protected byte[] ciphertext;

	protected String policyStr;
	
	public Ciphertext(BinNode policy, Element C, Element C_prime,Element D, 
			HashMap<Integer, Element> C_y, HashMap<String, Element> C_i, byte[] aes_ci, String ps) {
		this.policy = policy;
		this.C = C.getImmutable();
		this.C_prime = C_prime.getImmutable();
		this.D = D.getImmutable();
		this.C_y = C_y;
		this.C_i = C_i;
		this.ciphertext = aes_ci;
		this.policyStr = ps;
	}
	

	
	
	public Ciphertext(byte[] ctBytes, Pairing pair) {
		
		ByteBuffer bf = ByteBuffer.wrap(ctBytes, 0, 4).order(ByteOrder.nativeOrder());
		int start_policyStr = 4;
		int end_policyStr = start_policyStr + bf.getInt();
		
		bf = ByteBuffer.wrap(ctBytes, end_policyStr, 4).order(ByteOrder.nativeOrder());
		int start_C = end_policyStr+4;
		int end_C = start_C + bf.getInt();
		bf = ByteBuffer.wrap(ctBytes,end_C,4).order(ByteOrder.nativeOrder());
		int start_cp = end_C+4;
		int end_cp = start_cp + bf.getInt();
		
		bf = ByteBuffer.wrap(ctBytes, end_cp, 4).order(ByteOrder.nativeOrder());
		int start_d= end_cp + 4;
		int end_d = start_d + bf.getInt();
		bf = ByteBuffer.wrap(ctBytes, end_d, 4).order(ByteOrder.nativeOrder());
		int start = end_d + 4;
		int totalCy =  bf.getInt();
		
		this.policyStr = new String(Arrays.copyOfRange(ctBytes, start_policyStr, end_policyStr));
		MSP_Builder util = new MSP_Builder();
		this.policy = util.createPolicy(this.policyStr);
		this.C = pair.getGT().newElementFromBytes(Arrays.copyOfRange(ctBytes, start_C, end_C)).getImmutable();
		this.C_prime = pair.getG2().newElementFromBytes(Arrays.copyOfRange(ctBytes, start_cp, end_cp)).getImmutable();
		this.D = pair.getG2().newElementFromBytes(Arrays.copyOfRange(ctBytes, start_d, end_d)).getImmutable();

		this.C_y = new HashMap<Integer,Element>();
		for(int i=0; i<totalCy; i++) {
			bf = ByteBuffer.wrap(ctBytes, start, 4).order(ByteOrder.nativeOrder());
			start+=4;
			int size = bf.getInt();

			int key = Integer.parseInt(new String(Arrays.copyOfRange(ctBytes, start, start+size)));
			start+=size;
			bf = ByteBuffer.wrap(ctBytes, start, 4).order(ByteOrder.nativeOrder());
			start+=4;
			size = bf.getInt();
			Element value = pair.getG1().newElementFromBytes(Arrays.copyOfRange(ctBytes, start, start+size)).getImmutable();
			start+=size;
			C_y.put(key, value);
		}
		
		bf = ByteBuffer.wrap(ctBytes, start, 4).order(ByteOrder.nativeOrder());
		start+=4;
		int totalCi =  bf.getInt();
		this.C_i = new HashMap<String,Element>();
		for(int i=0; i<totalCi; i++) {
			bf = ByteBuffer.wrap(ctBytes, start, 4).order(ByteOrder.nativeOrder());
			start+=4;
			int size = bf.getInt();
			String key = new String(Arrays.copyOfRange(ctBytes, start, start+size));
			start+=size;
			bf = ByteBuffer.wrap(ctBytes, start, 4).order(ByteOrder.nativeOrder());
			start+=4;
			size = bf.getInt();
			Element value = pair.getG1().newElementFromBytes(Arrays.copyOfRange(ctBytes, start, start+size)).getImmutable();
			start+=size;
			C_i.put(key, value);
		}
		
		bf = ByteBuffer.wrap(ctBytes, start, 4).order(ByteOrder.nativeOrder());
		start+=4;
		int size = bf.getInt();
		this.ciphertext = Arrays.copyOfRange(ctBytes, start, start+size);
		
	}
	
	
	public byte[] toByteArray() {
		ByteArrayOutputStream bs = new ByteArrayOutputStream();
		byte[] ps = policyStr.getBytes();
		byte[] c = C.toBytes();
		byte[] c_prime = C_prime.toBytes();
		byte[] d = D.toBytes();
		
		try {
			bs.write(EntityHelper.int_to_bytes(ps.length));
			bs.write(ps);
			bs.write(EntityHelper.int_to_bytes(c.length));
			bs.write(c);
			bs.write(EntityHelper.int_to_bytes(c_prime.length));
			bs.write(c_prime);
			bs.write(EntityHelper.int_to_bytes(d.length));
			bs.write(d);
		 
			bs.write(EntityHelper.int_to_bytes(C_y.size()));
			for(Entry<Integer, Element> cy : C_y.entrySet()) {
				byte[] keyStr = (""+cy.getKey()).getBytes();
				bs.write(EntityHelper.int_to_bytes(keyStr.length));
				bs.write(keyStr);
				byte[] yByte = cy.getValue().toBytes();
				bs.write(EntityHelper.int_to_bytes(yByte.length));
				bs.write(yByte);
			}
			
			
			bs.write(EntityHelper.int_to_bytes(C_i.size()));
			for(Entry<String, Element> ci : C_i.entrySet()) {
				byte[] keyStr = ci.getKey().getBytes();
				bs.write(EntityHelper.int_to_bytes(keyStr.length));
				bs.write(keyStr);
				byte[] iByte = ci.getValue().toBytes();
				bs.write(EntityHelper.int_to_bytes(iByte.length));
				bs.write(iByte);
			}
			
			bs.write(EntityHelper.int_to_bytes(this.ciphertext.length));
			bs.write(this.ciphertext);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bs.toByteArray();
	}
	
	
	public void printCipherText() {
		policy.printTree();
		System.out.println(C);
		System.out.println(C_prime);
		System.out.println(D);
		for(Entry<Integer,Element> cy: C_y.entrySet()) {
			System.out.println(cy.getKey()+": "+cy.getValue());
		}
		for(Entry<String,Element> ci: C_i.entrySet()) {
			System.out.println(ci.getKey()+": "+ci.getValue());
		}
	}
	
	public boolean compareCiphertext(Ciphertext ct) {
		if(this.ciphertext.length!=ct.ciphertext.length) {
			return false;
		}
		for(int i =0; i<this.ciphertext.length; i++) {
			if(this.ciphertext[i]!=ct.ciphertext[i]) {
				return false;
			}
		}
		return true;
	}
	
}
