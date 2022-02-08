package icu.xchat.server.net.tasks;

import icu.xchat.server.GlobalVariables;
import icu.xchat.server.database.DaoManager;
import icu.xchat.server.entities.UserInfo;
import icu.xchat.server.exceptions.LoginException;
import icu.xchat.server.net.Client;
import icu.xchat.server.net.PacketBody;
import icu.xchat.server.net.WorkerThreadPool;
import icu.xchat.server.utils.EncryptUtils;
import icu.xchat.server.utils.SecurityKeyPairTool;
import org.bson.BSONObject;
import org.bson.BasicBSONDecoder;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.Base64;
import java.util.Objects;
import java.util.Random;

/**
 * 用户登陆任务
 *
 * @author shouchen
 */
public class UserLoginTask extends AbstractTask {
    private final Client client;
    private UserInfo userInfo;
    private byte[] authCode;

    public UserLoginTask(Client client) throws LoginException {
        super(null, null);
        if (client.getUserInfo() != null) {
            throw new LoginException("重复登陆");
        }
        this.packetSum = 3;
        this.client = client;
    }

    @Override
    public void handlePacket(PacketBody packetBody) throws Exception {
        byte[] data = packetBody.getData();
        BSONObject bsonObject;
        switch (packetBody.getId()) {
            case 0:
                bsonObject = new BasicBSONDecoder().readObject(data);
                if (!Objects.equals(GlobalVariables.PROTOCOL_VERSION, bsonObject.get("PROTOCOL_VERSION"))) {
                    throw new LoginException("通讯协议版本错误");
                }
                client.getPackageUtils().setDecryptCipher(EncryptUtils.getDecryptCipher());
                PacketBody finalPacket = new PacketBody()
                        .setTaskId(this.taskId)
                        .setId(0)
                        .setData(SecurityKeyPairTool.getPublicKey().getEncoded());
                WorkerThreadPool.execute(() -> client.postPacket(finalPacket));
                break;
            case 1:
                client.getPackageUtils().setEncryptKey(new SecretKeySpec(packetBody.getData(), "AES"));
                finalPacket = new PacketBody()
                        .setTaskId(this.taskId)
                        .setId(1);
                WorkerThreadPool.execute(() -> client.postPacket(finalPacket));
                break;
            case 2:
                PublicKey publicKey = EncryptUtils.getPublicKey("RSA", data);
                userInfo = DaoManager.getUserInfoDao().getUserInfoByUidCode(getUidCode(publicKey.getEncoded()));
                if (userInfo == null) {
                    throw new LoginException("用户不存在");
                }
                Cipher cipher = EncryptUtils.getEncryptCipher("RSA", publicKey);
                genAuthCode();
                finalPacket = new PacketBody()
                        .setTaskId(this.taskId)
                        .setId(2)
                        .setData(cipher.doFinal(authCode));
                WorkerThreadPool.execute(() -> client.postPacket(finalPacket));
                break;
            case 3:
                if (Arrays.equals(authCode, data)) {
                    client.setUserInfo(this.userInfo);
                } else {
                    throw new LoginException("用户验证失败");
                }
                done();
                break;
        }
    }

    @Override
    public void done() {
        System.out.println("登陆成功\n" + userInfo);
        client.removeTask(this.taskId);
    }

    private String getUidCode(byte[] encode) throws NoSuchAlgorithmException {
        byte[] digest = MessageDigest.getInstance("SHA-512").digest();
        byte[] part = new byte[12];
        System.arraycopy(digest, 0, part, 0, 12);
        return Base64.getEncoder().encodeToString(part);
    }

    private void genAuthCode() {
        Random random = new Random();
        this.authCode = new byte[random.nextInt(5) + 10];
        random.nextBytes(authCode);
    }

    @Override
    public PacketBody startPacket() {
        return null;
    }
}
