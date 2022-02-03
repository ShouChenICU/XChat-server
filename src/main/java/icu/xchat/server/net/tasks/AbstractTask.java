package icu.xchat.server.net.tasks;

/**
 * 传输任务抽象类
 *
 * @author shouchen
 */
public abstract class AbstractTask implements Task {
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
