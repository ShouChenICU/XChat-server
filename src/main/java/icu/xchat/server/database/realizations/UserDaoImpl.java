package icu.xchat.server.database.realizations;

import icu.xchat.server.database.DataBaseManager;
import icu.xchat.server.database.interfaces.UserDao;
import icu.xchat.server.entities.UserInfo;
import icu.xchat.server.utils.CacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 用户信息数据访问对象实现
 *
 * @author shouchen
 */
public class UserDaoImpl implements UserDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserDaoImpl.class);
    private static final CacheManager<String, UserInfo> USER_INFO_CACHES = new CacheManager<>(3 * 60 * 1000);

    /**
     * 根据用户标识码获取一个用户信息
     *
     * @param uidCode 用户标识码
     * @return 用户信息实体
     */
    @Override
    public UserInfo getUserInfoByUidCode(String uidCode) {
        UserInfo userInfo = USER_INFO_CACHES.getCache(uidCode);
        if (userInfo != null) {
            return userInfo;
        }
        try (Connection connection = DataBaseManager.getConnection()) {
            connection.setAutoCommit(false);
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT u.uid,u.uid_code,u.status,u.signature,u.time_stamp FROM t_users AS u WHERE u.uid_code = ?");
            preparedStatement.setString(1, uidCode);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                userInfo = new UserInfo();
                userInfo.setUid(resultSet.getInt("uid"))
                        .setUidCode(resultSet.getString("uid_code"))
                        .setStatus(resultSet.getInt("status"))
                        .setSignature(resultSet.getString("signature"))
                        .setTimeStamp(resultSet.getLong("time_stamp"));
                preparedStatement = connection.prepareStatement("SELECT a.\"key\",a.value FROM t_user_attributes AS a WHERE a.uid = ?");
                preparedStatement.setInt(1, userInfo.getUid());
                resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    userInfo.setAttribute(resultSet.getString("key"), resultSet.getString("value"));
                }
            }
        } catch (SQLException e) {
            LOGGER.error("", e);
            userInfo = null;
        }
        if (userInfo != null) {
            USER_INFO_CACHES.putCache(userInfo.getUidCode(), userInfo);
        }
        return userInfo;
    }

    /**
     * 更新用户信息
     *
     * @param userInfo 用户信息
     * @return 结果
     */
    @Override
    public boolean updateUserInfo(UserInfo userInfo) {
        try (Connection connection = DataBaseManager.getConnection()) {
            // 开始事务
            connection.setAutoCommit(false);
            try {
                // 更新用户信息
                PreparedStatement preparedStatement = connection.prepareStatement("UPDATE t_users SET status = ?,signature = ?,time_stamp = ? WHERE uid_code = ?");
                preparedStatement.setInt(1, userInfo.getStatus());
                preparedStatement.setString(2, userInfo.getSignature());
                preparedStatement.setLong(3, userInfo.getTimeStamp());
                preparedStatement.setString(4, userInfo.getUidCode());
                preparedStatement.executeUpdate();
                // 获取用户属性集的key列表
                preparedStatement = connection.prepareStatement("SELECT \"key\" FROM t_user_attributes WHERE uid = ?");
                preparedStatement.setInt(1, userInfo.getUid());
                ResultSet resultSet = preparedStatement.executeQuery();
                List<String> keys = new ArrayList<>();
                while (resultSet.next()) {
                    keys.add(resultSet.getString("key"));
                }
                preparedStatement = connection.prepareStatement("UPDATE t_user_attributes SET value = ?");
                PreparedStatement preparedStatement1 = connection.prepareStatement("INSERT INTO t_user_attributes (uid,\"key\",value) VALUES(?,?,?)");
                preparedStatement1.setInt(1, userInfo.getUid());
                // 遍历列表，更新或者插入属性
                for (Map.Entry<String, String> entry : userInfo.getAttributeMap().entrySet()) {
                    if (keys.contains(entry.getKey())) {
                        preparedStatement.setString(1, entry.getValue());
                        preparedStatement.executeUpdate();
                        keys.remove(entry.getKey());
                    } else {
                        preparedStatement1.setString(2, entry.getKey());
                        preparedStatement1.setString(3, entry.getValue());
                        preparedStatement1.executeUpdate();

                    }
                }
                // 删除多余的属性
                preparedStatement = connection.prepareStatement("DELETE FROM t_user_attributes WHERE uid = ? AND \"key\" = ?");
                preparedStatement.setInt(1, userInfo.getUid());
                for (String key : keys) {
                    preparedStatement.setString(2, key);
                    preparedStatement.executeUpdate();
                }
                // 提交事务
                connection.commit();
            } catch (Exception e) {
                LOGGER.error("", e);
                // 回滚事务
                connection.rollback();
                return false;
            }
        } catch (SQLException e) {
            LOGGER.error("", e);
            return false;
        }
        USER_INFO_CACHES.removeCache(userInfo.getUidCode());
        return true;
    }
}
