package icu.xchat.server.net.tasks;


import icu.xchat.server.entities.MessageInfo;
import icu.xchat.server.net.PacketBody;
import icu.xchat.server.net.WorkerThreadPool;
import icu.xchat.server.utils.BsonUtils;
import org.bson.BSONObject;

import java.util.Objects;

/**
 * 数据接收任务
 *
 * @author shouchen
 */
public class ReceiveTask extends AbstractTransmitTask {
    /**
     * 处理一个包
     *
     * @param packetBody 包
     */
    @Override
    public void handlePacket(PacketBody packetBody) throws Exception {
        if (packetBody.getId() == 0) {
            BSONObject object = BsonUtils.decode(packetBody.getData());
            this.actionType = (int) object.get("ACTION_TYPE");
            this.dataType = (int) object.get("DATA_TYPE");
            this.dataContent = new byte[(int) object.get("DATA_SIZE")];
        } else if (packetBody.getId() == 1) {
            byte[] buf = packetBody.getData();
            System.arraycopy(buf, 0, dataContent, processedLength, buf.length);
            processedLength += buf.length;
        }
        if (processedLength == dataContent.length) {
            done();
        } else {
            WorkerThreadPool.execute(() -> client.postPacket(new PacketBody()
                    .setTaskId(this.taskId)
                    .setId(1)));
        }
    }

    @Override
    public void done() {
        WorkerThreadPool.execute(() -> {
            if (Objects.equals(dataType, TYPE_MSG_INFO)) {
                MessageInfo messageInfo = new MessageInfo();
                messageInfo.deserialize(dataContent);

                // TODO: 2022/3/14
            }
            client.postPacket(new PacketBody()
                    .setTaskId(this.taskId)
                    .setId(2));
        });
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
