package icu.xchat.server.database.entity;

/**
 * 聊天室实体类
 *
 * @author shouchen
 */
public class ChatRoomEntity {
    /**
     * 房间id
     */
    private Integer id;
    /**
     * 房间名
     */
    private String topic;
    /**
     * 等级
     */
    private Integer level;
    /**
     * 描述
     */
    private String description;
    /**
     * 创建时间
     */
    private Long createTime;
}
