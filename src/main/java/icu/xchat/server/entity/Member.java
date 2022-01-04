package icu.xchat.server.entity;

import java.util.Objects;

/**
 * 成员实体类
 *
 * @author shouchen
 */
public class Member {
    public static final int ROLE_DEFAULT = 0;
    public static final int ROLE_ADMIN = 1;
    public static final int ROLE_OWNER = 2;
    public static final int PERMISSION_UTTER = 1;
    public static final int PERMISSION_FILE_DOWNLOAD = 1 << 1;
    public static final int PERMISSION_FILE_UPLOAD = 1 << 2;
    private int rid;
    private String uidCode;
    private int role;
    private int permission;
    private long joinTime;

    public Member() {
    }

    public int getRid() {
        return rid;
    }

    public void setRid(int rid) {
        this.rid = rid;
    }

    public String getUidCode() {
        return uidCode;
    }

    public void setUidCode(String uidCode) {
        this.uidCode = uidCode;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public int getPermission() {
        return permission;
    }

    public void setPermission(int permission) {
        this.permission = permission;
    }

    public long getJoinTime() {
        return joinTime;
    }

    public void setJoinTime(long joinTime) {
        this.joinTime = joinTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Member member = (Member) o;
        return rid == member.rid && Objects.equals(uidCode, member.uidCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rid, uidCode);
    }
}
