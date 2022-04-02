package icu.xchat.server.net;

import icu.xchat.server.database.DaoManager;
import icu.xchat.server.entities.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 客户管理器
 *
 * @author shouchen
 */
public class ClientManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientManager.class);
    private static final Map<String, Client> CLIENT_MAP;
    private static final ReadWriteLock READ_WRITE_LOCK;

    static {
        READ_WRITE_LOCK = new ReentrantReadWriteLock();
        CLIENT_MAP = new HashMap<>();
    }

    /**
     * 根据用户识别码获取一个客户
     *
     * @param uidCode 用户识别码
     * @return 客户实体
     */
    public static Client getClientByUidCode(String uidCode) {
        if (uidCode == null) {
            return null;
        }
        READ_WRITE_LOCK.readLock().lock();
        try {
            return CLIENT_MAP.get(uidCode);
        } finally {
            READ_WRITE_LOCK.readLock().unlock();
        }
    }

    /**
     * 加载客户
     *
     * @param uidCode 用户识别码
     */
    public static Client loadClient(String uidCode) {
        Client client;
        READ_WRITE_LOCK.writeLock().lock();
        try {
            client = getClientByUidCode(uidCode);
            if (client != null) {
                return client;
            }
            UserInfo userInfo = DaoManager.getUserDao().getUserInfoByUidCode(uidCode);
            if (userInfo != null) {
                client = new Client() {
                    @Override
                    public UserInfo getUserInfo() {
                        return userInfo;
                    }
                };
                CLIENT_MAP.put(uidCode, client);
                return client;
            } else {
                return null;
            }
        } finally {
            READ_WRITE_LOCK.writeLock().unlock();
        }
    }
}
