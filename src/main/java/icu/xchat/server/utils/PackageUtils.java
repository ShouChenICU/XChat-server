package icu.xchat.server.utils;

import icu.xchat.server.net.PacketBody;
import org.bson.BSONObject;
import org.bson.BasicBSONObject;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.zip.DataFormatException;

/**
 * 网络传输帧工具类
 *
 * @author shouchen
 */
public class PackageUtils {
    private static final String ENCRYPT_ALGORITHM = "AES/GCM/NoPadding";
    private static final int T_LEN = 128;
    private SecretKey key;
    private Cipher encryptCipher;
    private Cipher decryptCipher;
    private byte[] encryptIV;
    private byte[] decryptIV;

    public PackageUtils() {
    }

    public PackageUtils initCrypto(SecretKey key, byte[] encryptIV, byte[] decryptIV) throws NoSuchPaddingException, NoSuchAlgorithmException {
        this.key = key;
        this.encryptIV = encryptIV;
        this.decryptIV = decryptIV;
        this.encryptCipher = Cipher.getInstance(ENCRYPT_ALGORITHM);
        this.decryptCipher = Cipher.getInstance(ENCRYPT_ALGORITHM);
        return this;
    }

    public PackageUtils setEncryptCipher(Cipher encryptCipher) {
        this.encryptCipher = encryptCipher;
        return this;
    }

    public PackageUtils setDecryptCipher(Cipher decryptCipher) {
        this.decryptCipher = decryptCipher;
        return this;
    }

    public PackageUtils setEncryptIV(byte[] encryptIV) {
        this.encryptIV = encryptIV;
        return this;
    }

    public PackageUtils setDecryptIV(byte[] decryptIV) {
        this.decryptIV = decryptIV;
        return this;
    }

    public byte[] encodePacket(PacketBody packetBody) throws IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException {
        BSONObject object = new BasicBSONObject();
        object.put("TASK_ID", packetBody.getTaskId());
        object.put("ID", packetBody.getId());
        object.put("TASK_TYPE", packetBody.getTaskType());
        object.put("DATA", packetBody.getData());
        byte[] data;
        if (this.key != null) {
            this.encryptCipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(T_LEN, encryptIV));
            byte[] iv = EncryptUtils.genIV();
            object.put("IV", iv);
            this.encryptIV = iv;
            data = CompressionUtils.compress(BsonUtils.encode(object));
            data = encryptCipher.doFinal(data);
        } else if (encryptCipher != null) {
            data = CompressionUtils.compress(BsonUtils.encode(object));
            data = encryptCipher.doFinal(data);
        } else {
            data = CompressionUtils.compress(BsonUtils.encode(object));
        }
        return data;
    }

    public PacketBody decodePacket(byte[] data) throws IllegalBlockSizeException, BadPaddingException, DataFormatException, InvalidAlgorithmParameterException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException {
        BSONObject object;
        if (this.key != null) {
            this.decryptCipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(T_LEN, decryptIV));
            data = decryptCipher.doFinal(data);
            data = CompressionUtils.deCompress(data);
            object = BsonUtils.decode(data);
            this.decryptIV = (byte[]) object.get("IV");
        } else if (decryptCipher != null) {
            data = decryptCipher.doFinal(data);
            data = CompressionUtils.deCompress(data);
            object = BsonUtils.decode(data);
        } else {
            data = CompressionUtils.deCompress(data);
            object = BsonUtils.decode(data);
        }
        return new PacketBody()
                .setTaskId((Integer) object.get("TASK_ID"))
                .setId((Integer) object.get("ID"))
                .setTaskType(((Integer) object.get("TASK_TYPE")))
                .setData((byte[]) object.get("DATA"));
    }
}
