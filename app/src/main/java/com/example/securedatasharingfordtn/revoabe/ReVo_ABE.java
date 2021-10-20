package com.example.securedatasharingfordtn.revoabe;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import it.unisa.dia.gas.jpbc.*;
import com.example.securedatasharingfordtn.policy_msp.BinNode;
import com.example.securedatasharingfordtn.policy_msp.MSP_Builder;
import com.example.securedatasharingfordtn.revoabe.Ciphertext;


public class ReVo_ABE {
	int nodeCount;

	Pairing group;
	PublicKey publicKey;
	MasterKey masterKey;
	
	
	public ReVo_ABE(Pairing pair, int m) {
		group = pair;
		nodeCount = m;
		setup(nodeCount);
	}
	
	public ReVo_ABE(Pairing pair, int m, long seed) {
		group = pair;
		nodeCount = m;
		setup(nodeCount, seed);
	}
	
	public ReVo_ABE(int m, Pairing pair, PublicKey pk, MasterKey mk) {
		nodeCount = m;
		group = pair;
		publicKey = pk;
		masterKey = mk;
	}
	
	

	
	private void setup(int m) {
		nodeCount = m;
		Element g1 = group.getG1().newRandomElement().getImmutable();
		Element g2 = group.getG2().newRandomElement().getImmutable();
		Element alpha = group.getZr().newRandomElement().getImmutable();
		Element beta = group.getZr().newRandomElement().getImmutable();
		Element g1_alpha = g1.powZn(alpha).getImmutable();
		Element g2_beta = g2.powZn(beta).getImmutable();
		Element e_gg_alpha = group.pairing(g1_alpha, g2).getImmutable();
		Element a = group.getZr().newRandomElement().getImmutable();
		Element g1_a = g1.powZn(a).getImmutable();
		MembershipTree membership_tree = new MembershipTree(m,g1,group);
		
		publicKey = new PublicKey(membership_tree, g1,g2,g2_beta,e_gg_alpha,g1_a);
		masterKey = new MasterKey(g1_alpha, beta);
		
	}
	
	private void setup(int m, long seed) {
		nodeCount = m;
		Element g1 = group.getG1().newRandomElement().getImmutable();
		Element g2 = group.getG2().newRandomElement().getImmutable();
		Element alpha = group.getZr().newRandomElement().getImmutable();
		Element beta = group.getZr().newRandomElement().getImmutable();
		Element g1_alpha = g1.powZn(alpha).getImmutable();
		Element g2_beta = g2.powZn(beta).getImmutable();
		Element e_gg_alpha = group.pairing(g1_alpha, g2).getImmutable();
		Element a = group.getZr().newRandomElement().getImmutable();
		Element g1_a = g1.powZn(a).getImmutable();
		MembershipTree membership_tree = new MembershipTree(m,g1,group, seed);
		
		publicKey = new PublicKey(membership_tree, g1,g2,g2_beta,e_gg_alpha,g1_a);
		masterKey = new MasterKey(g1_alpha, beta);
		
	}
	
	public PrivateKey keyGen(List<String> attr_list, int user_id) {
		if (publicKey.membership_tree == null || user_id < 1 || user_id > nodeCount) {
			return null;
		}
		List<String> al= new ArrayList<String>();
		for(String attr:attr_list) {
			al.add(attr.toUpperCase());
		}
		
		Element t = group.getZr().newRandomElement().getImmutable();
		Element g_alpha_at = masterKey.g1_alpha.mul(publicKey.g1_a.powZn(t)).getImmutable();
		Element L = publicKey.g2.powZn(t).getImmutable();
		HashMap<String, Element> K_i = new HashMap<String, Element>();
		HashMap<Integer,Element> K_y = new HashMap<Integer, Element>();
		for(TreeNode node : publicKey.membership_tree.getUserPath(user_id)) {
			
			K_y.put(node.y_i, (g_alpha_at.mul(node.g_y_i)).powZn(masterKey.beta.invert()).getImmutable());
		}
		for(String attr : al) {
			byte[] at = attr.getBytes();
			Element value = group.getG1().newElementFromHash(at, 0, at.length).getImmutable();
			
			K_i.put(attr, value.powZn(t).getImmutable());
		}
		return new PrivateKey(al,K_i,L,K_y);
	}
	
	public static PrivateKey keyGen(PublicKey publicKey, MasterKey masterKey, List<String> attr_list, int user_id ) {
		if (publicKey.membership_tree == null || user_id < 1 || user_id > publicKey.membership_tree.m) {
			return null;
		}
		List<String> al= new ArrayList<String>();
		for(String attr:attr_list) {
			al.add(attr.toUpperCase());
		}
		
		Element t = publicKey.membership_tree.group.getZr().newRandomElement().getImmutable();
		Element g_alpha_at = masterKey.g1_alpha.mul(publicKey.g1_a.powZn(t)).getImmutable();
		Element L = publicKey.g2.powZn(t).getImmutable();
		HashMap<String, Element> K_i = new HashMap<String, Element>();
		HashMap<Integer,Element> K_y = new HashMap<Integer, Element>();
		for(TreeNode node : publicKey.membership_tree.getUserPath(user_id)) {
			
			K_y.put(node.y_i, (g_alpha_at.mul(node.g_y_i)).powZn(masterKey.beta.invert()).getImmutable());
		}
		for(String attr : al) {
			byte[] at = attr.getBytes();
			Element value = publicKey.membership_tree.group.getG1().newElementFromHash(at, 0, at.length).getImmutable();
			
			K_i.put(attr, value.powZn(t).getImmutable());
		}
		return new PrivateKey(al,K_i,L,K_y);
	}

	
	public Ciphertext encrypt(PublicKey pk, byte[] msg, String policyString, List<Integer> RL) {
		MSP_Builder util = new MSP_Builder();
		BinNode policy = util.createPolicy(policyString);

		Hashtable<String,List<Integer>> mono_span_prog = util.convert_policy_to_msp(policy);
		int num_cols = util.getLongestRow();
		
		List<Element> u = new ArrayList<Element>();
		for (int i=0; i<num_cols; i++) {			
			Element rand = this.group.getZr().newRandomElement().getImmutable();
			u.add(rand);
		}
		//shared secret
		Element s = u.get(0).getImmutable();
		Element r = this.group.getZr().newRandomElement().getImmutable();
		Element C_prime = pk.g2_beta.powZn(s).getImmutable();
		Element D = pk.g2.powZn(r).getImmutable();
		HashMap<Integer,Element> C_y = new HashMap<Integer,Element>();
		for (TreeNode node : pk.membership_tree.getSubsetCover(RL)) {
			C_y.put(node.y_i, node.g_y_i.powZn(s).getImmutable());
		}
		
		HashMap<String,Element> C_i = new HashMap<String,Element>();
		for (Map.Entry<String, List<Integer>> ele : mono_span_prog.entrySet()) {
			
			String attr = ele.getKey();
			//System.out.println(ele);
			List<Integer> row = ele.getValue();
			int cols = row.size();
			Element lambda_i = this.group.getZr().newZeroElement().getImmutable();
			//System.out.println(attr);
			for (int i = 0; i<cols; i++) {
				lambda_i = lambda_i.add(u.get(i).mul(row.get(i)));
			}
			String attr_stripped = MSP_Builder.strip_index(attr);
			byte[] at = attr_stripped.getBytes();
			C_i.put(attr, (pk.g1_a.powZn(lambda_i)).div(this.group.getG1().newElementFromHash(at , 0, at.length).powZn(r)).getImmutable());
			
		}
		Element seed = this.group.getGT().newRandomElement().getImmutable();
		Element C = (pk.e_gg_alpha.powZn(s)).mul(seed).getImmutable();
		byte[] aes_ci = AES.encrypt(msg, seed.toBytes());
		
		return new Ciphertext(policy, C, C_prime, D, C_y, C_i,aes_ci,policyString);
	}
	
	
	public static Ciphertext encrypt(Pairing pair, PublicKey pk, byte[] msg, String policyString, List<Integer> RL) {
		MSP_Builder util = new MSP_Builder();
		BinNode policy = util.createPolicy(policyString);

		Hashtable<String,List<Integer>> mono_span_prog = util.convert_policy_to_msp(policy);
		int num_cols = util.getLongestRow();
		
		List<Element> u = new ArrayList<Element>();
		for (int i=0; i<num_cols; i++) {			
			Element rand = pair.getZr().newRandomElement().getImmutable();
			u.add(rand);
		}
		//shared secret
		Element s = u.get(0).getImmutable();
		Element r = pair.getZr().newRandomElement().getImmutable();
		Element C_prime = pk.g2_beta.powZn(s).getImmutable();
		Element D = pk.g2.powZn(r).getImmutable();
		HashMap<Integer,Element> C_y = new HashMap<Integer,Element>();
		for (TreeNode node : pk.membership_tree.getSubsetCover(RL)) {
			C_y.put(node.y_i, node.g_y_i.powZn(s).getImmutable());
		}
		
		HashMap<String,Element> C_i = new HashMap<String,Element>();
		for (Map.Entry<String, List<Integer>> ele : mono_span_prog.entrySet()) {
			
			String attr = ele.getKey();
			//System.out.println(ele);
			List<Integer> row = ele.getValue();
			int cols = row.size();
			Element lambda_i = pair.getZr().newZeroElement().getImmutable();
			//System.out.println(attr);
			for (int i = 0; i<cols; i++) {
				lambda_i = lambda_i.add(u.get(i).mul(row.get(i)));
			}
			String attr_stripped = MSP_Builder.strip_index(attr);
			byte[] at = attr_stripped.getBytes();
			C_i.put(attr, (pk.g1_a.powZn(lambda_i)).div(pair.getG1().newElementFromHash(at , 0, at.length).powZn(r)).getImmutable());
			
		}
		Element seed = pair.getGT().newRandomElement().getImmutable();
		Element C = (pk.e_gg_alpha.powZn(s)).mul(seed).getImmutable();
		byte[] aes_ci = AES.encrypt(msg, seed.toBytes());
		System.out.println("encryption done, seed: "+seed.toString());
		return new Ciphertext(policy, C, C_prime, D, C_y, C_i,aes_ci,policyString );
	}
	
	
	public static void printBytes(byte[] bt) {
		for (byte b : bt) {
			System.out.print(Integer.toHexString(b)+", ");
		}
	}
	
	public byte[] decrypt(PublicKey pk, Ciphertext ctxt, PrivateKey key) {
		HashMap<Integer, Element> Ky = (HashMap<Integer, Element>) key.k_y.clone();
		Set<Integer> common_y_i = Ky.keySet();
		if (!common_y_i.retainAll(ctxt.C_y.keySet())) {
			//System.out.println("This user is in the revocation list.");

			return "This user is in the revocation list.".getBytes();
		}
		if (common_y_i.toArray().length==0) {
			return "This user is in the revocation list.".getBytes();
		}
		int y_i = (int) common_y_i.toArray()[0];
		
		Element P = this.group.pairing(key.k_y.get(y_i), ctxt.C_prime).getImmutable();
		Element Q = this.group.pairing(ctxt.C_y.get(y_i), pk.g2).getImmutable();
		if(key.attr_list==null || key.attr_list.size()==0) {
			return "attributes not exists".getBytes();
		}
		List<BinNode> nodes = MSP_Builder.prune(ctxt.policy, key.attr_list);
		if (nodes == null) {
			//System.out.println("Policy not satisfied.");
			return "Policy not satisfied.".getBytes();
		}
		
		Element prodC_i = null;
		Element prodK_i = null;
		
		for(BinNode node:nodes) {
			String attr= node.getAttributeAndIndex();
			String attr_stripped = MSP_Builder.strip_index(attr);
			//System.out.println(attr);
			if(prodC_i==null)prodC_i = ctxt.C_i.get(attr).getImmutable();
			else
				prodC_i = prodC_i.mul(ctxt.C_i.get(attr)).getImmutable();
			
			if(prodK_i==null)prodK_i = key.k_i.get(attr_stripped).getImmutable();
			else
				prodK_i = prodK_i.mul(key.k_i.get(attr_stripped)).getImmutable();
		}
		
		Element W = P.div((Q.mul(this.group.pairing(prodC_i, key.L)).mul(this.group.pairing(prodK_i, ctxt.D)))).getImmutable();
		Element seed = ctxt.C.div(W).getImmutable();
		
		return AES.decrypt(ctxt.ciphertext, seed.toBytes());
	}
	
	public static byte[] decrypt(Pairing pair, PublicKey pk, Ciphertext ctxt, PrivateKey key) {
		HashMap<Integer, Element> Ky = (HashMap<Integer, Element>) key.k_y.clone();
		Set<Integer> common_y_i = Ky.keySet();
		if (!common_y_i.retainAll(ctxt.C_y.keySet())) {
			//System.out.println("This user is in the revocation list.");

			return "This user is in the revocation list.".getBytes();
		}
		if (common_y_i.toArray().length==0) {
			return "This user is in the revocation list.".getBytes();
		}
		int y_i = (int) common_y_i.toArray()[0];
		
		Element P = pair.pairing(key.k_y.get(y_i), ctxt.C_prime).getImmutable();
		Element Q = pair.pairing(ctxt.C_y.get(y_i), pk.g2).getImmutable();

		if(key.attr_list==null || key.attr_list.size()==0) {
			return "attributes not exists".getBytes();
		}
		List<BinNode> nodes = MSP_Builder.prune(ctxt.policy, key.attr_list);
		if (nodes == null) {
			//System.out.println("Policy not satisfied.");
			return "Policy not satisfied.".getBytes();
		}
		Element prodC_i = null;
		Element prodK_i = null;
		
		for(BinNode node:nodes) {
			String attr= node.getAttributeAndIndex();
			String attr_stripped = MSP_Builder.strip_index(attr);
			//System.out.println(attr);
			if(prodC_i==null)prodC_i = ctxt.C_i.get(attr).getImmutable();
			else
				prodC_i = prodC_i.mul(ctxt.C_i.get(attr)).getImmutable();
			
			if(prodK_i==null)prodK_i = key.k_i.get(attr_stripped).getImmutable();
			else
				prodK_i = prodK_i.mul(key.k_i.get(attr_stripped)).getImmutable();
		}
		
		Element W = P.div((Q.mul(pair.pairing(prodC_i, key.L)).mul(pair.pairing(prodK_i, ctxt.D)))).getImmutable();
		Element seed = ctxt.C.div(W).getImmutable();
		
		
		System.out.println("To the last step of decrypt for Revo_abe, seed: "+seed.toString());
		return AES.decrypt(ctxt.ciphertext, seed.toBytes());
	}
	
	
	public int getNodeCount() {
		return nodeCount;
	}
	
	public PublicKey getPublicKey() {
		return publicKey;
	}
	
	public MasterKey getMasterKey() {
		return masterKey;
	}

	
}
