package icu.xchat.server.net;

import icu.xchat.server.entity.User;

import java.util.Map;

/**
 * 网络客户端实体
 *
 * @author shouchen
 */
public class Client {
    private User user;
    private Map<Integer, Task> taskMap;

    public Client() {

    }
}