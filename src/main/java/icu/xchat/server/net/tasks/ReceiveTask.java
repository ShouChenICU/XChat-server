package icu.xchat.server.net.tasks;


import icu.xchat.server.constants.TaskTypes;
import icu.xchat.server.database.DaoManager;
import icu.xchat.server.entities.MessageInfo;
import icu.xchat.server.net.DispatchCenter;
import icu.xchat.server.net.PacketBody;
import icu.xchat.server.net.WorkerThreadPool;
import icu.xchat.server.utils.BsonUtils;
import org.bson.BSONObject;

import java.nio.charset.StandardCharsets;
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
                // 验证身份是否一致
                if (!Objects.equals(messageInfo.getSender(), client.getUserInfo().getUidCode())) {
                    client.postPacket(new PacketBody()
                            .setTaskId(this.taskId)
                            .setTaskType(TaskTypes.ERROR)
                            .setData("发送者身份异常！".getBytes(StandardCharsets.UTF_8)));
                    super.done();
                    return;
                } else if (!client.getRidList().contains(messageInfo.getRid())) {
                    // 验证用户是否在该房间
                    client.postPacket(new PacketBody()
                            .setTaskId(this.taskId)
                            .setTaskType(TaskTypes.ERROR)
                            .setData("发送者不属于该房间！".getBytes(StandardCharsets.UTF_8)));
                    super.done();
                    return;
                }
                // 设置时间戳
                messageInfo.setTimeStamp(System.currentTimeMillis());
                // 写入数据库
                if (!DaoManager.getMessageDao().insertMessage(messageInfo)) {
                    client.postPacket(new PacketBody()
                            .setTaskId(this.taskId)
                            .setTaskType(TaskTypes.ERROR)
                            .setData("发生了不该发生的错误！".getBytes(StandardCharsets.UTF_8)));
                    super.done();
                }
                // 广播消息
                DispatchCenter.getInstance().broadcastMessage(messageInfo);
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
