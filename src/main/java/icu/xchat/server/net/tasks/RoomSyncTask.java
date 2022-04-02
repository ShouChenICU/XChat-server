package icu.xchat.server.net.tasks;

import icu.xchat.server.constants.TaskTypes;
import icu.xchat.server.database.DaoManager;
import icu.xchat.server.entities.ChatRoomInfo;
import icu.xchat.server.exceptions.TaskException;
import icu.xchat.server.net.DispatchCenter;
import icu.xchat.server.net.PacketBody;
import icu.xchat.server.net.WorkerThreadPool;
import icu.xchat.server.utils.BsonUtils;
import org.bson.BSONObject;
import org.bson.BasicBSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 房间信息同步任务
 *
 * @author shouchen
 */
public class RoomSyncTask extends AbstractTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(RoomSyncTask.class);
    private CountDownLatch latch;
    private List<Integer> ridList;

    public RoomSyncTask() {
        super();
        this.ridList = new ArrayList<>();
    }

    /**
     * 处理一个包
     *
     * @param packetBody 包
     */
    @Override
    public void handlePacket(PacketBody packetBody) throws Exception {
        if (packetBody.getId() == 0) {
            BSONObject object = new BasicBSONObject();
            ridList = DaoManager.getRoomDao().getRoomIdListByUidCode(client.getUserInfo().getUidCode());
            // 如果列表为空，直接结束
            if (ridList.isEmpty()) {
                WorkerThreadPool.execute(() -> client.postPacket(new PacketBody()
                        .setTaskId(this.taskId)
                        .setId(2)));
                done();
                return;
            }
            latch = new CountDownLatch(ridList.size());
            object.put("RID_LIST", ridList);
            WorkerThreadPool.execute(() -> client.postPacket(packetBody.setData(BsonUtils.encode(object))));
        } else if (packetBody.getId() == 1) {
            WorkerThreadPool.execute(() -> {
                BSONObject object;
                object = BsonUtils.decode(packetBody.getData());
                int rid = (Integer) object.get("RID");
                // 防止客户端尝试获取未加入的房间信息
                if (!ridList.contains(rid)) {
                    client.postPacket(new PacketBody()
                            .setTaskId(this.taskId)
                            .setTaskType(TaskTypes.ERROR)
                            .setData("用户不属于该房间！".getBytes(StandardCharsets.UTF_8)));
                    this.terminate("用户不属于该房间！");
                    return;
                }
                ChatRoomInfo roomInfo = DaoManager.getRoomDao().getRoomInfoByRid(rid);
                ridList.remove((Integer) rid);
                if (roomInfo != null) {
                    byte[] digest = null;
                    try {
                        digest = MessageDigest.getInstance("SHA-256").digest(roomInfo.serialize());
                    } catch (NoSuchAlgorithmException e) {
                        LOGGER.error("", e);
                        terminate(e.getMessage());
//                        DispatchCenter.closeClient(client);
                        // TODO: 2022/4/2
                    }
                    byte[] hash = (byte[]) object.get("HASH");
                    if (!Arrays.equals(digest, hash)) {
                        try {
                            client.addTask(new PushTask(roomInfo, PushTask.TYPE_ROOM_INFO, PushTask.ACTION_UPDATE, new ProgressAdapter() {
                                @Override
                                public void completeProgress() {
                                    super.completeProgress();
                                    latch.countDown();
                                }
                            }));
                        } catch (TaskException e) {
                            LOGGER.error("", e);
                            terminate(e.getMessage());
                        }
                    }
                }
                if (ridList.isEmpty()) {
                    try {
                        if (latch.await(30, TimeUnit.SECONDS)) {
                            client.postPacket(new PacketBody()
                                    .setTaskId(this.taskId)
                                    .setId(2));
                            done();
                        } else {
                            terminate("超时，同步房间失败！");
                            client.postPacket(new PacketBody()
                                    .setTaskId(this.taskId)
                                    .setTaskType(TaskTypes.ERROR)
                                    .setData("超时，同步房间失败！".getBytes(StandardCharsets.UTF_8)));
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    client.postPacket(new PacketBody()
                            .setTaskId(this.taskId)
                            .setId(1));
                }
            });
        }
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
