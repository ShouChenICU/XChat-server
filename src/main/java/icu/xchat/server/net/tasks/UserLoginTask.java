package icu.xchat.server.net.tasks;

import icu.xchat.server.GlobalVariables;
import icu.xchat.server.constants.TaskTypes;
import icu.xchat.server.database.DaoManager;
import icu.xchat.server.entities.Identity;
import icu.xchat.server.entities.UserInfo;
import icu.xchat.server.exceptions.LoginException;
import icu.xchat.server.net.PacketBody;
import icu.xchat.server.net.WorkerThreadPool;
import icu.xchat.server.utils.*;
import org.bson.BSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

/**
 * 用户登陆任务
 *
 * @author shouchen
 */
public class UserLoginTask extends AbstractTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserLoginTask.class);
    private UserInfo userInfo;
    private byte[] authCode;

    public UserLoginTask() throws LoginException {
        if (client.getUserInfo() != null) {
            throw new LoginException("重复登陆");
        }
    }

    @Override
    public void handlePacket(PacketBody packetBody) throws Exception {
        byte[] data = packetBody.getData();
        BSONObject bsonObject;
        switch (packetBody.getId()) {
            case 0:
                /*
                 * 验证通讯协议版本
                 */
                bsonObject = BsonUtils.decode(data);
                if (!Objects.equals(GlobalVariables.PROTOCOL_VERSION, bsonObject.get("PROTOCOL_VERSION"))) {
                    client.postPacket(new PacketBody()
                            .setTaskId(this.taskId)
                            .setTaskType(TaskTypes.ERROR)
                            .setData("通讯协议版本错误".getBytes(StandardCharsets.UTF_8)));
                    throw new LoginException("通讯协议版本错误");
                }
                client.getPackageUtils().setDecryptCipher(EncryptUtils.getDecryptCipher());
                /*
                 * 发送服务器公钥
                 */
                PacketBody finalPacket = new PacketBody()
                        .setTaskId(this.taskId)
                        .setId(0)
                        .setData(SecurityKeyPairTool.getPublicKey().getEncoded());
                WorkerThreadPool.execute(() -> client.postPacket(finalPacket));
                break;
            case 1:
                /*
                 * 设置对称密钥，建立安全信道
                 */
                bsonObject = BsonUtils.decode(packetBody.getData());
                client.getPackageUtils().initCrypto(new SecretKeySpec((byte[]) bsonObject.get("KEY"), "AES"), (byte[]) bsonObject.get("ENCRYPT_IV"), (byte[]) bsonObject.get("DECRYPT_IV"));
                finalPacket = new PacketBody()
                        .setTaskId(this.taskId)
                        .setId(1);
                WorkerThreadPool.execute(() -> client.postPacket(finalPacket));
                break;
            case 2:
                PublicKey publicKey = EncryptUtils.getPublicKey("RSA", data);
                userInfo = DaoManager.getUserInfoDao().getUserInfoByUidCode(IdentityUtils.getUidCodeByPublicKeyCode(publicKey.getEncoded()));
                if (userInfo == null) {
                    client.postPacket(new PacketBody()
                            .setTaskId(this.taskId)
                            .setTaskType(TaskTypes.ERROR)
                            .setData("用户不存在".getBytes(StandardCharsets.UTF_8)));
                    throw new LoginException("用户不存在");
                }
                /*
                 * 找到用户，验证此用户的属性签名
                 */
                Identity identity = new Identity()
                        .setAttributes(userInfo.getAttributeMap())
                        .setTimeStamp(userInfo.getTimeStamp())
                        .setSignature(userInfo.getSignature())
                        .setPublicKey(publicKey);
                /*
                 * 如果验证失败，将时间戳置最小值，等待客户端发起身份同步以覆盖错误的数据
                 */
                if (!identity.checkSignature()) {
                    userInfo.setTimeStamp(Long.MIN_VALUE);
                }
                Cipher cipher = EncryptUtils.getEncryptCipher("RSA", publicKey);
                /*
                 * 生成随机验证码并用客户的公钥加密发给客户
                 */
                this.authCode = genAuthCode();
                finalPacket = new PacketBody()
                        .setTaskId(this.taskId)
                        .setId(2)
                        .setData(cipher.doFinal(authCode));
                WorkerThreadPool.execute(() -> client.postPacket(finalPacket));
                break;
            case 3:
                /*
                 * 验证用户的合法身份
                 */
                if (Arrays.equals(authCode, data)) {
                    client.setUserInfo(this.userInfo);
                } else {
                    client.postPacket(new PacketBody()
                            .setTaskId(this.taskId)
                            .setTaskType(TaskTypes.ERROR)
                            .setData("用户身份验证失败".getBytes(StandardCharsets.UTF_8)));
                    throw new LoginException("用户身份验证失败");
                }
                /*
                 * 验证成功，告诉客户登陆完毕
                 */
                finalPacket = new PacketBody()
                        .setTaskId(this.taskId)
                        .setId(3);
                WorkerThreadPool.execute(() -> client.postPacket(finalPacket));
                done();
                break;
        }
    }

    @Override
    public void done() {
        super.done();
        LOGGER.info("用户 {} 登陆成功\n", userInfo.getUidCode());
        client.removeTask(this.taskId);
    }

    private byte[] genAuthCode() {
        Random random = new Random();
        byte[] authCode = new byte[random.nextInt(5) + 10];
        random.nextBytes(authCode);
        return authCode;
    }

    @Override
    public PacketBody startPacket() {
        return null;
    }
}
