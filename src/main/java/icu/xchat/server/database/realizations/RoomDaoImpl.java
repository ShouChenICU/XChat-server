package icu.xchat.server.database.realizations;

import icu.xchat.server.database.DataBaseManager;
import icu.xchat.server.database.interfaces.RoomDao;
import icu.xchat.server.entities.ChatRoomInfo;
import icu.xchat.server.entities.MemberInfo;
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
 * 聊天室数据访问对象实现
 *
 * @author shouchen
 */
public class RoomDaoImpl implements RoomDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(RoomDaoImpl.class);
    private static final CacheManager<Integer, ChatRoomInfo> ROOM_INFO_CACHES = new CacheManager<>(3 * 60 * 1000);

    /**
     * 获取一个房间信息
     *
     * @param rid 房间id
     * @return 房间信息实体
     */
    @Override
    public ChatRoomInfo getRoomInfoByRid(int rid) {
        ChatRoomInfo roomInfo = ROOM_INFO_CACHES.getCache(rid);
        if (roomInfo != null) {
            return roomInfo;
        }
        try (Connection connection = DataBaseManager.getConnection()) {
            connection.setAutoCommit(false);
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT rid,status,creation_time FROM t_rooms WHERE rid = ?");
            preparedStatement.setInt(1, rid);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                roomInfo = new ChatRoomInfo();
                roomInfo.setRid(resultSet.getInt("rid"));
                roomInfo.setCreation_time(resultSet.getLong("creation_time"));
                preparedStatement = connection.prepareStatement("SELECT \"key\",\"value\" FROM t_room_attributes WHERE rid = ?");
                preparedStatement.setInt(1, rid);
                resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    roomInfo.setAttribute(resultSet.getString("key"), resultSet.getString("value"));
                }
                preparedStatement = connection.prepareStatement("SELECT \"uid_code\",\"role\",\"permission\",\"join_time\" FROM r_members WHERE rid = ?");
                preparedStatement.setInt(1, rid);
                resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    roomInfo.addMember(new MemberInfo()
                            .setRid(rid)
                            .setUidCode(resultSet.getString("uid_code"))
                            .setRole(resultSet.getString("role"))
                            .setPermission(resultSet.getInt("permission"))
                            .setJoinTime(resultSet.getLong("join_time")));
                }
            }
        } catch (SQLException e) {
            LOGGER.error("", e);
            return null;
        }
        if (roomInfo != null) {
            ROOM_INFO_CACHES.putCache(rid, roomInfo);
        }
        return roomInfo;
    }

    /**
     * 插入一个房间
     *
     * @param roomInfo 房间信息
     * @return 结果
     */
    @Override
    public boolean insertRoomInfo(ChatRoomInfo roomInfo) {
        try (Connection connection = DataBaseManager.getConnection()) {
            connection.setAutoCommit(false);
            try {
                // 插入房间信息
                PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO t_rooms (status,creation_time) VALUES(?,?)");
                preparedStatement.setInt(1, 0);
                long t = System.currentTimeMillis();
                preparedStatement.setLong(2, t);
                if (preparedStatement.executeUpdate() < 1) {
                    throw new Exception("房间信息插入失败！");
                }
                // 获取生成的主键id
                ResultSet resultSet = preparedStatement.getGeneratedKeys();
                if (resultSet.next()) {
                    roomInfo.setRid(resultSet.getInt("rid"));
                    roomInfo.setCreation_time(t);
                }
                // 插入房间属性集
                preparedStatement = connection.prepareStatement("INSERT INTO t_room_attributes (rid,\"key\",value) VALUES(?,?,?)");
                preparedStatement.setInt(1, roomInfo.getRid());
                for (Map.Entry<String, String> entry : roomInfo.getAttributeMap().entrySet()) {
                    preparedStatement.setString(2, entry.getKey());
                    preparedStatement.setString(3, entry.getValue());
                    if (preparedStatement.executeUpdate() < 1) {
                        throw new Exception("房间属性插入失败！");
                    }
                }
                // 插入成员
                preparedStatement = connection.prepareStatement("INSERT INTO r_members (rid,uid_code,role,permission,join_time) VALUES(?,?,?,?,?)");
                for (MemberInfo memberInfo : roomInfo.getMemberInfoMap().values()) {
                    memberInfo.setJoinTime(t);
                    preparedStatement.setInt(1, roomInfo.getRid());
                    preparedStatement.setString(2, memberInfo.getUidCode());
                    preparedStatement.setString(3, memberInfo.getRole());
                    preparedStatement.setInt(4, memberInfo.getPermission());
                    preparedStatement.setLong(5, t);
                    if (preparedStatement.executeUpdate() < 1) {
                        throw new Exception("房间成员插入失败！");
                    }
                }
            } catch (Exception e) {
                LOGGER.error("", e);
                // 回滚事物
                connection.rollback();
                return false;
            }
            // 提交事物
            connection.commit();
        } catch (SQLException e) {
            LOGGER.error("", e);
            return false;
        }
        return true;
    }

    /**
     * 获取用户加入的房间id列表
     *
     * @param uidCode 用户标识码
     * @return 房间id列表
     */
    @Override
    public List<Integer> getRoomIdListByUidCode(String uidCode) {
        List<Integer> ridList = new ArrayList<>();
        try (Connection connection = DataBaseManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT rid FROM r_members WHERE uid_code = ?");
            preparedStatement.setString(1, uidCode);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                ridList.add(resultSet.getInt("rid"));
            }
        } catch (SQLException e) {
            LOGGER.error("", e);
        }
        return ridList;
    }

    /**
     * 获取所有房间id
     *
     * @return 房间id列表
     */
    @Override
    public List<Integer> getRoomIdList() {
        List<Integer> ridList = new ArrayList<>();
        try (Connection connection = DataBaseManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT rid FROM t_rooms");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                ridList.add(resultSet.getInt("rid"));
            }
        } catch (SQLException e) {
            LOGGER.error("", e);
        }
        return ridList;
    }
}
