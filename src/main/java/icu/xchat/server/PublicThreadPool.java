package icu.xchat.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

/**
 * 公共线程池
 *
 * @author shouchen
 */
public class PublicThreadPool {
    private static final Logger LOGGER = LoggerFactory.getLogger(PublicThreadPool.class);
    private static final int MAX_WORK_COUNT = 1024;
    private static final String THREAD_NAME = "PublicThread";
    private static ThreadPoolExecutor executor;
    private static volatile int threadNum = 0;

    /**
     * 初始化公共线程池
     *
     * @param threadCount 线程数量
     */
    protected static void init(int threadCount) {
        executor = new ThreadPoolExecutor(threadCount, threadCount, 0, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(MAX_WORK_COUNT), runnable -> {
            Thread thread = new Thread(runnable);
            synchronized (PublicThreadPool.class) {
                thread.setName(THREAD_NAME + "-" + threadNum++);
            }
            thread.setDaemon(true);
            return thread;
        }, new ThreadPoolExecutor.CallerRunsPolicy() {
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
                LOGGER.warn("公共线程池任务数已满！");
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
}
