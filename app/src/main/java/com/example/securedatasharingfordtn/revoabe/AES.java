package com.example.securedatasharingfordtn.revoabe;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
 
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
 
public class AES {
 
    //private static SecretKeySpec secretKey;
    //private static byte[] key;

    public static String hashPassword(String password){
        MessageDigest sha = null;
        byte[] key = password.getBytes(StandardCharsets.UTF_8);
        try {
            sha = MessageDigest.getInstance("SHA-256");
            byte[] newKey = sha.digest(key);
            return new String(newKey);
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return password;
    }


    public static SecretKeySpec setKey(byte[] key) 
    {
        MessageDigest sha = null;
        SecretKeySpec secretKey = null;
        try {
            //key = myKey.getBytes("UTF-8");
            sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16); 
            secretKey = new SecretKeySpec(key, "AES");
        } 
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } 
        return secretKey;

    }
 
    public static byte[] encrypt(byte[] dataToEncrypt, byte[] secret) 
    {
    	MessageDigest sha = null;
        try
        {
        	SecretKeySpec secretKey = setKey(secret);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
           
            byte[] msg_ct = cipher.doFinal(dataToEncrypt);
            byte[] cat_msg = ByteBuffer.allocate(secret.length+msg_ct.length).put(secret).put(msg_ct).array();
            sha = MessageDigest.getInstance("SHA-224");
            byte[] VK = sha.digest(cat_msg);
            
            
            byte[] ret = ByteBuffer.allocate(VK.length+msg_ct.length).put(VK).put(msg_ct).array();
            
            //System.out.println(String.format("%d, %d, %d, %d", msg_ct.length,cat_msg.length,VK.length,ret.length));
            
            return ret;
        } 
        catch (Exception e) 
        {
            System.out.println("Error while encrypting: " + e.toString());
        }
        return null;
    }
 
    public static byte[] decrypt(byte[] dataToDecrypt, byte[] secret) 
    {
    	MessageDigest sha = null;
        try
        {
        	byte[] VK = Arrays.copyOfRange(dataToDecrypt, 0, 28);
        	byte[] msg_ct = Arrays.copyOfRange(dataToDecrypt, 28,dataToDecrypt.length);        	
            byte[] cat_msg = ByteBuffer.allocate(secret.length+msg_ct.length).put(secret).put(msg_ct).array();
            
            
            sha = MessageDigest.getInstance("SHA-224");
            if(!Arrays.equals(VK,sha.digest(cat_msg))) {
            	//System.out.println("AES found verification not match");
            	return "AES found verification not match, wrong keys".getBytes();
            }
        	//System.out.println("verification succeed");
        	SecretKeySpec secretKey = setKey(secret);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);

            
            return cipher.doFinal(msg_ct);
        } 
        catch (Exception e) 
        {
            System.out.println("Error while decrypting: " + e.toString());
        }
        //System.out.println("Something wrong and return null for AES");
        return "Something wrong and return null for AES".getBytes();
    }
}