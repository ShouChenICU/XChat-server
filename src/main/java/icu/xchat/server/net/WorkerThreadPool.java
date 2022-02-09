package icu.xchat.server.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 任务线程池
 *
 * @author shouchen
 */
public class WorkerThreadPool {
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkerThreadPool.class);
    private static final int MAX_WORK_COUNT = 4096;
    private static final String THREAD_NAME = "worker-thread-pool";
    private static ThreadPoolExecutor executor;
    private static volatile int threadNum = 0;

    /**
     * 初始化任务线程池
     *
     * @param threadCount 线程数量
     */
    public static synchronized void init(int threadCount) {
        if (executor != null) {
            LOGGER.warn("线程池重复初始化！");
            return;
        }
        executor = new ThreadPoolExecutor(threadCount, threadCount, 0, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(MAX_WORK_COUNT), runnable -> {
            Thread thread = new Thread(runnable);
            synchronized (WorkerThreadPool.class) {
                thread.setName(THREAD_NAME + "-" + threadNum++);
            }
            thread.setDaemon(true);
            return thread;
        }, new ThreadPoolExecutor.CallerRunsPolicy() {
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
                // TODO: 2022/1/7
                LOGGER.warn("任务线程池任务数已满！");
                super.rejectedExecution(r, e);
            }
        });
    }

    /**
     * 加入任务
     *
     * @param runnable 待执行任务
     */
    public static void execute(Runnable runnable) {
        executor.execute(runnable);
    }

    /**
     * 获取线程池
     *
     * @return 线程池
     */
    public static ThreadPoolExecutor getExecutor() {
        return executor;
    }
}
