package icu.xchat.server.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * 用户身份工具类
 */
public class IdentityUtils {

    /**
     * 从公钥计算标识码
     *
     * @param publicKeyCode 公钥
     * @return 标识码
     */
    public static String getCodeByPublicKeyCode(byte[] publicKeyCode) throws NoSuchAlgorithmException {
        byte[] digestCode;
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        digestCode = messageDigest.digest(publicKeyCode);
        byte[] buf = new byte[12];
        System.arraycopy(digestCode, 0, buf, 0, buf.length);
        return Base64.getEncoder().encodeToString(buf);
    }
}
