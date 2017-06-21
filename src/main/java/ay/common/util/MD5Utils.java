package ay.common.util;

import sun.misc.BASE64Decoder;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by yangfeng3 on 2015/8/2.
 *  para:   String
 *  return: String
 *  func:   encrypt sourceString into 32 bit MD5  String
 */
public class MD5Utils {
    public static String encodeMD5(String rawString){
        String result="";
        MessageDigest md5 = null;
        try {
             md5 = MessageDigest.getInstance("MD5");
            md5.update(rawString.getBytes());
            byte[] encodeString = md5.digest();

            int i=0;
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < encodeString.length; offset++) {
                i = encodeString[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            result = buf.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }finally {
            return result;
        }
    }

    public static boolean verifyMD5(String rawString, String sign){
        String subMD5 = encodeMD5(rawString).substring(5, 21);
        //System.out.println(subMD5);
        //System.out.println(sign);
        return !sign.equals(subMD5);
    }

    public static String decodeBase64(String s) {
        byte[] b = null;
        String result = null;
        if (s != null) {
            BASE64Decoder decoder = new BASE64Decoder();
            try {
                b = decoder.decodeBuffer(s);
                result = new String(b, "utf-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
