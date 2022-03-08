package icu.xchat.server.net.tasks;

/**
 * 抽象传输任务
 *
 * @author shouchen
 */
public abstract class AbstractTransmitTask extends AbstractTask {
    /**
     * 创建动作
     */
    protected static final Integer ACTION_CREATE = 1;
    /**
     * 更新动作
     */
    protected static final Integer ACTION_UPDATE = 2;
    /**
     * 传输内容是房间信息
     */
    protected static final Integer TYPE_ROOM_INFO = 11;
    /**
     * 传输内容是用户信息
     */
    protected static final Integer TYPE_USER_INFO = 12;
    /**
     * 传输内容是消息
     */
    protected static final Integer TYPE_MSG_INFO = 13;
    /**
     * 动作类型
     */
    protected int actionType;
    /**
     * 数据类型
     */
    protected int dataType;
    /**
     * 数据内容
     */
    protected byte[] dataContent;
    /**
     * 已处理长度
     */
    protected int processedLength;

    public AbstractTransmitTask() {
        this.processedLength = 0;
    }
}
