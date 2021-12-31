package icu.xchat.server;

import java.util.concurrent.*;

/**
 * 公共线程池
 *
 * @author shouchen
 */
public final class PublicThreadPool {
    private static final int MAX_WORK_COUNT = 1024;
    private static ThreadPoolExecutor executor;

    /**
     * 初始化公共线程池
     *
     * @param threadCount 线程数量
     */
    public static void init(int threadCount) {
        executor = new ThreadPoolExecutor(threadCount, threadCount, 0, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(MAX_WORK_COUNT), runnable -> {
            Thread thread = new Thread(runnable);
            thread.setDaemon(true);
            return thread;
        }, new ThreadPoolExecutor.CallerRunsPolicy());
    }

    /**
     * 加入任务
     *
     * @param runnable 待执行任务
     */
    public static void execute(Runnable runnable) {
        executor.execute(runnable);
    }
}
