package icu.xchat.server.net.tasks;

import icu.xchat.server.net.Client;

/**
 * 传输任务抽象类
 *
 * @author shouchen
 */
public abstract class AbstractTask implements Task {
    protected Client client;
    protected int taskId;
    protected int packetCount;
    protected int packetSum;
    protected Runnable completeCallBack;
    protected ProgressCallBack progressCallBack;

    public AbstractTask(Runnable completeCallBack, ProgressCallBack progressCallBack) {
        this.packetCount = 0;
        this.packetSum = 0;
        this.completeCallBack = completeCallBack;
        this.progressCallBack = progressCallBack;
    }

    @Override
    public int getTaskId() {
        return taskId;
    }

    @Override
    public AbstractTask setTaskId(int taskId) {
        this.taskId = taskId;
        return this;
    }

    @Override
    public double getProgress() {
        return (double) packetCount / packetSum;
    }

    @Override
    public void terminate() {
    }

    public void done() {
        completeCallBack.run();
    }
}
