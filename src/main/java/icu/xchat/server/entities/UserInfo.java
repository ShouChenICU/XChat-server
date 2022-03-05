package icu.xchat.server.entities;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户信息
 *
 * @author shouchen
 */
public class UserInfo {
    /**
     * 用户索引id
     */
    private Integer uid;
    /**
     * 用户唯一识别码
     */
    private String uidCode;
    /**
     * 状态
     */
    private Integer status;
    /**
     * 签名
     */
    private String signature;
    /**
     * 修改时间
     */
    private Long timeStamp;
    /**
     * 用户属性
     */
    private Map<String, String> attributeMap;

    public UserInfo() {
        this.attributeMap = new HashMap<>();
    }

    public Integer getUid() {
        return uid;
    }

    public UserInfo setUid(Integer uid) {
        this.uid = uid;
        return this;
    }

    public String getUidCode() {
        return uidCode;
    }

    public UserInfo setUidCode(String uidCode) {
        this.uidCode = uidCode;
        return this;
    }

    public Integer getStatus() {
        return status;
    }

    public UserInfo setStatus(Integer status) {
        this.status = status;
        return this;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public UserInfo setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
        return this;
    }

    public String getSignature() {
        return signature;
    }

    public UserInfo setSignature(String signature) {
        this.signature = signature;
        return this;
    }

    public Map<String, String> getAttributeMap() {
        return attributeMap;
    }

    public UserInfo setAttributeMap(Map<String, String> attributeMap) {
        this.attributeMap = attributeMap;
        return this;
    }
}
