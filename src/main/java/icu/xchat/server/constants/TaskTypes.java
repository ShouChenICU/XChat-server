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
     * 登陆
     */
    public static final int LOGIN = 1;
    /**
     * 注销
     */
    public static final int LOGOUT = 2;
    /**
     * 传输任务
     */
    public static final int TRANSMIT = 3;
    /**
     * 资源
     */
    public static final int RESOURCE = 4;
    /**
     * 心跳包
     */
    public static final int HEART = 5;
    /**
     * 身份同步
     */
    public static final int IDENTITY_SYNC = 6;
    /**
     * 房间信息同步
     */
    public static final int ROOM_SYNC = 7;
    /**
     * 用户信息同步
     */
    public static final int USER_SYNC = 8;
}
