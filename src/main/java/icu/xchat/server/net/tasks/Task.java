package icu.xchat.server.net.tasks;

import icu.xchat.server.net.PacketBody;

/**
 * 传输任务抽象类
 *
 * @author shouchen
 */
interface Task {
    /**
     * 获取任务进度
     *
     * @return 任务进度
     */
    double getProgress();

    /**
     * 处理一个包
     *
     * @param packetBody 包
     * @return 下一个发送的包，为null则结束任务
     */
    PacketBody handlePacket(PacketBody packetBody) throws Exception;

    /**
     * 任务结束
     */
    void done();
}
