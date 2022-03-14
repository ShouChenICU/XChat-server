package icu.xchat.server.database.interfaces;

import icu.xchat.server.entities.ChatRoomInfo;

import java.util.List;

/**
 * 聊天室数据访问对象
 *
 * @author shouchen
 */
public interface RoomDao {
    /**
     * 获取一个房间信息
     *
     * @param rid 房间id
     * @return 房间信息实体
     */
    ChatRoomInfo getRoomInfoByRid(int rid);

    /**
     * 获取用户加入的房间id列表
     *
     * @param uidCode 用户标识码
     * @return 房间id列表
     */
    List<Integer> getRoomIdListByUidCode(String uidCode);

    /**
     * 获取所有房间id
     *
     * @return 房间id列表
     */
    List<Integer> getRoomIdList();
}
