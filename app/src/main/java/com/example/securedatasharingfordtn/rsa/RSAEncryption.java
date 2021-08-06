package com.example.securedatasharingfordtn.rsa;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Random;

import it.unisa.dia.gas.plaf.jpbc.util.Arrays;

public class RSAEncryption {

	private final BigInteger e;
	private final BigInteger n;
	private final BigInteger d;

	public RSAEncryption(String password) {
		long[] seeds = getSeeds(password);
		Random rp = new Random(seeds[0]);
		Random rq = new Random(seeds[1]);
		
		BigInteger p = BigInteger.probablePrime(1024, rp);
		BigInteger q = BigInteger.probablePrime(1024, rq);
		
		e = BigInteger.valueOf(65537);
		n = p.multiply(q);
		BigInteger lambda = lcm(p.subtract(BigInteger.ONE), q.subtract(BigInteger.ONE));
		d =e.modInverse(lambda);
		
	}
	
	public String encryptString(String input) {
		BigInteger m = new BigInteger(input.getBytes());
		return m.modPow(e, n).toString();
	}
	
	public byte[] encrypt(byte[] input) {
		BigInteger m = new BigInteger(input);
		return m.modPow(e, n).toByteArray();
	}
	
	
	public String decryptString(String input) {
		BigInteger m = new BigInteger(input);
		return new String(m.modPow(d, n).toByteArray(),StandardCharsets.UTF_8);
	}	
	
	public byte[] decrypt(byte[] input) {
		BigInteger m = new BigInteger(input);
		return m.modPow(d, n).toByteArray();
	}
	
	public BigInteger lcm(BigInteger s, BigInteger s1)
    {
        // convert string 'a' and 'b' into BigInteger

  
        // calculate multiplication of two bigintegers
        BigInteger mul = s.multiply(s1);
  
        // calculate gcd of two bigintegers
        BigInteger gcd = s.gcd(s1);
  
        // calculate lcm using formula: lcm * gcd = x * y
        BigInteger lcm = mul.divide(gcd);
        return lcm;
    }
	
	 public long[] getSeeds(String password) {
		 long ret[] = new long[2];
		 MessageDigest digest;
			try {
				digest = MessageDigest.getInstance("SHA3-256");
				byte[] encodedhash = digest.digest(
					password.getBytes(StandardCharsets.UTF_8));
				byte[] b1 = new byte[16];
				byte[] b2 = new byte[16];
				//System.out.println(encodedhash.length);
				b1 = Arrays.copyOfRange(encodedhash, 0, 15);
				b2 = Arrays.copyOfRange(encodedhash, 16, 31);
				BigInteger modulus = new BigInteger("2147483647");
				BigInteger seedp = new BigInteger(b1).mod(modulus);
				BigInteger seedq = new BigInteger(b2).mod(modulus);
				ret[0] = seedp.longValue();
				ret[1] = seedq.longValue();
				return ret;
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return null;
	 }
	 
	 
}
