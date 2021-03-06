package cn.com.ava.zqproject.ui.videoResource.util;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES对称加解密工具类
 * @author hwy
 * @date 2021/9/22
 */
public class AESUtils {
    /**
     * AES是加密方式 CBC是工作模式 PKCS5Padding是填充模式
     * */
    private static final String CBC_PKCS5_PADDING = "AES/CBC/PKCS5Padding";
    /**
    * AES 加密
    * */
    private static final String AES = "AES";
    /**
     * 加解密密钥
     */
    public static final String PASSWORD_CRYPT_KEY = "ava_aes_password";

    /**
     * 加密
     *
     * @param content 须要加密的内容
     * @param password 加密密码
     * @return
     */
    public static String encrypt(String content, String password) {
        try {
            IvParameterSpec zeroIv = new IvParameterSpec(password.getBytes());
            SecretKeySpec key1 = new SecretKeySpec(password.getBytes(),AES);
            Cipher cipher = Cipher.getInstance(CBC_PKCS5_PADDING);
            cipher.init(Cipher.ENCRYPT_MODE, key1, zeroIv);
            byte[] encryptedData = cipher.doFinal(content.getBytes());
            String encryptResultStr = parseByte2HexStr(encryptedData);
            return encryptResultStr;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解密
     *
     * @param content 待解密内容
     * @param password 解密密钥
     * @return
     */
    public static String decrypt(String content, String password) {
        try {

            byte[] decryptFrom = parseHexStr2Byte(content);
            IvParameterSpec zeroIv = new IvParameterSpec(password.getBytes());
            SecretKeySpec key1 = new SecretKeySpec(password.getBytes(),AES);
            Cipher cipher = Cipher.getInstance(CBC_PKCS5_PADDING);
            cipher.init(Cipher.DECRYPT_MODE, key1, zeroIv);
            byte decryptedData[] = cipher.doFinal(decryptFrom);
            return new String(decryptedData);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**将二进制转换成16进制
     * @param buf
     * @return
     */
    public static String parseByte2HexStr(byte buf[]) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    /**将16进制转换为二进制
     * @param hexStr
     * @return
     */
    public static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1)
            return null;
        byte[] result = new byte[hexStr.length()/2];
        for (int i = 0;i< hexStr.length()/2; i++) {
            int high = Integer.parseInt(hexStr.substring(i*2, i*2+1), 16);
            int low = Integer.parseInt(hexStr.substring(i*2+1, i*2+2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }
}
