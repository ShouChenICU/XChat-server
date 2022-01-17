package icu.xchat.server.net;

import icu.xchat.server.database.entity.UserEntity;

import java.util.HashMap;
import java.util.Map;

/**
 * 网络客户端实体
 *
 * @author shouchen
 */
class Client {
    private UserEntity userEntity;
    private final Map<Integer, Task> taskMap;

    public Client() {
        taskMap = new HashMap<>();
    }


}
