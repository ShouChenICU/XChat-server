package icu.xchat.server.utils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * 加解密工具类
 *
 * @author shouchen
 */
public final class EncryptUtils {
    private static final int KEY_SIZE = 256;
    private static final String ENCRYPT_ALGORITHM = "AES";

    public static SecretKey genAesKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(ENCRYPT_ALGORITHM);
        keyGenerator.init(KEY_SIZE, new SecureRandom());
        return keyGenerator.generateKey();
    }

    public static Cipher getEncryptCipher(SecretKey encryptKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        Cipher cipher = Cipher.getInstance(ENCRYPT_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, encryptKey);
        return cipher;
    }

    public static Cipher getDecryptCipher(SecretKey decryptKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        Cipher cipher = Cipher.getInstance(ENCRYPT_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, decryptKey);
        return cipher;
    }

    public static Cipher getEncryptCipher() throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        Cipher cipher = Cipher.getInstance(SecurityKeyPairTool.getKeyPairType());
        cipher.init(Cipher.PUBLIC_KEY, SecurityKeyPairTool.getPublicKey());
        return cipher;
    }

    public static Cipher getDecryptCipher() throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        Cipher cipher = Cipher.getInstance(SecurityKeyPairTool.getKeyPairType());
        cipher.init(Cipher.PRIVATE_KEY, SecurityKeyPairTool.getPrivateKey());
        return cipher;
    }

    public static Cipher getEncryptCipher(String algorithm, PublicKey publicKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.PUBLIC_KEY, publicKey);
        return cipher;
    }

    public static Cipher getDecryptCipher(String algorithm, PrivateKey privateKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.PRIVATE_KEY, privateKey);
        return cipher;
    }

    public static PublicKey getPublicKey(String algorithm, byte[] encode) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
        return keyFactory.generatePublic(new X509EncodedKeySpec(encode));
    }

    public static PrivateKey getPrivateKey(String algorithm, byte[] encode) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
        return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(encode));
    }
}
