package icu.xchat.server.database.entities;

import java.util.Objects;

/**
 * 成员实体类
 *
 * @author shouchen
 */
public class MemberEntity {
    /**
     * 房间id
     */
    private Integer roomId;
    /**
     * 用户id
     */
    private Integer uid;
    /**
     * 角色
     */
    private String role;
    /**
     * 权限
     */
    private Integer permissions;
    /**
     * 加入时间
     */
    private Long joinTime;

    public Integer getRoomId() {
        return roomId;
    }

    public MemberEntity setRoomId(Integer roomId) {
        this.roomId = roomId;
        return this;
    }

    public Integer getUid() {
        return uid;
    }

    public MemberEntity setUid(Integer uid) {
        this.uid = uid;
        return this;
    }

    public String getRole() {
        return role;
    }

    public MemberEntity setRole(String role) {
        this.role = role;
        return this;
    }

    public Integer getPermissions() {
        return permissions;
    }

    public MemberEntity setPermissions(Integer permissions) {
        this.permissions = permissions;
        return this;
    }

    public Long getJoinTime() {
        return joinTime;
    }

    public MemberEntity setJoinTime(Long joinTime) {
        this.joinTime = joinTime;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MemberEntity that = (MemberEntity) o;
        return roomId.equals(that.roomId) && uid.equals(that.uid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roomId, uid);
    }

    @Override
    public String toString() {
        return "MemberEntity{" +
                "roomId=" + roomId +
                ", uid=" + uid +
                ", role='" + role + '\'' +
                ", permissions=" + permissions +
                ", joinTime=" + joinTime +
                '}';
    }
}
