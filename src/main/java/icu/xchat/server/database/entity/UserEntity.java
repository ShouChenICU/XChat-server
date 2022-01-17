package icu.xchat.server.database.entity;

import java.util.Objects;

/**
 * 用户实体类
 *
 * @author shouchen
 */
public class UserEntity {
    /**
     * 用户id
     */
    private Integer uid;
    /**
     * 用户唯一识别码
     */
    private String uidCode;
    /**
     * 用户昵称
     */
    private String nick;
    /**
     * 称号id
     */
    private Integer tid;
    /**
     * 用户等级
     */
    private Integer level;
    /**
     * 经验
     */
    private Integer exp;
    /**
     * 状态
     */
    private Integer status;
    /**
     * 修改时间
     */
    private Long timeStamp;

    public UserEntity() {
    }

    public Integer getUid() {
        return uid;
    }

    public UserEntity setUid(Integer uid) {
        this.uid = uid;
        return this;
    }

    public String getUidCode() {
        return uidCode;
    }

    public UserEntity setUidCode(String uidCode) {
        this.uidCode = uidCode;
        return this;
    }

    public String getNick() {
        return nick;
    }

    public UserEntity setNick(String nick) {
        this.nick = nick;
        return this;
    }

    public Integer getTid() {
        return tid;
    }

    public UserEntity setTid(Integer tid) {
        this.tid = tid;
        return this;
    }

    public Integer getLevel() {
        return level;
    }

    public UserEntity setLevel(Integer level) {
        this.level = level;
        return this;
    }

    public Integer getExp() {
        return exp;
    }

    public UserEntity setExp(Integer exp) {
        this.exp = exp;
        return this;
    }

    public Integer getStatus() {
        return status;
    }

    public UserEntity setStatus(Integer status) {
        this.status = status;
        return this;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public UserEntity setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserEntity that = (UserEntity) o;
        return uid.equals(that.uid) && uidCode.equals(that.uidCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uid, uidCode);
    }

    @Override
    public String toString() {
        return "UserEntity{" +
                "uid=" + uid +
                ", uidCode='" + uidCode + '\'' +
                ", nick='" + nick + '\'' +
                ", tid=" + tid +
                ", level=" + level +
                ", exp=" + exp +
                ", status=" + status +
                ", timeStamp=" + timeStamp +
                '}';
    }
}
