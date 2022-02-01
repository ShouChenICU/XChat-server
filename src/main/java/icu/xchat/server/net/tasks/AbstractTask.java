package icu.xchat.server.net.tasks;

import icu.xchat.server.net.PacketBody;

public abstract class AbstractTask implements Task {
    protected int packetId;
    protected int packetSum;
    protected Runnable runnable;

    public AbstractTask(Runnable runnable) {
        this.packetId = 0;
        this.packetSum = 0;
        this.runnable = runnable;
    }

    @Override
    public double getProgress() {
        return (double) packetId / packetSum;
    }

    @Override
    public void done() {
        runnable.run();
    }
}
