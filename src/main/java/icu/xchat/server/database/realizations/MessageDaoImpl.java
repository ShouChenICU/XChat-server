package icu.xchat.server.database.realizations;

import icu.xchat.server.database.DataBaseManager;
import icu.xchat.server.database.interfaces.MessageDao;
import icu.xchat.server.entities.MessageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 消息数据访问对象实现
 *
 * @author shouchen
 */
public class MessageDaoImpl implements MessageDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageDaoImpl.class);

    /**
     * 根据id获取消息
     *
     * @param id 消息id
     * @return 消息实体
     */
    @Override
    public MessageInfo getMessageById(int id) {
        MessageInfo messageInfo = null;
        try (Connection connection = DataBaseManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT id,room_id AS rid,sender,type,content,signature,time_stamp FROM t_messages WHERE m.id = ?");
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
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO t_messages (room_id,sender,type,content,signature,time_stamp) VALUES(?,?,?,?,?,?)");
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
                messageInfo.setId(resultSet.getInt("id"));
            }
        } catch (SQLException e) {
            LOGGER.error("", e);
            return false;
        }
        return true;
    }
}
