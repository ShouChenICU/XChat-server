package icu.xchat.server.net.tasks;

import icu.xchat.server.net.Client;

/**
 * 传输任务抽象类
 *
 * @author shouchen
 */
public abstract class AbstractTask implements Task {
    public static final ProgressCallBack EMPTY_PROGRESS_CALLBACK = new ProgressCallBack() {
        @Override
        public void startProgress() {
        }

        @Override
        public void updateProgress(double progress) {
        }

        @Override
        public void completeProgress() {
        }

        @Override
        public void terminate(String errMsg) {
        }
    };
    protected Client client;
    protected int taskId;
    protected int packetCount;
    protected ProgressCallBack progressCallBack;

    public AbstractTask() {
        this.packetCount = 0;
        this.progressCallBack = EMPTY_PROGRESS_CALLBACK;
    }

    public AbstractTask(ProgressCallBack progressCallBack) {
        this.packetCount = 0;
        this.progressCallBack = progressCallBack;
        progressCallBack.startProgress();
    }

    public AbstractTask setClient(Client client) {
        this.client = client;
        return this;
    }

    public Client getClient() {
        return client;
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
    public void terminate(String errMsg) {
        progressCallBack.terminate(errMsg);
    }

    public void done() {
        progressCallBack.completeProgress();
    }
}
