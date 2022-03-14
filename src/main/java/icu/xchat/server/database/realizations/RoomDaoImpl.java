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
