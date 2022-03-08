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
import java.util.Arrays;

/**
 * 房间信息同步任务
 *
 * @author shouchen
 */
public class RoomSyncTask extends AbstractTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(RoomSyncTask.class);

    public RoomSyncTask() {
        super();
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
            object.put("RID_LIST", DaoManager.getRoomDao().getRoomIdListByUidCode(client.getUserInfo().getUidCode()));
            WorkerThreadPool.execute(() -> client.postPacket(packetBody.setData(BsonUtils.encode(object))));
        } else if (packetBody.getId() == 1) {
            WorkerThreadPool.execute(() -> {
                BSONObject object;
                object = BsonUtils.decode(packetBody.getData());
                ChatRoomInfo roomInfo = DaoManager.getRoomDao().getRoomInfoByRid((Integer) object.get("RID"));
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
