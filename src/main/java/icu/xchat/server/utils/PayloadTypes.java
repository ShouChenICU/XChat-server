package icu.xchat.server.utils;

/**
 * 传输帧负载类型
 *
 * @author shouchen
 */
public class PayloadTypes {
    /**
     * 执行命令
     */
    public static final int COMMAND = 0;
    /**
     * 登陆
     */
    public static final int LOGIN = 1;
    /**
     * 消息
     */
    public static final int MSG = 2;
    /**
     * 资源
     */
    public static final int RESOURCE = 3;
    /**
     * 心跳包
     */
    public static final int HEART = 4;
}
