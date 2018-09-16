package com.tessModule.tess;

import java.security.MessageDigest;

public class paperDetact {
	
	public static String encryptApi(String apikey, String id) throws Exception {
//		if (apikey == null) {
//			return "";
//		}
//
//		byte[] hashValue = null; // 해쉬값
//
//		MessageDigest md = MessageDigest.getInstance("SHA-256");
//
//		md.reset();
//		md.update(id.getBytes());
//
//		hashValue = md.digest(apikey.getBytes());
//		
		
		 StringBuffer sbuf = new StringBuffer();
	     
		    MessageDigest mDigest = MessageDigest.getInstance("SHA-256");
		    mDigest.update(id.getBytes());
		    byte[] msgStr = mDigest.digest(apikey.getBytes()) ;
		     
		    for(int i=0; i < msgStr.length; i++){
		        byte tmpStrByte = msgStr[i];
		        String tmpEncTxt = Integer.toString((tmpStrByte & 0xff) + 0x100, 16).substring(1);
		         
		        sbuf.append(tmpEncTxt) ;
		    }


//		return new String(hashValue);
		return sbuf.toString();
	}
	public static void main(String[] args) throws Exception {
		
		String res = paperDetact.encryptApi("a2awdsdwd", "ss");
		
		System.out.println("res : "+res);
		//0004a3b7d74166bff37a7b1dba45a9e96a6e9276b042ffd7ef41fcbedadb7bfa
		//6ec735064f05a687eb4a1e93ec354750d1da66f83a8bd478cdd8d06218cd80d0
		//86968a5d1a7a6c7c9a3328dfea394aaaa7271aacf056351fa8ccc6a1c7363e2f
	}
}
