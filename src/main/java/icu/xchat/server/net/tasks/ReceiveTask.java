package icu.xchat.server.net.tasks;


import icu.xchat.server.constants.MemberPermissions;
import icu.xchat.server.constants.MemberRoles;
import icu.xchat.server.constants.TaskTypes;
import icu.xchat.server.database.DaoManager;
import icu.xchat.server.entities.ChatRoomInfo;
import icu.xchat.server.entities.MemberInfo;
import icu.xchat.server.entities.MessageInfo;
import icu.xchat.server.exceptions.TaskException;
import icu.xchat.server.net.*;
import icu.xchat.server.utils.BsonUtils;
import org.bson.BSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

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
                MessageInfo messageInfo = new MessageInfo(dataContent);
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
                    return;
                }
                // 广播消息
                ChatRoomManager.broadcastMessage(messageInfo);
            } else if (Objects.equals(dataType, TYPE_ROOM_INFO)) {
                // 新建房间
                if (Objects.equals(actionType, ACTION_CREATE)) {
                    // TODO: 2022/3/15 限制房间最大数量
                    ChatRoomInfo roomInfo = new ChatRoomInfo(dataContent);
                    Map<String, MemberInfo> memberInfoMap = new HashMap<>();
                    // 设置房主
                    memberInfoMap.put(client.getUserInfo().getUidCode(), new MemberInfo()
                            .setUidCode(client.getUserInfo().getUidCode())
                            .setRole(MemberRoles.ROLE_OWNER)
                            .setPermission(MemberPermissions.ALL));
                    roomInfo.setMemberInfoMap(memberInfoMap);
                    // 写入数据库，自动更新主键和时间戳
                    if (DaoManager.getRoomDao().insertRoomInfo(roomInfo)) {
                        // 将新房间加入到调度器
                        ChatRoom chatRoom = new ChatRoom(roomInfo);
                        chatRoom.putClint(client);
                        ChatRoomManager.putChatRoom(chatRoom);
                        CountDownLatch latch = new CountDownLatch(1);
                        try {
                            // 推送这个新房间
                            client.addTask(new PushTask(roomInfo,
                                    PushTask.TYPE_ROOM_INFO,
                                    PushTask.ACTION_CREATE,
                                    new ProgressAdapter() {
                                        @Override
                                        public void completeProgress() {
                                            latch.countDown();
                                            super.completeProgress();
                                        }
                                    }));
                            // 等待推送成功
                            if (!latch.await(30, TimeUnit.SECONDS)) {
                                client.postPacket(new PacketBody()
                                        .setTaskId(this.taskId)
                                        .setTaskType(TaskTypes.ERROR)
                                        .setData("超时！".getBytes(StandardCharsets.UTF_8)));
                                super.done();
                                return;
                            }
                        } catch (TaskException | InterruptedException e) {
                            client.postPacket(new PacketBody()
                                    .setTaskId(this.taskId)
                                    .setTaskType(TaskTypes.ERROR)
                                    .setData("这是不可能发生的错误！".getBytes(StandardCharsets.UTF_8)));
                            super.done();
                            return;
                        }
                    } else {
                        client.postPacket(new PacketBody()
                                .setTaskId(this.taskId)
                                .setTaskType(TaskTypes.ERROR)
                                .setData("发生了不该发生的错误！".getBytes(StandardCharsets.UTF_8)));
                        super.done();
                        return;
                    }
                }
            }
            client.postPacket(new PacketBody()
                    .setTaskId(this.taskId)
                    .setId(2));
            super.done();
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
