package icu.xchat.server.database;

import icu.xchat.server.database.interfaces.RoomDao;
import icu.xchat.server.database.interfaces.UserDao;
import icu.xchat.server.database.realizations.RoomDaoImpl;
import icu.xchat.server.database.realizations.UserDaoImpl;

/**
 * 数据访问对象管理器
 *
 * @author shouchen
 */
public final class DaoManager {
    private static UserDao userDao;
    private static RoomDao roomDao;

    public static synchronized void init() {
        userDao = new UserDaoImpl();
        roomDao = new RoomDaoImpl();
    }

    public static UserDao getUserDao() {
        return userDao;
    }

    public static RoomDao getRoomDao() {
        return roomDao;
    }
}
