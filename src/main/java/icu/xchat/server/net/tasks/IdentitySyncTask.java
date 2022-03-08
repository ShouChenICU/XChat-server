package icu.xchat.server.net.tasks;

import icu.xchat.server.constants.TaskTypes;
import icu.xchat.server.database.DaoManager;
import icu.xchat.server.entities.Identity;
import icu.xchat.server.net.PacketBody;
import icu.xchat.server.net.WorkerThreadPool;
import icu.xchat.server.utils.BsonUtils;
import icu.xchat.server.utils.IdentityUtils;
import org.bson.BSONObject;
import org.bson.BasicBSONObject;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Map;
import java.util.Objects;

/**
 * 身份同步任务
 *
 * @author shouchen
 */
public class IdentitySyncTask extends AbstractTask {
    private PublicKey publicKey;
    private boolean isDownload;
    private byte[] identityData;
    private int processedSize;

    public IdentitySyncTask() {
        super();
    }

    /**
     * 处理一个包
     *
     * @param packetBody 包
     */
    @Override
    public void handlePacket(PacketBody packetBody) throws Exception {
        if (Objects.equals(packetBody.getTaskType(), TaskTypes.ERROR)) {
            terminate(new String(packetBody.getData(), StandardCharsets.UTF_8));
            return;
        }
        if (packetBody.getId() == 0) {
            BSONObject object = BsonUtils.decode(packetBody.getData());
            byte[] pubKey = (byte[]) object.get("PUBLIC_KEY");
            if (!Objects.equals(client.getUserInfo().getUidCode(), IdentityUtils.getUidCodeByPublicKeyCode(pubKey))) {
                throw new Exception("身份不匹配！");
            }
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            this.publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(pubKey));
            long timeStamp = (Long) object.get("TIMESTAMP");
            /*
             * 根据用户发过来的时间戳决定同步方向
             */
            if (client.getUserInfo().getTimeStamp() < timeStamp) {
                /*
                 * 用户 -> 服务器
                 */
                isDownload = true;
                WorkerThreadPool.execute(() ->
                        client.postPacket(new PacketBody()
                                .setTaskId(this.taskId)
                                .setId(0)
                                .setData(new byte[]{1})));
            } else if (client.getUserInfo().getTimeStamp() > timeStamp) {
                /*
                 * 服务器 -> 用户
                 */
                isDownload = false;
                client.postPacket(new PacketBody()
                        .setTaskId(this.taskId)
                        .setId(0)
                        .setData(new byte[]{0}));
                object = new BasicBSONObject();
                object.put("ATTRIBUTES", client.getUserInfo().getAttributeMap());
                object.put("TIMESTAMP", client.getUserInfo().getTimeStamp());
                object.put("SIGNATURE", client.getUserInfo().getSignature());
                this.identityData = BsonUtils.encode(object);
                object = new BasicBSONObject();
                object.put("SIZE", this.identityData.length);
                client.postPacket(new PacketBody()
                        .setTaskId(this.taskId)
                        .setId(1)
                        .setData(BsonUtils.encode(object)));
                upload();
            } else {
                /*
                 * 不同步
                 */
                WorkerThreadPool.execute(() ->
                        client.postPacket(new PacketBody()
                                .setTaskId(this.taskId)
                                .setId(0)
                                .setData(new byte[]{2})));
                done();
            }
        } else if (packetBody.getId() == 1) {
            int size = (int) BsonUtils.decode(packetBody.getData()).get("SIZE");
            if (size > 16 * 1024 * 1024) {
                WorkerThreadPool.execute(() -> client.postPacket(new PacketBody()
                        .setTaskId(this.taskId)
                        .setTaskType(TaskTypes.ERROR)
                        .setData("数据大小超限！".getBytes(StandardCharsets.UTF_8))));
                this.terminate("数据大小超限！");
                return;
            }
            this.identityData = new byte[size];
            this.processedSize = 0;
        } else if (isDownload) {
            download(packetBody);
        } else {
            upload();
        }
    }

    private void download(PacketBody packetBody) {
        byte[] buf = packetBody.getData();
        System.arraycopy(buf, 0, this.identityData, this.processedSize, buf.length);
        processedSize += buf.length;
        if (processedSize == identityData.length) {
            done();
        } else {
            WorkerThreadPool.execute(() -> client.postPacket(new PacketBody()
                    .setTaskId(this.taskId)));
        }
    }

    private void upload() {
        byte[] buf;
        int pendingSize = identityData.length - processedSize;
        if (pendingSize > 64000) {
            buf = new byte[64000];
            System.arraycopy(identityData, processedSize, buf, 0, buf.length);
            processedSize += buf.length;
            WorkerThreadPool.execute(() -> client.postPacket(new PacketBody()
                    .setTaskId(this.taskId)
                    .setId(2)
                    .setData(buf)));
        } else {
            buf = new byte[pendingSize];
            System.arraycopy(identityData, processedSize, buf, 0, buf.length);
            processedSize += buf.length;
            WorkerThreadPool.execute(() -> client.postPacket(new PacketBody()
                    .setTaskId(this.taskId)
                    .setId(2)
                    .setData(buf)));
            done();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void done() {
        if (isDownload) {
            BSONObject object = BsonUtils.decode(identityData);
            Identity identity = new Identity()
                    .setAttributes((Map<String, String>) object.get("ATTRIBUTES"))
                    .setTimeStamp((Long) object.get("TIMESTAMP"))
                    .setSignature((String) object.get("SIGNATURE"))
                    .setPublicKey(publicKey);
            if (identity.checkSignature()) {
                client.getUserInfo().setAttributeMap(identity.getAttributes());
                client.getUserInfo().setTimeStamp(identity.getTimeStamp());
                client.getUserInfo().setSignature(identity.getSignature());
                DaoManager.getUserDao().updateUserInfo(client.getUserInfo());
            } else {
                progressCallBack.terminate("身份验证失败，拒绝同步！");
            }
        }
        super.done();
    }

    /**
     * 起步包
     *
     * @return 第一个发送的包
     */
    @Override
    public PacketBody startPacket() {
        return null;
    }
}
