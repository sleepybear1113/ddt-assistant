package cn.xiejx.ddtassistant.utils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2022/10/06 21:23
 */
public class EncryptUtil {
    public static void main(String[] args) throws Exception {
        String src = "http://pc.sleepybear.cn:9999";
        String key = "1q2w3e4r5t6y7u8i";

        String x = aesEncrypt(src, key);
        System.out.println(x);
        String s = aesDecrypt(x, key);
        System.out.println(s);
    }

    public static String aesEncrypt(String sSrc, String sKey){
        if (sKey == null || sKey.length() != 16) {
            return null;
        }
        byte[] raw = sKey.getBytes(StandardCharsets.UTF_8);
        SecretKeySpec secretKeySpec = new SecretKeySpec(raw, "AES");
        try {
            // "算法/模式/补码方式"
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            byte[] encrypted = cipher.doFinal(sSrc.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException |
                 InvalidKeyException e) {
            return null;
        }

    }

    public static String aesDecrypt(String sSrc, String key) {
        try {
            if (key == null || key.length() != 16) {
                return null;
            }
            byte[] raw = key.getBytes(StandardCharsets.UTF_8);
            SecretKeySpec secretKeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            byte[] decode = Base64.getDecoder().decode(sSrc);
            byte[] original = cipher.doFinal(decode);
            return new String(original, StandardCharsets.UTF_8);
        } catch (Exception ex) {
            return null;
        }
    }

}
