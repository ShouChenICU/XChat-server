package icu.xchat.server.net;

import icu.xchat.server.entity.ChatRoom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 调度中心
 *
 * @author shouchen
 */
class DispatchCenter {
    private static final Logger LOGGER = LoggerFactory.getLogger(DispatchCenter.class);
    private static volatile DispatchCenter dispatchCenter;
    private Map<String, Client> onlineClientMap;
    private Map<Integer, ChatRoom> chatRoomMap;

    /**
     * 获取单实例
     *
     * @return 单实例
     */
    public static DispatchCenter getInstance() {
        if (dispatchCenter == null) {
            synchronized (DispatchCenter.class) {
                if (dispatchCenter == null) {
                    dispatchCenter = new DispatchCenter();
                }
            }
        }
        return dispatchCenter;
    }

    private DispatchCenter() {
        onlineClientMap = new HashMap<>();
        chatRoomMap = new HashMap<>();
    }

    public void stop() {

    }
}
