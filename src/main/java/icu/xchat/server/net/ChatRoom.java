package icu.xchat.server.net;

import icu.xchat.server.entities.ChatRoomInfo;
import icu.xchat.server.entities.MessageInfo;
import icu.xchat.server.net.tasks.PushTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 聊天室
 *
 * @author shouchen
 */
public class ChatRoom {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChatRoom.class);
    private final ConcurrentHashMap<String, Client> onlineClintMap;
    private ChatRoomInfo roomInfo;

    public ChatRoom(ChatRoomInfo roomInfo) {
        this.roomInfo = roomInfo;
        this.onlineClintMap = new ConcurrentHashMap<>();
    }

    public int getRid() {
        return roomInfo.getRid();
    }

    public ChatRoom updateRoomInfo(ChatRoomInfo roomInfo) {
        this.roomInfo = roomInfo;
        return this;
    }

    public ChatRoom putClint(Client client) {
        this.onlineClintMap.put(client.getUserInfo().getUidCode(), client);
        return this;
    }

    public ChatRoom removeClient(String uidCode) {
        this.onlineClintMap.remove(uidCode);
        return this;
    }

    /**
     * 广播消息
     *
     * @param messageInfo 消息
     */
    public void broadcastMessage(MessageInfo messageInfo) {
        for (Map.Entry<String, Client> entry : onlineClintMap.entrySet()) {
            WorkerThreadPool.execute(() -> {
                try {
                    entry.getValue().addTask(new PushTask(messageInfo, PushTask.TYPE_MSG_INFO, PushTask.ACTION_CREATE));
                } catch (Exception e) {
                    onlineClintMap.remove(entry.getKey());
                    LOGGER.warn("消息广播异常：{}", entry.getValue().getUserInfo(), e);
                }
            });
        }
    }
}
