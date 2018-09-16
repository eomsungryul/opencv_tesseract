package com.tessModule.tess;

import java.security.MessageDigest;

public class scrty {
	
	/**
	 * 비밀번호를 암호화하는 기능(복호화가 되면 안되므로 SHA-256 인코딩 방식 적용)
	 *
	 * @param apikey 암호화될 패스워드
	 * @param id salt로 사용될 사용자 ID 지정
	 * @return
	 * @throws Exception
	 */
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
		
		String res = scrty.encryptApi("dwebss", "0");
		
		System.out.println("res : "+res);
		//e7fa00d8d0ba81a3c012cc030c20a196c02835657e4dbce80f94289c09f311d4
	}
}
