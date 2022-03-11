package icu.xchat.server.net.tasks;

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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 房间信息同步任务
 *
 * @author shouchen
 */
public class RoomSyncTask extends AbstractTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(RoomSyncTask.class);
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
            object.put("RID_LIST", ridList);
            WorkerThreadPool.execute(() -> client.postPacket(packetBody.setData(BsonUtils.encode(object))));
        } else if (packetBody.getId() == 1) {
            WorkerThreadPool.execute(() -> {
                BSONObject object;
                object = BsonUtils.decode(packetBody.getData());
                int rid = (Integer) object.get("RID");
                // 防止客户端尝试获取未加入的房间信息
                if (!ridList.contains(rid)) {
                    this.terminate("用户不属于该房间！");
                    return;
                }
                ChatRoomInfo roomInfo = DaoManager.getRoomDao().getRoomInfoByRid(rid);
                if (roomInfo != null) {
                    byte[] digest = null;
                    try {
                        digest = MessageDigest.getInstance("SHA-256").digest(roomInfo.serialize());
                    } catch (NoSuchAlgorithmException e) {
                        LOGGER.error("", e);
                        terminate(e.getMessage());
                        DispatchCenter.getInstance().closeClient(client);
                    }
                    byte[] hash = (byte[]) object.get("HASH");
                    if (!Arrays.equals(digest, hash)) {
                        try {
                            if (hash.length == 256) {
                                client.addTask(new PushTask(roomInfo, PushTask.TYPE_ROOM_INFO, PushTask.ACTION_UPDATE));
                            } else {
                                client.addTask(new PushTask(roomInfo, PushTask.TYPE_ROOM_INFO, PushTask.ACTION_CREATE));
                            }
                        } catch (TaskException e) {
                            LOGGER.error("", e);
                            terminate(e.getMessage());
                        }
                    }
                }
                client.postPacket(new PacketBody()
                        .setTaskId(this.taskId)
                        .setId(1));
            });
        } else {
            done();
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
