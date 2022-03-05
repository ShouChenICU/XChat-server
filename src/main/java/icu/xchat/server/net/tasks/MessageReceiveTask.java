package icu.xchat.server.net.tasks;

import icu.xchat.server.net.PacketBody;

/**
 * 消息接收处理任务
 *
 * @author shouchen
 */
public class MessageReceiveTask extends AbstractTask {

    public MessageReceiveTask() {
    }

    /**
     * 处理一个包
     *
     * @param packetBody 包
     */
    @Override
    public void handlePacket(PacketBody packetBody) throws Exception {

    }

    /**
     * 起步包
     *
     * @return 第一个发送的包
     */
    @Override
    public PacketBody startPacket() {
        return null;
    }
}
