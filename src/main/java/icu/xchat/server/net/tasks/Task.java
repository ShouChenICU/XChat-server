package icu.xchat.server.net.tasks;

import icu.xchat.server.net.PacketBody;

/**
 * 传输任务抽象类
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
     * 获取任务进度
     *
     * @return 任务进度
     */
    double getProgress();

    /**
     * 处理一个包
     *
     * @param packetBody 数据包
     */
    void handlePacketBody(PacketBody packetBody);
}
