package icu.xchat.server.net.tasks;

/**
 * 进度更新回调
 *
 * @author shouchen
 */
public interface ProgressCallBack {
    /**
     * 进度开始
     */
    void startProgress();

    /**
     * 进度更新
     *
     * @param progress 进度
     */
    void updateProgress(double progress);

    /**
     * 完成
     */
    void completeProgress();

    /**
     * 意外终止
     *
     * @param errMsg 错误信息
     */
    void terminate(String errMsg);
}
