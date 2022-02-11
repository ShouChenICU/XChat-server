package icu.xchat.server.net.tasks;

import icu.xchat.server.net.Client;
import icu.xchat.server.net.PacketBody;
import icu.xchat.server.net.WorkerThreadPool;
import icu.xchat.server.utils.BsonUtils;
import icu.xchat.server.utils.TaskTypes;
import org.bson.BSONObject;
import org.bson.BasicBSONObject;

import java.security.PublicKey;
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

    public IdentitySyncTask(Client client) {
        this.client = client;
    }

    /**
     * 处理一个包
     *
     * @param packetBody 包
     */
    @Override
    public void handlePacket(PacketBody packetBody) {
        if (Objects.equals(packetBody.getTaskType(), TaskTypes.ERROR)) {
            terminate((String) BsonUtils.decode(packetBody.getData()).get("ERR_MSG"));
            return;
        }
        if (packetBody.getId() == 0) {
            BSONObject object = BsonUtils.decode(packetBody.getData());
            byte[] pubKey = (byte[]) object.get("PUBLIC_KEY");
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

    @Override
    public void done() {
        super.done();
        if (isDownload) {

        }
        client.removeTask(this.taskId);
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
