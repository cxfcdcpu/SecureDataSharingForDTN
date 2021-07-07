package com.example.securedatasharingfordtn.revoabe;
import java.util.HashMap;
import java.util.List;

import it.unisa.dia.gas.jpbc.*;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import com.example.securedatasharingfordtn.tree_type.*;

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
	
	public ReVo_ABE(int m, Pairing pair, PublicKey pk, MasterKey mk) {
		nodeCount = m;
		group = pair;
		publicKey = pk;
		masterKey = mk;
	}
	
	public PublicKey getPublicKey() {
		return publicKey;
	}
	
	public MasterKey getMasterKey() {
		return masterKey;
	}
	
	
	private void setup(int m) {
		nodeCount = m;
		Element g1 = group.getG1().newRandomElement();
		Element g2 = group.getG2().newRandomElement();
		Element alpha = group.getZr().newRandomElement();
		Element beta = group.getZr().newRandomElement();
		Element g1_alpha = g1.powZn(alpha);
		Element g2_beta = g2.powZn(beta);
		Element e_gg_alpha = group.pairing(g1_alpha, g2);
		Element a = group.getZr().newRandomElement();
		Element g1_a = g1.powZn(a);
		MembershipTree membership_tree = new MembershipTree(m,g1,group);
		
		publicKey = new PublicKey(membership_tree, g1,g2,g2_beta,e_gg_alpha,g1_a);
		masterKey = new MasterKey(g1_alpha, beta);
		
	}
	
	public PrivateKey keygen(List<String> attr_list, int user_id) {
		if (publicKey.membership_tree == null || user_id < 1 || user_id > nodeCount) {
			return null;
		}
		Element t = group.getZr().newRandomElement();
		Element g_alpha_at = masterKey.g1_alpha.mul(publicKey.g1_a.powZn(t));
		Element L = publicKey.g2.powZn(t);
		HashMap<String, Element> K_i = new HashMap<String, Element>();
		HashMap<Integer,Element> K_y = new HashMap<Integer, Element>();
		for(TreeNode node : publicKey.membership_tree.getUserPath(user_id)) {
			
			K_y.put(node.y_i, (g_alpha_at.mul(node.g_y_i)).powZn(masterKey.beta.invert()));
		}
		for(String attr : attr_list) {
			byte[] at = attr.getBytes();
			Element value = group.getG1().newElementFromHash(at, 0, at.length);
			
			K_i.put(attr, value.powZn(t));
		}
		return new PrivateKey(attr_list,K_i,L,K_y);
	}
	
	public int getNodeCount() {
		return nodeCount;
	}
	


	
}
