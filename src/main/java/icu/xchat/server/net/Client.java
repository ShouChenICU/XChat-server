package icu.xchat.server.net;

import icu.xchat.server.entities.UserInfo;
import icu.xchat.server.net.tasks.Task;

import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;

/**
 * 网络客户端实体
 *
 * @author shouchen
 */
public class Client {
    private final SocketChannel channel;
    private final Map<Integer, Task> taskMap;
    private UserInfo userInfo;

    public Client(SocketChannel channel) {
        this.channel = channel;
        taskMap = new HashMap<>();
    }

    public boolean isLogin() {
        return userInfo != null;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public Client setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
        return this;
    }
}