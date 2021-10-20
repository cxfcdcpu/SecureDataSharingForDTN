package com.example.securedatasharingfordtn.revoabe;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.util.Arrays;

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
	
	//	The following is how to change public key of mission to bytes
	//	os.write(EntityHelper.int_to_bytes(g1.length));
	//	os.write(g1);
	//	os.write(EntityHelper.int_to_bytes(g2.length));
	//	os.write(g2);
	//	os.write(EntityHelper.int_to_bytes(g1_a.length));
	//	os.write(g1_a);
	//	os.write(EntityHelper.int_to_bytes(g2_beta.length));
	//	os.write(g2_beta);
	//	os.write(EntityHelper.int_to_bytes(e_gg_alpha.length));
	//	os.write(e_gg_alpha);
	//	The next function is to change bytes to public key.
	public PublicKey(byte[] pkInBytes, Pairing pair) {
		ByteBuffer bf = ByteBuffer.wrap(pkInBytes, 0, 4).order(ByteOrder.nativeOrder());
		int start_g1 = 4;
		int end_g1 = start_g1 + bf.getInt();
		
		bf = ByteBuffer.wrap(pkInBytes, end_g1, 4).order(ByteOrder.nativeOrder());
		int start_g2 = end_g1+4;
		int end_g2 = start_g2 + bf.getInt();
		bf = ByteBuffer.wrap(pkInBytes,end_g2,4).order(ByteOrder.nativeOrder());
		int start_g1_a = end_g2+4;
		int end_g1_a = start_g1_a + bf.getInt();
		
		bf = ByteBuffer.wrap(pkInBytes, end_g1_a, 4).order(ByteOrder.nativeOrder());
		int start_g2_beta = end_g1_a + 4;
		int end_g2_beta = start_g2_beta + bf.getInt();
		bf = ByteBuffer.wrap(pkInBytes, end_g2_beta, 4).order(ByteOrder.nativeOrder());
		int start_egg = end_g2_beta + 4;
		int end_egg = start_egg + bf.getInt();
		bf = ByteBuffer.wrap(pkInBytes, end_egg, 4).order(ByteOrder.nativeOrder());
		int capacity = bf.getInt();
		bf = ByteBuffer.wrap(pkInBytes, end_egg+4, 4).order(ByteOrder.nativeOrder());
		int seed = bf.getInt();
		this.g1 = pair.getG1().newElementFromBytes(Arrays.copyOfRange(pkInBytes, start_g1, end_g1)).getImmutable();
		this.g2 = pair.getG2().newElementFromBytes(Arrays.copyOfRange(pkInBytes, start_g2, end_g2)).getImmutable();
		this.g1_a = pair.getG1().newElementFromBytes(Arrays.copyOfRange(pkInBytes, start_g1_a, end_g1_a)).getImmutable();
		this.g2_beta = pair.getG2().newElementFromBytes(Arrays.copyOfRange(pkInBytes, start_g2_beta, end_g2_beta)).getImmutable();
		this.e_gg_alpha = pair.getGT().newElementFromBytes(Arrays.copyOfRange(pkInBytes, start_egg, end_egg)).getImmutable();
		this.membership_tree = new MembershipTree(capacity, g1, pair, seed);
	}
	
	public void printPublicKey() {
		System.out.println("MembershipTree: "+membership_tree.toString());
		membership_tree.printAllNodesBFS();
		System.out.println("g1: "+g1.toString());
		System.out.println("g2: "+g2.toString());
		System.out.println("g2_beta: "+g2_beta.toString());
		
		System.out.println("e_gg_alpha: "+e_gg_alpha.toString());
		System.out.println("g1_a: "+g1_a.toString());
	}

	public String getString(){
		String ret = "";
		ret+="MembershipTree: "+membership_tree.toString()+"\n";
		ret+="g1: "+g1.toString()+"\n";
		ret+="g2: "+g2.toString()+"\n";
		ret+="g2_beta: "+g2_beta.toString()+"\n";
		ret+="e_gg_alpha: "+e_gg_alpha.toString()+"\n";
		ret+="g1_a: "+g1_a.toString()+"\n";
		return ret;
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
	
	public MembershipTree getMembershipTree() {
		return membership_tree;
	}


	
}
