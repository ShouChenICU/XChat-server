package icu.xchat.server.database.realizations;

import icu.xchat.server.database.interfaces.RoomDao;
import icu.xchat.server.entities.ChatRoomInfo;

import java.util.List;

/**
 * 聊天室数据访问对象实现
 *
 * @author shouchen
 */
public class RoomDaoImpl implements RoomDao {
    /**
     * 获取一个房间信息
     *
     * @param rid 房间id
     * @return 房间信息实体
     */
    @Override
    public ChatRoomInfo getRoomInfoByRid(int rid) {
        return null;
    }

    /**
     * 获取用户加入的房间id列表
     *
     * @param uidCode 用户标识码
     * @return 房间id列表
     */
    @Override
    public List<Integer> getRoomIdListByUidCode(String uidCode) {
        return null;
    }
}
