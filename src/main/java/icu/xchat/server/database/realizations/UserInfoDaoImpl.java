package icu.xchat.server.database.realizations;

import icu.xchat.server.database.interfaces.UserInfoDao;
import icu.xchat.server.entities.UserInfo;

/**
 * 用户信息数据访问对象实现
 *
 * @author shouchen
 */
public class UserInfoDaoImpl implements UserInfoDao {
    private static volatile UserInfoDao userInfoDao;

    public static UserInfoDao getInstance() {
        if (userInfoDao == null) {
            synchronized (UserInfoDaoImpl.class) {
                if (userInfoDao == null) {
                    userInfoDao = new UserInfoDaoImpl();
                }
            }
        }
        return userInfoDao;
    }

    private UserInfoDaoImpl() {
    }


    /**
     * 根据用户标识码获取一个用户信息
     *
     * @param uidCode 用户标识码
     * @return 用户信息实体
     */
    @Override
    public UserInfo getUserInfoByUidCode(String uidCode) {
        return null;
    }
}
