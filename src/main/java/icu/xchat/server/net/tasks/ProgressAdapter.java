package icu.xchat.server.net.tasks;


/**
 * 任务进度更新适配器
 *
 * @author shouchen
 */
public class ProgressAdapter implements ProgressCallBack {
    /**
     * 进度开始
     */
    @Override
    public void startProgress() {
    }

    /**
     * 进度更新
     *
     * @param progress 进度
     */
    @Override
    public void updateProgress(double progress) {
    }

    /**
     * 完成
     */
    @Override
    public void completeProgress() {
    }

    /**
     * 意外终止
     *
     * @param errMsg 错误信息
     */
    @Override
    public void terminate(String errMsg) {
    }
}
