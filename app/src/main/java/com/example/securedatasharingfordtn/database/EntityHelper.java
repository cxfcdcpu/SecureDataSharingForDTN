package com.example.securedatasharingfordtn.database;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class EntityHelper {
	public static byte[] int_to_bytes(int myI) {
		return ByteBuffer.allocate(4).order(ByteOrder.nativeOrder()).putInt(myI).array();
	}
	
	public static int bytes_to_int(byte[] myB) {
		return ByteBuffer.wrap(myB).order(ByteOrder.nativeOrder()).getInt();
	}
	
	public static byte[] stringList_to_bytes(List<String> sl) {
		byte[] valueByteArray = String.join(",", sl).getBytes();
		ByteArrayOutputStream bs = new ByteArrayOutputStream();
		try {
			bs.write(int_to_bytes(valueByteArray.length));
			bs.write(valueByteArray);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return bs.toByteArray();
	}
	
	public static List<String> bytes_to_stringList(byte[] sb){
		String curString = new String(sb, StandardCharsets.UTF_8);
		return Arrays.asList(curString.split(","));
	}
	
	public static void printStringList(List<String> sl) {
		for(String s:sl) {
			System.out.println(s);
		}
	}

	public static void testKotlinByteArray(byte[] kotlinByteArray){

	}
	
	
}
