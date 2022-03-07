package icu.xchat.server.entities;

import icu.xchat.server.constants.MemberPermissions;
import icu.xchat.server.constants.MemberRoles;
import icu.xchat.server.utils.BsonUtils;
import org.bson.BSONObject;
import org.bson.BasicBSONObject;

import java.util.Objects;

/**
 * 成员信息类
 *
 * @author shouchen
 */
@SuppressWarnings("unused")
public class MemberInfo implements Serialization {
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

    @Override
    public String toString() {
        return "MemberInfo{" +
                "rid=" + rid +
                ", uidCode='" + uidCode + '\'' +
                ", role='" + role + '\'' +
                ", permission=" + permission +
                ", joinTime=" + joinTime +
                '}';
    }

    /**
     * 对象序列化
     *
     * @return 数据
     */
    @Override
    public byte[] serialize() {
        BSONObject object = new BasicBSONObject();
        object.put("RID", rid);
        object.put("UID_CODE", uidCode);
        object.put("ROLE", role);
        object.put("PERMISSION", permission);
        object.put("JOIN_TIME", joinTime);
        return BsonUtils.encode(object);
    }

    /**
     * 反序列化为对象
     *
     * @param data 数据
     */
    @Override
    public void deserialize(byte[] data) {
        BSONObject object = BsonUtils.decode(data);
        this.rid = (Integer) object.get("RID");
        this.uidCode = (String) object.get("UID_CODE");
        this.role = (String) object.get("ROLE");
        this.permission = (Integer) object.get("PERMISSION");
        this.joinTime = (Long) object.get("JOIN_TIME");
    }
}
