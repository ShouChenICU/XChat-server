package icu.xchat.server.database.realizations;

import icu.xchat.server.database.DataBaseManager;
import icu.xchat.server.database.interfaces.MessageDao;
import icu.xchat.server.entities.MessageInfo;
import icu.xchat.server.utils.CacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 消息数据访问对象实现
 *
 * @author shouchen
 */
public class MessageDaoImpl implements MessageDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageDaoImpl.class);
    private static final CacheManager<Integer, MessageInfo> MESSAGE_INFO_CACHES = new CacheManager<>(TimeUnit.MINUTES.toMillis(3));

    /**
     * 根据id获取消息
     *
     * @param id 消息id
     * @return 消息实体
     */
    @Override
    public MessageInfo getMessageById(int id) {
        MessageInfo messageInfo = MESSAGE_INFO_CACHES.getCache(id);
        if (messageInfo != null) {
            return messageInfo;
        }
        try (Connection connection = DataBaseManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT id,room_id AS rid,sender,type,content,signature,time_stamp FROM t_messages WHERE id = ?");
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                messageInfo = new MessageInfo()
                        .setId(id)
                        .setRid(resultSet.getInt("rid"))
                        .setSender(resultSet.getString("sender"))
                        .setType(resultSet.getInt("type"))
                        .setContent(resultSet.getString("content"))
                        .setSignature(resultSet.getString("signature"))
                        .setTimeStamp(resultSet.getLong("time_stamp"));
            }
        } catch (SQLException e) {
            LOGGER.error("", e);
            messageInfo = null;
        }
        if (messageInfo != null) {
            MESSAGE_INFO_CACHES.putCache(id, messageInfo);
        }
        return messageInfo;
    }

    /**
     * 插入一条消息并自动生成主键id
     *
     * @param messageInfo 消息实体
     * @return 结果
     */
    @Override
    public boolean insertMessage(MessageInfo messageInfo) {
        try (Connection connection = DataBaseManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO t_messages (room_id,sender,type,content,signature,time_stamp) VALUES(?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, messageInfo.getRid());
            preparedStatement.setString(2, messageInfo.getSender());
            preparedStatement.setInt(3, messageInfo.getType());
            preparedStatement.setString(4, messageInfo.getContent());
            preparedStatement.setString(5, messageInfo.getSignature());
            preparedStatement.setLong(6, messageInfo.getTimeStamp());
            if (preparedStatement.executeUpdate() < 1) {
                return false;
            }
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                messageInfo.setId(resultSet.getInt(1));
            }
        } catch (SQLException e) {
            LOGGER.error("", e);
            return false;
        }
        MESSAGE_INFO_CACHES.putCache(messageInfo.getId(), messageInfo);
        return true;
    }

    /**
     * 获取指定最新时间和数量的消息id列表
     *
     * @param time  时间戳
     * @param count 数量
     * @return 消息列表
     */
    @Override
    public List<Integer> getMessageIdListByLatestTimeAndCount(int rid, long time, int count) {
        List<Integer> messageIdList = new ArrayList<>();
        try (Connection connection = DataBaseManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT id FROM t_messages WHERE room_id = ? AND time_stamp < ? AND is_delete = 0 ORDER BY time_stamp LIMIT ?");
            preparedStatement.setInt(1, rid);
            preparedStatement.setLong(2, time);
            preparedStatement.setInt(3, count);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                messageIdList.add(resultSet.getInt("id"));
            }
        } catch (SQLException e) {
            LOGGER.error("", e);
        }
        return messageIdList;
    }
}
