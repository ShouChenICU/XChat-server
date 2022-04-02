package icu.xchat.server.net;

import icu.xchat.server.database.DaoManager;
import icu.xchat.server.entities.MessageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 聊天室管理器
 *
 * @author shouchen
 */
public class ChatRoomManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChatRoomManager.class);
    private static final Map<Integer, ChatRoom> ROOM_MAP;
    private static final ReadWriteLock READ_WRITE_LOCK;

    static {
        READ_WRITE_LOCK = new ReentrantReadWriteLock();
        ROOM_MAP = new HashMap<>();
    }

    /**
     * 加载聊天室
     */
    public static void loadChatRoom() {
        List<Integer> ridList = DaoManager.getRoomDao().getRoomIdList();
        READ_WRITE_LOCK.writeLock().lock();
        try {
            for (int rid : ridList) {
                ROOM_MAP.put(rid, new ChatRoom(DaoManager.getRoomDao().getRoomInfoByRid(rid)));
            }
        } finally {
            READ_WRITE_LOCK.writeLock().unlock();
        }
    }

    /**
     * 添加聊天室
     */
    public static void putChatRoom(ChatRoom chatRoom) {
        READ_WRITE_LOCK.writeLock().lock();
        try {
            ROOM_MAP.put(chatRoom.getRid(), chatRoom);
        } finally {
            READ_WRITE_LOCK.writeLock().unlock();
        }
    }

    /**
     * 广播消息
     *
     * @param messageInfo 消息信息
     */
    public static void broadcastMessage(MessageInfo messageInfo) {
        READ_WRITE_LOCK.readLock().lock();
        try {
            ChatRoom chatRoom = ROOM_MAP.get(messageInfo.getRid());
            if (chatRoom != null) {
                chatRoom.broadcastMessage(messageInfo);
            } else {
                LOGGER.warn("异常消息：{}", messageInfo);
            }
        } finally {
            READ_WRITE_LOCK.readLock().unlock();
        }
    }

    /**
     * 更新房间的在线用户列表
     *
     * @param client 客户实体
     */
    public static void updateClient(Client client) {
        READ_WRITE_LOCK.readLock().lock();
        try {
            for (int rid : client.getRidList()) {
                ChatRoom chatRoom = ROOM_MAP.get(rid);
                if (chatRoom != null) {
                    chatRoom.putClint(client);
                }
            }
        } finally {
            READ_WRITE_LOCK.readLock().unlock();
        }
    }

    /**
     * 移除用户
     */
    public static void removeClient(Client client) {
        READ_WRITE_LOCK.writeLock().lock();
        try {
            for (int rid : client.getRidList()) {
                ROOM_MAP.get(rid).removeClient(client.getUserInfo().getUidCode());
            }
        } finally {
            READ_WRITE_LOCK.writeLock().unlock();
        }
    }
}
