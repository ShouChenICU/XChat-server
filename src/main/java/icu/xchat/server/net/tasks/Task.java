package icu.xchat.server.net.tasks;


import icu.xchat.server.net.PacketBody;

/**
 * 传输任务抽象接口
 *
 * @author shouchen
 */
public interface Task {
    /**
     * 获取任务id
     *
     * @return 任务id
     */
    int getTaskId();

    /**
     * 设置任务id
     *
     * @param taskId 任务id
     */
    Task setTaskId(int taskId);

    /**
     * 处理一个包
     *
     * @param packetBody 包
     */
    void handlePacket(PacketBody packetBody) throws Exception;

    /**
     * 起步包
     *
     * @return 第一个发送的包
     */
    PacketBody startPacket();

    /**
     * 终止任务
     */
    void terminate(String errMsg);

    /**
     * 完成任务
     */
    void done();
}
