package icu.xchat.server.entities;

import icu.xchat.server.utils.MemberPermissions;
import icu.xchat.server.utils.MemberRoles;

import java.util.Objects;

/**
 * 成员信息类
 *
 * @author shouchen
 */
@SuppressWarnings("unused")
public class MemberInfo {
    /**
     * 房间id
     */
    private Integer rid;
    /**
     * 用户识别码
     */
    private String uidCode;
    /**
     * 角色
     */
    private String role;
    /**
     * 权限
     */
    private Integer permission;
    /**
     * 加入时间
     */
    private Long joinTime;

    public boolean isOwner() {
        return Objects.equals(MemberRoles.ROLE_OWNER, this.role);
    }

    public boolean isAdmin() {
        return Objects.equals(MemberRoles.ROLE_ADMIN, this.role);
    }

    public boolean isDefault() {
        return Objects.equals(MemberRoles.ROLE_DEFAULT, this.role);
    }

    public boolean canUtter() {
        return (this.permission & MemberPermissions.PERMISSION_UTTER) > 0;
    }

    public boolean canDownloadFile() {
        return (this.permission & MemberPermissions.PERMISSION_FILE_DOWNLOAD) > 0;
    }

    public boolean canUploadFile() {
        return (this.permission & MemberPermissions.PERMISSION_FILE_UPLOAD) > 0;
    }

    public Integer getRid() {
        return rid;
    }

    public MemberInfo setRid(Integer rid) {
        this.rid = rid;
        return this;
    }

    public String getUidCode() {
        return uidCode;
    }

    public MemberInfo setUidCode(String uidCode) {
        this.uidCode = uidCode;
        return this;
    }

    public String getRole() {
        return role;
    }

    public MemberInfo setRole(String role) {
        this.role = role;
        return this;
    }

    public Integer getPermission() {
        return permission;
    }

    public MemberInfo setPermission(Integer permission) {
        this.permission = permission;
        return this;
    }

    public Long getJoinTime() {
        return joinTime;
    }

    public MemberInfo setJoinTime(Long joinTime) {
        this.joinTime = joinTime;
        return this;
    }
}
