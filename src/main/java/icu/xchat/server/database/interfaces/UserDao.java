package icu.xchat.server.database.interfaces;

import icu.xchat.server.entities.UserInfo;

import java.util.List;

/**
 * 用户信息数据访问对象
 *
 * @author shouchen
 */
public interface UserDao {
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

    /**
     * 获取和某用户相关的所有用户识别码
     *
     * @param uidCode 指定用户的识别码
     * @return 用户识别码列表
     */
    List<String> getUidCodeListAboutUser(String uidCode);
}
