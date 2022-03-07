package icu.xchat.server.entities;

import icu.xchat.server.constants.MemberPermissions;
import icu.xchat.server.constants.MemberRoles;
import icu.xchat.server.constants.RoomAttributes;
import icu.xchat.server.utils.BsonUtils;
import org.bson.BSONObject;
import org.bson.BasicBSONObject;

import java.util.*;

/**
 * 房间信息实体
 *
 * @author shouchen
 */
@SuppressWarnings("unused")
public class ChatRoomInfo implements Serialization {
    /**
     * 房间id
     */
    private Integer rid;
    /**
     * 成员map
     */
    private Map<String, MemberInfo> memberInfoMap;
    /**
     * 属性集
     */
    private Map<String, String> attributeMap;
    /**
     * 创建时间
     */
    private Long creation_time;

    public ChatRoomInfo() {
        this.attributeMap = new HashMap<>();
        this.memberInfoMap = new HashMap<>();
    }

    public Integer getRid() {
        return rid;
    }

    public ChatRoomInfo setRid(Integer rid) {
        this.rid = rid;
        return this;
    }

    public Map<String, MemberInfo> getMemberInfoMap() {
        return Collections.unmodifiableMap(memberInfoMap);
    }

    public ChatRoomInfo setMemberInfoMap(Map<String, MemberInfo> memberInfoMap) {
        this.memberInfoMap = memberInfoMap;
        return this;
    }

    public MemberInfo getMemberInfo(String uidCode) {
        return memberInfoMap.get(uidCode);
    }

    public ChatRoomInfo addMember(MemberInfo memberInfo) {
        this.memberInfoMap.put(memberInfo.getUidCode(), memberInfo);
        return this;
    }

    public Map<String, String> getAttributeMap() {
        return Collections.unmodifiableMap(attributeMap);
    }

    public ChatRoomInfo setAttributeMap(Map<String, String> attributeMap) {
        this.attributeMap = attributeMap;
        return this;
    }

    public String getAttribute(String key) {
        return attributeMap.get(key);
    }

    public ChatRoomInfo setAttribute(String key, String value) {
        this.attributeMap.put(key, value);
        return this;
    }

    public ChatRoomInfo removeAttribute(String key) {
        this.attributeMap.remove(key);
        return this;
    }

    public Long getCreation_time() {
        return creation_time;
    }

    public ChatRoomInfo setCreation_time(Long creation_time) {
        this.creation_time = creation_time;
        return this;
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
        List<byte[]> members = new ArrayList<>();
        for (Map.Entry<String, MemberInfo> entry : memberInfoMap.entrySet()) {
            members.add(entry.getValue().serialize());
        }
        object.put("MEMBERS", members);
        object.put("ATTRIBUTES", attributeMap);
        object.put("CREATION_TIME", creation_time);
        return BsonUtils.encode(object);
    }

    /**
     * 反序列化为对象
     *
     * @param data 数据
     */
    @SuppressWarnings("unchecked")
    @Override
    public void deserialize(byte[] data) {
        BSONObject object = BsonUtils.decode(data);
        this.rid = (Integer) object.get("RID");
        this.memberInfoMap = new HashMap<>();
        List<byte[]> members = (List<byte[]>) object.get("MEMBERS");
        for (byte[] member : members) {
            MemberInfo memberInfo = new MemberInfo();
            memberInfo.deserialize(member);
            this.addMember(memberInfo);
        }
        this.attributeMap = (Map<String, String>) object.get("ATTRIBUTES");
        this.creation_time = (Long) object.get("CREATION_TIME");
    }

    @Override
    public String toString() {
        return "ChatRoomInfo{" +
                "rid=" + rid +
                ", memberInfoMap=" + memberInfoMap +
                ", attributeMap=" + attributeMap +
                ", creation_time=" + creation_time +
                '}';
    }
}
