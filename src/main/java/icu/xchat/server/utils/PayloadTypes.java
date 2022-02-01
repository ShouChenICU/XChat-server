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
    public static final int PAYLOAD_COMMAND = 0;
    /**
     * 创建任务
     */
    public static final int PAYLOAD_TASK = 1;
    /**
     * 公钥
     */
    public static final int PAYLOAD_PUBLIC_KEY = 2;
    /**
     * 密钥
     */
    public static final int PAYLOAD_SECRET_KEY = 3;
    /**
     * 消息
     */
    public static final int PAYLOAD_MSG = 4;
    /**
     * 资源
     */
    public static final int PAYLOAD_RESOURCE = 5;
}
