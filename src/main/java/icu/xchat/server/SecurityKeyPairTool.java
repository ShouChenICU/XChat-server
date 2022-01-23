package icu.xchat.server;

import org.bson.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

/**
 * 服务端密钥对工具类
 *
 * @author shouchen
 */
public final class SecurityKeyPairTool {
    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityKeyPairTool.class);
    private static final String KEYPAIR_FILE_NAME = "xchat-server.key";
    private static final String TIME_STAMP = "time-stamp";
    private static final String PUBLIC_KEY = "public-key";
    private static final String PRIVATE_KEY = "private-key";
    public static final String KEYPAIR_ALGORITHM_RSA = "RSA";
    public static final int KEYPAIR_SIZE_DEFAULT = 4096;
    private static KeyPair keypair;
    private static PublicKey publicKey;
    private static PrivateKey privateKey;

    /**
     * 生成RSA密钥对并保存
     *
     * @param keypairSize 密钥长度
     * @throws NoSuchAlgorithmException 类型错误
     */
    private static void genRsaKeypair(int keypairSize) throws NoSuchAlgorithmException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance(KEYPAIR_ALGORITHM_RSA);
        generator.initialize(keypairSize, new SecureRandom());
        keypair = generator.generateKeyPair();
        publicKey = keypair.getPublic();
        privateKey = keypair.getPrivate();
        BSONObject bsonObject = new BasicBSONObject();
        bsonObject.put(PUBLIC_KEY, publicKey.getEncoded());
        bsonObject.put(PRIVATE_KEY, privateKey.getEncoded());
        bsonObject.put(TIME_STAMP, System.currentTimeMillis());
        BSONEncoder encoder = new BasicBSONEncoder();
        byte[] buf = encoder.encode(bsonObject);
        try (FileOutputStream outputStream = new FileOutputStream(KEYPAIR_FILE_NAME);
             DeflaterOutputStream deflaterOutputStream = new DeflaterOutputStream(outputStream, new Deflater(Deflater.BEST_COMPRESSION))) {
            deflaterOutputStream.write(buf);
            deflaterOutputStream.flush();
        } catch (IOException e) {
            LOGGER.error("", e);
        }
    }

    public static PublicKey getPublicKey() {
        return publicKey;
    }

    public static PrivateKey getPrivateKey() {
        return privateKey;
    }

    /**
     * 生成服务端密钥对
     *
     * @param keyPairType 密钥对类型
     * @throws NoSuchAlgorithmException 密钥对类型错误
     */
    public static synchronized void genKeypair(String keyPairType, int keypairSize) throws NoSuchAlgorithmException {
        if (new File(KEYPAIR_FILE_NAME).exists()) {
            LOGGER.warn("已存在密钥文件！");
            return;
        }
        if (KEYPAIR_ALGORITHM_RSA.equalsIgnoreCase(keyPairType)) {
            genRsaKeypair(keypairSize);
        } else {
            LOGGER.error("不支持的密钥对类型：" + keyPairType);
        }
    }

    /**
     * 加载密钥对
     */
    public static synchronized void loadKeyPair(String keypairType) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        if (keypair != null) {
            LOGGER.warn("不能重复加载密钥！");
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (FileInputStream inputStream = new FileInputStream(KEYPAIR_FILE_NAME);
             InflaterInputStream inflaterInputStream = new InflaterInputStream(inputStream)) {
            byte[] buf = new byte[64];
            int len;
            while ((len = inflaterInputStream.read(buf)) != -1) {
                outputStream.write(buf, 0, len);
            }
        }
        byte[] data = outputStream.toByteArray();
        BSONDecoder decoder = new BasicBSONDecoder();
        BSONObject bsonObject = decoder.readObject(data);
        KeyFactory keyFactory = KeyFactory.getInstance(keypairType);
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec((byte[]) bsonObject.get(PUBLIC_KEY));
        publicKey = keyFactory.generatePublic(x509EncodedKeySpec);
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec((byte[]) bsonObject.get(PRIVATE_KEY));
        privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
    }
}
