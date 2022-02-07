package icu.xchat.server.entities;

import java.util.Map;

/**
 * 用户信息
 *
 * @author shouchen
 */
public class UserInfo {
    /**
     * 用户唯一识别码
     */
    private String uidCode;
    /**
     * 经验
     */
    private Integer exp;
    /**
     * 等级
     */
    private Integer level;
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

    public String getUidCode() {
        return uidCode;
    }

    public UserInfo setUidCode(String uidCode) {
        this.uidCode = uidCode;
        return this;
    }

    public Integer getExp() {
        return exp;
    }

    public UserInfo setExp(Integer exp) {
        this.exp = exp;
        return this;
    }

    public Integer getLevel() {
        return level;
    }

    public UserInfo setLevel(Integer level) {
        this.level = level;
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
