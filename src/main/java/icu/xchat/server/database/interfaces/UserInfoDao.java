package icu.xchat.server.database.interfaces;

import icu.xchat.server.entities.UserInfo;

/**
 * 用户信息数据访问对象
 *
 * @author shouchen
 */
public interface UserInfoDao {
    /**
     * 根据用户标识码获取一个用户信息
     *
     * @param uidCode 用户标识码
     * @return 用户信息实体
     */
    UserInfo getUserInfoByUidCode(String uidCode);

    /**
     * 更新用户信息
     *
     * @param userInfo 用户信息
     * @return 结果
     */
    boolean updateUserInfo(UserInfo userInfo);
}
