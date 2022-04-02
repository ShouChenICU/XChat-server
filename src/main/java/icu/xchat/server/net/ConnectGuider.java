package icu.xchat.server.net;

import icu.xchat.server.GlobalVariables;
import icu.xchat.server.utils.BsonUtils;
import icu.xchat.server.utils.EncryptUtils;
import icu.xchat.server.utils.Encryptor;
import icu.xchat.server.utils.SecurityKeyPairTool;
import org.bson.BSONObject;
import org.bson.BasicBSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.channels.SelectionKey;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

/**
 * 连接引导器
 *
 * @author shouchen
 */
public class ConnectGuider extends AbstractNetIO {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectGuider.class);
    private volatile boolean isDone;
    private final Encryptor encryptor;
    private Client client;
    private byte[] authCode;
    private int status;

    public ConnectGuider(SelectionKey key) {
        super(key);
        isDone = false;
        encryptor = new Encryptor();
        this.status = 0;
        key.attach(this);
        key.interestOps(SelectionKey.OP_READ);
        key.selector().wakeup();
    }

    @Override
    protected void dataHandler(byte[] data) throws Exception {
        BSONObject object;
        if (status == 0) {
            object = BsonUtils.decode(data);
            int protocol = (int) object.get("PROTOCOL");
            // 验证通讯协议版本
            if (Objects.equals(protocol, GlobalVariables.PROTOCOL_VERSION)) {
                // 密钥协商
                object.put("PUB_KEY", SecurityKeyPairTool.getPublicKey().getEncoded());
                status = 1;
                doWrite(BsonUtils.encode(object));
            } else {
                throw new Exception("protocol error");
            }
        } else if (status == 1) {
            // 用服务端私钥解密
            Cipher cipher = EncryptUtils.getDecryptCipher(SecurityKeyPairTool.getPrivateKey());
            data = cipher.doFinal(data);
            object = BsonUtils.decode(data);
            // 获取密钥数据和IV信息
            SecretKey aesKey = new SecretKeySpec((byte[]) object.get("KEY"), "AES");
            byte[] encryptIV = (byte[]) object.get("ENCRYPT_IV");
            byte[] decryptIV = (byte[]) object.get("DECRYPT_IV");
            // 初始化加解密器
            encryptor.initCrypto(aesKey, decryptIV, encryptIV);
            status = 2;
        } else if (status == 2) {
            // 获取身份识别码
            data = encryptor.decode(data);
            object = BsonUtils.decode(data);
            String uidCode = (String) object.get("UID_CODE");
            client = ClientManager.loadClient(uidCode);
            // 找不到用户
            if (client == null) {
                String err = "User not found";
                object = new BasicBSONObject();
                object.put("STATUS", false);
                object.put("CONTENT", err.getBytes(StandardCharsets.UTF_8));
                doWrite(encryptor.encode(BsonUtils.encode(object)));
                throw new Exception(err);
            } else if (client.isConnect()) {
                // 用户重复登陆
                String err = "User logged in repeatedly";
                object = new BasicBSONObject();
                object.put("STATUS", false);
                object.put("CONTENT", err.getBytes(StandardCharsets.UTF_8));
                doWrite(encryptor.encode(BsonUtils.encode(object)));
                throw new Exception(err);
            }
            // 验证身份
            Cipher cipher = EncryptUtils.getEncryptCipher(client.getUserInfo().getPublicKey());
            authCode = genAuthCode();
            object = new BasicBSONObject();
            object.put("STATUS", true);
            object.put("CONTENT", cipher.doFinal(authCode));
            doWrite(encryptor.encode(BsonUtils.encode(object)));
            status = 3;
        } else if (status == 3) {
            data = encryptor.decode(data);
            object = BsonUtils.decode(data);
            byte[] authC = (byte[]) object.get("AUTH_CODE");
            if (Arrays.equals(authC, authCode)) {
                // 验证成功
                isDone = true;
                object = new BasicBSONObject();
                object.put("STATUS", true);
                doWrite(encryptor.encode(BsonUtils.encode(object)));
                key.attach(client);
                client.updateEncryptor(encryptor);
                client.update(this);
            } else {
                // 验证失败
                String err = "Authentication failed";
                object = new BasicBSONObject();
                object.put("STATUS", false);
                object.put("CONTENT", err.getBytes(StandardCharsets.UTF_8));
                doWrite(encryptor.encode(BsonUtils.encode(object)));
                throw new Exception(err);
            }
        } else {
            throw new Exception("guide fail");
        }
    }

    @Override
    protected void exceptionHandler(Exception exception) {
        LOGGER.warn("", exception);
        try {
            disconnect();
        } catch (Exception e) {
            LOGGER.warn("", e);
        }
    }

    public boolean isDone() {
        return isDone;
    }

    private byte[] genAuthCode() {
        Random random = new Random();
        byte[] authCode = new byte[random.nextInt(5) + 10];
        random.nextBytes(authCode);
        return authCode;
    }
}
