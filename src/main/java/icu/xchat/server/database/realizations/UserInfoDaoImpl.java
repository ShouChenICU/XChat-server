package icu.xchat.server.database.realizations;

import icu.xchat.server.database.interfaces.UserInfoDao;
import icu.xchat.server.entities.UserInfo;

/**
 * 用户信息数据访问对象实现
 *
 * @author shouchen
 */
public class UserInfoDaoImpl implements UserInfoDao {
    /**
     * 根据用户标识码获取一个用户信息
     *
     * @param uidCode 用户标识码
     * @return 用户信息实体
     */
    @Override
    public UserInfo getUserInfoByUidCode(String uidCode) {
        return new UserInfo()
                .setUidCode(uidCode)
                .setTimeStamp(0L);
    }
}
