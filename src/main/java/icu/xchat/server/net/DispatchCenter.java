package icu.xchat.server.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.channels.SelectionKey;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 调度中心
 *
 * @author shouchen
 */
public class DispatchCenter {
    private static final Logger LOGGER = LoggerFactory.getLogger(DispatchCenter.class);
    private static final ScheduledThreadPoolExecutor timerExecutor;

    static {
        timerExecutor = new ScheduledThreadPoolExecutor(1, runnable -> {
            Thread thread = new Thread(runnable);
            thread.setDaemon(true);
            return thread;
        });
    }

    /**
     * 新的连接
     *
     * @param key 连接键
     */
    public static void newConnect(SelectionKey key) {
        WorkerThreadPool.execute(() -> {
            ConnectGuider connectGuider = new ConnectGuider(key);
            timerExecutor.schedule(() -> {
                try {
                    if (!connectGuider.isDone()) {
                        connectGuider.disconnect();
                    }
                } catch (Exception e) {
                    LOGGER.warn("", e);
                }
            }, 5, TimeUnit.SECONDS);
        });
    }

    /**
     * 心跳检测
     *
     * @param client 客户
     */
    private static void heartTest(Client client) {
        if (System.currentTimeMillis() - client.getHeartTime() > 30000) {
            if (client.isConnect()) {
                // TODO: 2022/4/2  
            }
        } else {
            timerExecutor.schedule(() -> heartTest(client), 10, TimeUnit.SECONDS);
        }
    }
}
