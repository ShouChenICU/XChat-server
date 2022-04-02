package icu.xchat.server.net;

import icu.xchat.server.entities.ChatRoomInfo;
import icu.xchat.server.entities.MessageInfo;
import icu.xchat.server.net.tasks.PushTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 聊天室
 *
 * @author shouchen
 */
public class ChatRoom {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChatRoom.class);
    private final ConcurrentHashMap<String, Client> clintMap;
    private ChatRoomInfo roomInfo;

    public ChatRoom(ChatRoomInfo roomInfo) {
        this.roomInfo = roomInfo;
        this.clintMap = new ConcurrentHashMap<>();
    }

    public int getRid() {
        return roomInfo.getRid();
    }

    public ChatRoom updateRoomInfo(ChatRoomInfo roomInfo) {
        this.roomInfo = roomInfo;
        return this;
    }

    public ChatRoom putClint(Client client) {
        this.clintMap.put(client.getUserInfo().getUidCode(), client);
        return this;
    }

    public ChatRoom removeClient(String uidCode) {
        this.clintMap.remove(uidCode);
        return this;
    }

    /**
     * 广播消息
     *
     * @param messageInfo 消息
     */
    public void broadcastMessage(MessageInfo messageInfo) {
        for (Client client : clintMap.values()) {
            if (client.isConnect()) {
                WorkerThreadPool.execute(() -> {
                    try {
                        client.addTask(new PushTask(messageInfo, PushTask.TYPE_MSG_INFO, PushTask.ACTION_CREATE));
                    } catch (Exception e) {
                        LOGGER.warn("消息广播异常：{}", client.getUserInfo(), e);
                    }
                });
            }
        }
    }
}
