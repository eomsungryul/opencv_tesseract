package com.tessModule.tess.cmmn.util;

import java.security.MessageDigest;

import org.springframework.stereotype.Component;

@Component("scrtyUtil")
public class ScrtyUtil {
	

	/**
	 * api키와 id를 암호화하는 기능
	 *
	 * @param apikey 암호화될 패스워드
	 * @param id salt로 사용될 사용자 ID 지정
	 * @return
	 * @throws Exception
	 */
	public static String encryptApi(String apikey, String id) throws Exception {
		 StringBuffer sbuf = new StringBuffer();
	     
		    MessageDigest mDigest = MessageDigest.getInstance("SHA-256");
		    mDigest.update(id.getBytes());
		    byte[] msgStr = mDigest.digest(apikey.getBytes()) ;
		     
		    for(int i=0; i < msgStr.length; i++){
		        byte tmpStrByte = msgStr[i];
		        String tmpEncTxt = Integer.toString((tmpStrByte & 0xff) + 0x100, 16).substring(1);
		         
		        sbuf.append(tmpEncTxt) ;
		    }
		return sbuf.toString();
	}

}
