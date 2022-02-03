package icu.xchat.server.utils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public final class EncryptUtils {
    private static final int KEY_SIZE = 256;
    private static String ENCRYPT_ALGORITHM;

    public static void init(String keypairAlgorithm) {
        ENCRYPT_ALGORITHM = keypairAlgorithm;
    }

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
}
