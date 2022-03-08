package icu.xchat.server.database;

import icu.xchat.server.database.interfaces.UserDao;
import icu.xchat.server.database.realizations.UserDaoImpl;

/**
 * 数据访问对象管理器
 *
 * @author shouchen
 */
public final class DaoManager {
    private static UserDao userDao;

    public static synchronized void init(){
        userDao =new UserDaoImpl();
    }

    public static UserDao getUserDao() {
        return userDao;
    }
}
