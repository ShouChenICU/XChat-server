package icu.xchat.server.net.tasks;

import icu.xchat.server.entities.Serialization;
import icu.xchat.server.net.PacketBody;
import icu.xchat.server.net.WorkerThreadPool;
import icu.xchat.server.utils.BsonUtils;
import org.bson.BSONObject;
import org.bson.BasicBSONObject;

/**
 * 推送任务
 *
 * @author shouchen
 */
public class PushTask extends AbstractTransmitTask {
    public PushTask(Serialization obj, int dataType, int actionType) {
        super();
        this.actionType = actionType;
        this.dataType = dataType;
        this.dataContent = obj.serialize();
    }

    /**
     * 处理一个包
     *
     * @param packetBody 包
     */
    @Override
    public void handlePacket(PacketBody packetBody) throws Exception {
        if (processedLength == dataContent.length) {
            WorkerThreadPool.execute(() -> client.postPacket(new PacketBody()
                    .setTaskId(this.taskId)
                    .setId(2)));
            done();
            return;
        }
        int len = Math.max(64000, dataContent.length - processedLength);
        byte[] buf = new byte[len];
        System.arraycopy(dataContent, processedLength, buf, 0, buf.length);
        processedLength += len;
        WorkerThreadPool.execute(() -> client.postPacket(new PacketBody()
                .setTaskId(this.taskId)
                .setId(1)
                .setData(buf)));
    }

    /**
     * 起步包
     *
     * @return 第一个发送的包
     */
    @Override
    public PacketBody startPacket() {
        BSONObject object = new BasicBSONObject();
        object.put("ACTION_TYPE", this.actionType);
        object.put("DATA_TYPE", this.dataType);
        object.put("DATA_SIZE", this.dataContent.length);
        return new PacketBody()
                .setId(0)
                .setData(BsonUtils.encode(object));
    }
}
