package com.ixuxie.utils;

import java.util.Random;

public class StringUtil {

    public static String getRundomNum(){
        Random random = new Random();
        int randomNum = random.nextInt(1000000);
        System.out.println(randomNum);
        String randomCode = String.format("%06d", randomNum);
        return randomCode;
    }
//StringUtil.getPwd(name,pwd);
    public static String getPwd(String name,String pwd){
        return MD5.getMessageDigest(name + pwd);

    }
}
