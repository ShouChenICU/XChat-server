package icu.xchat.server.database.entities;

import java.util.Objects;

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
     * 根目录id
     */
    private Integer fileId;
    /**
     * 描述
     */
    private String description;
    /**
     * 删除标记
     */
    private Integer deleteMark;
    /**
     * 创建时间
     */
    private Long createTime;

    public Integer getId() {
        return id;
    }

    public ChatRoomEntity setId(Integer id) {
        this.id = id;
        return this;
    }

    public String getTopic() {
        return topic;
    }

    public ChatRoomEntity setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public Integer getFileId() {
        return fileId;
    }

    public ChatRoomEntity setFileId(Integer fileId) {
        this.fileId = fileId;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public ChatRoomEntity setDescription(String description) {
        this.description = description;
        return this;
    }

    public Integer getDeleteMark() {
        return deleteMark;
    }

    public ChatRoomEntity setDeleteMark(Integer deleteMark) {
        this.deleteMark = deleteMark;
        return this;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public ChatRoomEntity setCreateTime(Long createTime) {
        this.createTime = createTime;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatRoomEntity that = (ChatRoomEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ChatRoomEntity{" +
                "id=" + id +
                ", topic='" + topic + '\'' +
                ", fileId=" + fileId +
                ", description='" + description + '\'' +
                ", deleteMark=" + deleteMark +
                ", createTime=" + createTime +
                '}';
    }
}
