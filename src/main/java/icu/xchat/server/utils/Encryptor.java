package icu.xchat.server.utils;

import org.bson.BSONObject;
import org.bson.BasicBSONObject;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.util.Random;

/**
 * 网络传输帧加解密器
 *
 * @author shouchen
 */
public class Encryptor {
    private static final String ENCRYPT_ALGORITHM = "AES/GCM/NoPadding";
    private static final int T_LEN = 128;
    private final Random random;
    private SecretKey key;
    private Cipher encryptCipher;
    private Cipher decryptCipher;
    private byte[] encryptIV;
    private byte[] decryptIV;

    public Encryptor() {
        random = new Random();
    }

    public void initCrypto(SecretKey key, byte[] encryptIV, byte[] decryptIV) throws Exception {
        this.key = key;
        this.encryptIV = encryptIV;
        this.decryptIV = decryptIV;
        this.encryptCipher = Cipher.getInstance(ENCRYPT_ALGORITHM);
        this.decryptCipher = Cipher.getInstance(ENCRYPT_ALGORITHM);
    }

    public byte[] encode(byte[] data) throws Exception {
        BSONObject object = new BasicBSONObject();
        object.put("DATA", data);
        this.encryptCipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(T_LEN, encryptIV));
        byte[] iv = genIV();
        object.put("IV", iv);
        this.encryptIV = iv;
        data = CompressionUtils.compress(BsonUtils.encode(object));
        data = encryptCipher.doFinal(data);
        return data;
    }

    public byte[] decode(byte[] data) throws Exception {
        BSONObject object;
        this.decryptCipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(T_LEN, decryptIV));
        data = decryptCipher.doFinal(data);
        data = CompressionUtils.deCompress(data);
        object = BsonUtils.decode(data);
        this.decryptIV = (byte[]) object.get("IV");
        return (byte[]) object.get("DATA");
    }

    private byte[] genIV() {
        byte[] iv = new byte[16];
        random.nextBytes(iv);
        return iv;
    }
}
