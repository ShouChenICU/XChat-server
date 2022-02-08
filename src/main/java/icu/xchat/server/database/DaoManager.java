package icu.xchat.server.database;

import icu.xchat.server.database.interfaces.UserInfoDao;
import icu.xchat.server.database.realizations.UserInfoDaoImpl;

/**
 * 数据访问对象管理器
 *
 * @author shouchen
 */
public final class DaoManager {
    private static UserInfoDao userInfoDao;

    public static synchronized void init(){
        userInfoDao=new UserInfoDaoImpl();
    }

    public static UserInfoDao getUserInfoDao() {
        return userInfoDao;
    }
}
