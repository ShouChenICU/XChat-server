package icu.xchat.server.constants;

/**
 * 任务类型
 *
 * @author shouchen
 */
public class TaskTypes {
    /**
     * 出错
     */
    public static final int ERROR = -1;
    /**
     * 执行命令
     */
    public static final int COMMAND = 0;
    /**
     * 注销
     */
    public static final int LOGOUT = 1;
    /**
     * 传输任务
     */
    public static final int TRANSMIT = 2;
    /**
     * 资源
     */
    public static final int RESOURCE = 3;
    /**
     * 心跳包
     */
    public static final int HEART = 4;
    /**
     * 身份同步
     */
    public static final int IDENTITY_SYNC = 5;
    /**
     * 房间信息同步
     */
    public static final int ROOM_SYNC = 6;
    /**
     * 用户信息同步
     */
    public static final int USER_SYNC = 7;
    /**
     * 消息同步
     */
    public static final int MSG_SYNC = 8;
}
