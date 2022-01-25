package icu.xchat.server.database.entities;

import java.util.Objects;

/**
 * 消息实体类
 *
 * @author shouchen
 */
public class MessageEntity {
    /**
     * 消息id
     */
    private Integer id;
    /**
     * 发送者id
     */
    private Integer senderId;
    /**
     * 房间id
     */
    private Integer roomId;
    /**
     * 消息类型
     */
    private Integer type;
    /**
     * 消息内容
     */
    private String content;
    /**
     * 签名
     */
    private String sign;
    /**
     * 删除标记
     */
    private Integer deleteMark;
    /**
     * 时间戳
     */
    private Integer timeStamp;

    public Integer getId() {
        return id;
    }

    public MessageEntity setId(Integer id) {
        this.id = id;
        return this;
    }

    public Integer getSenderId() {
        return senderId;
    }

    public MessageEntity setSenderId(Integer senderId) {
        this.senderId = senderId;
        return this;
    }

    public Integer getRoomId() {
        return roomId;
    }

    public MessageEntity setRoomId(Integer roomId) {
        this.roomId = roomId;
        return this;
    }

    public Integer getType() {
        return type;
    }

    public MessageEntity setType(Integer type) {
        this.type = type;
        return this;
    }

    public String getContent() {
        return content;
    }

    public MessageEntity setContent(String content) {
        this.content = content;
        return this;
    }

    public String getSign() {
        return sign;
    }

    public MessageEntity setSign(String sign) {
        this.sign = sign;
        return this;
    }

    public Integer getDeleteMark() {
        return deleteMark;
    }

    public MessageEntity setDeleteMark(Integer deleteMark) {
        this.deleteMark = deleteMark;
        return this;
    }

    public Integer getTimeStamp() {
        return timeStamp;
    }

    public MessageEntity setTimeStamp(Integer timeStamp) {
        this.timeStamp = timeStamp;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MessageEntity that = (MessageEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "MessageEntity{" +
                "id=" + id +
                ", senderId=" + senderId +
                ", roomId=" + roomId +
                ", type=" + type +
                ", content='" + content + '\'' +
                ", sign='" + sign + '\'' +
                ", deleteMark=" + deleteMark +
                ", timeStamp=" + timeStamp +
                '}';
    }
}
