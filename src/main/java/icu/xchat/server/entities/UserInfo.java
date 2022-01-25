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
     * 状态
     */
    private Integer status;
    /**
     * 修改时间
     */
    private Long timeStamp;
    /**
     * 用户属性
     */
    private Map<String, String> attributeMap;
}
