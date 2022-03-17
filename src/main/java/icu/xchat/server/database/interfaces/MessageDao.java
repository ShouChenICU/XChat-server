package icu.xchat.server.database.interfaces;

import icu.xchat.server.entities.MessageInfo;

import java.util.List;

/**
 * 消息数据访问接口
 *
 * @author shouchen
 */
public interface MessageDao {
    /**
     * 根据id获取消息
     *
     * @param id 消息id
     * @return 消息实体
     */
    MessageInfo getMessageById(int id);

    /**
     * 插入一条消息并自动生成主键id
     *
     * @param messageInfo 消息实体
     * @return 结果
     */
    boolean insertMessage(MessageInfo messageInfo);

    /**
     * 获取指定最新时间和数量的消息id列表
     *
     * @param rid   房间id
     * @param time  时间戳
     * @param count 数量
     * @return 消息列表
     */
    List<Integer> getMessageIdListByLatestTimeAndCount(int rid, long time, int count);
}
