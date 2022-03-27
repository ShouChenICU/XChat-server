package icu.xchat.server.net.tasks;

import icu.xchat.server.constants.TaskTypes;
import icu.xchat.server.database.DaoManager;
import icu.xchat.server.entities.MessageInfo;
import icu.xchat.server.exceptions.TaskException;
import icu.xchat.server.net.PacketBody;
import icu.xchat.server.net.WorkerThreadPool;
import icu.xchat.server.utils.BsonUtils;
import org.bson.BSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 房间同步任务
 *
 * @author shouchen
 */
public class MessageSyncTask extends AbstractTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageSyncTask.class);
    private List<Integer> messageIdList;

    public MessageSyncTask() {
        super();
    }

    /**
     * 处理一个包
     *
     * @param packetBody 包
     */
    @Override
    public void handlePacket(PacketBody packetBody) throws Exception {
        WorkerThreadPool.execute(() -> {
            BSONObject object = BsonUtils.decode(packetBody.getData());
            this.messageIdList = DaoManager.getMessageDao().getMessageIdListByLatestTimeAndCount(
                    (int) object.get("RID"),
                    (long) object.get("TIME"),
                    (int) object.get("COUNT")
            );
            // 如果列表为空，直接结束
            if (messageIdList.isEmpty()) {
                client.postPacket(new PacketBody()
                        .setTaskId(this.taskId));
                done();
                return;
            }
            CountDownLatch latch = new CountDownLatch(messageIdList.size());
            for (int id : messageIdList) {
                MessageInfo messageInfo = DaoManager.getMessageDao().getMessageById(id);
                try {
                    client.addTask(new PushTask(messageInfo,
                            PushTask.TYPE_MSG_INFO,
                            PushTask.ACTION_UPDATE,
                            new ProgressAdapter() {
                                @Override
                                public void completeProgress() {
                                    latch.countDown();
                                }
                            }));
                } catch (TaskException e) {
                    LOGGER.warn("不可能发生的错误！");
                }
            }
            try {
                if (latch.await(1, TimeUnit.MINUTES)) {
                    client.postPacket(new PacketBody()
                            .setTaskId(this.taskId)
                            .setId(0));
                    done();
                    return;
                }
            } catch (InterruptedException e) {
                LOGGER.warn("消息同步超时！", e);
            }
            client.postPacket(new PacketBody()
                    .setTaskId(this.taskId)
                    .setTaskType(TaskTypes.ERROR)
                    .setData("消息同步超时！".getBytes(StandardCharsets.UTF_8)));
            terminate("消息同步超时！");
        });
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
