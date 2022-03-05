package icu.xchat.server.entities;

import java.util.HashMap;
import java.util.Map;

/**
 * 房间信息实体
 *
 * @author shouchen
 */
public class ChatRoomInfo {
    /**
     * 房间id
     */
    private Integer rid;
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
    }

    public Integer getRid() {
        return rid;
    }

    public ChatRoomInfo setRid(Integer rid) {
        this.rid = rid;
        return this;
    }

    public Map<String, String> getAttributeMap() {
        return attributeMap;
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
}
