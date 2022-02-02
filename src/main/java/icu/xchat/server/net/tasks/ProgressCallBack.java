package icu.xchat.server.net.tasks;

/**
 * 进度更新回调
 *
 * @author shouchen
 */
public interface ProgressCallBack {
    /**
     * 进度更新
     *
     * @param progress 进度
     */
    void updateProgress(double progress);
}
