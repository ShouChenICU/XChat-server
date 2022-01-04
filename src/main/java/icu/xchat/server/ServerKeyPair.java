package icu.xchat.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * 服务端密钥对
 *
 * @author shouchen
 */
public final class ServerKeyPair {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerKeyPair.class);
    private static final String KEYPAIR_FILE_NAME = "XChat-server.key";
    public static final String KEYPAIR_RSA = "rsa";
    public static final String KEYPAIR_SIZE_DEFAULT = "4096";
    private static KeyPair keyPair;

    /**
     * 生成服务端密钥对
     *
     * @param keyPairType 密钥对类型
     * @throws NoSuchAlgorithmException 密钥对类型错误
     */
    public static synchronized void genKeyPair(String keyPairType, int keypairSize) throws NoSuchAlgorithmException {
        if (new File(KEYPAIR_FILE_NAME).exists()) {
            LOGGER.warn("已存在密钥文件！");
            return;
        }
        if (KEYPAIR_RSA.equals(keyPairType)) {
            KeyPairGenerator generator = KeyPairGenerator.getInstance(KEYPAIR_RSA);
            generator.initialize(keypairSize, new SecureRandom());
            keyPair = generator.generateKeyPair();
            // TODO: 2022/1/4
        }
    }

    public static synchronized void loadKeyPair() {
        // TODO: 2022/1/4
    }
}
