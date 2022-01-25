package icu.xchat.server.database.entities;

import java.util.Objects;

/**
 * 邮件实体类
 *
 * @author shouchen
 */
public class MailEntity {
    /**
     * 邮件id
     */
    private Integer id;
    /**
     * 发件人
     */
    private Integer senderId;
    /**
     * 收件人
     */
    private Integer receiverId;
    /**
     * 邮件主题
     */
    private String theme;
    /**
     * 邮件内容
     */
    private String content;
    /**
     * 创建时间
     */
    private Long creationTime;

    public Integer getId() {
        return id;
    }

    public MailEntity setId(Integer id) {
        this.id = id;
        return this;
    }

    public Integer getSenderId() {
        return senderId;
    }

    public MailEntity setSenderId(Integer senderId) {
        this.senderId = senderId;
        return this;
    }

    public Integer getReceiverId() {
        return receiverId;
    }

    public MailEntity setReceiverId(Integer receiverId) {
        this.receiverId = receiverId;
        return this;
    }

    public String getTheme() {
        return theme;
    }

    public MailEntity setTheme(String theme) {
        this.theme = theme;
        return this;
    }

    public String getContent() {
        return content;
    }

    public MailEntity setContent(String content) {
        this.content = content;
        return this;
    }

    public Long getCreationTime() {
        return creationTime;
    }

    public MailEntity setCreationTime(Long creationTime) {
        this.creationTime = creationTime;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MailEntity that = (MailEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "MailEntity{" +
                "id=" + id +
                ", senderId=" + senderId +
                ", receiverId=" + receiverId +
                ", theme='" + theme + '\'' +
                ", content='" + content + '\'' +
                ", creationTime=" + creationTime +
                '}';
    }
}
