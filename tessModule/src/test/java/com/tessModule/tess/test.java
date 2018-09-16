package com.tessModule.tess;

import java.io.File;
import java.security.MessageDigest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class test {
	
	 /**
     * @param args
     */
    public static void main(String[] args)
    {
        System.out.println("무조건 소수점 1,2자리 필수");
        String pattern = "[0-9]{1,3}\\.[0-9]{1,2}";
        test(pattern, "32");
        test(pattern, "3224.1");
        test(pattern, "32.1");
        test(pattern, "312.122");
        test(pattern, "312.122");
        test(pattern, "322.12");
        test(pattern, "3224.12");
        
        System.out.println("소수점 자유 (있던 없던)");
        pattern = "[0-9]*\\.?[0-9]*";
        test(pattern, "32");
        test(pattern, "32a");
        test(pattern, ".123");
        test(pattern, "3.123");
        test(pattern, "333.123");
        test(pattern, "333.1 23");
        
        System.out.println(
                "소수점 위 0~3자리, 소수점 아래 0~2자리, 소수점 없을수도 있음 --> 아래2개패턴 or 조건");
        pattern = "[0-9]{0,3}";
        test(pattern, "32");
        test(pattern, "3234");
        test(pattern, "32.3");
        test(pattern, "32.33");
        test(pattern, "32.334");
        pattern = "[0-9]{0,3}\\.[0-9]{1,2}";
        test(pattern, "32");
        test(pattern, "3234");
        test(pattern, "32.3");
        test(pattern, "32.33");
        test(pattern, "32.334");
        test(pattern, ".32");
        test(pattern, ".3234");
        test(pattern, ".32.3");
        test(pattern, ".32.33");
        test(pattern, ".32.334");
        
    }
    
    public static void test(String pattern, String value)
    {
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(value);
        boolean ok = m.matches();

        System.out.println(pattern+", "+value+", "+(ok?"O":"X"));
    }

}
