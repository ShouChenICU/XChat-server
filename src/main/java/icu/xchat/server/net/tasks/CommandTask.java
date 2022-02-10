package icu.xchat.server.net.tasks;

import icu.xchat.server.net.PacketBody;

/**
 * 命令执行任务
 *
 * @author shouchen
 */
public class CommandTask extends AbstractTask {
    public CommandTask() {
        // TODO: 2022/2/2
    }

    @Override
    public void handlePacket(PacketBody packetBody) {
    }

    @Override
    public PacketBody startPacket() {
        return null;
    }
}
