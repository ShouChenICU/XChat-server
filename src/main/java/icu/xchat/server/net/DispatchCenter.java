package icu.xchat.server.net;

import icu.xchat.server.entities.ChatRoomInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private static volatile DispatchCenter dispatchCenter;
    private final List<Client> onlineClientList;
    private final Map<String, Client> loginClientMap;
    private final Map<Integer, ChatRoomInfo> chatRoomMap;

    static {
        timerExecutor = new ScheduledThreadPoolExecutor(1, runnable -> {
            Thread thread = new Thread(runnable);
            thread.setDaemon(true);
            return thread;
        });
    }

    /**
     * 获取单实例
     *
     * @return 单实例
     */
    public static DispatchCenter getInstance() {
        if (dispatchCenter == null) {
            synchronized (DispatchCenter.class) {
                if (dispatchCenter == null) {
                    dispatchCenter = new DispatchCenter();
                }
            }
        }
        return dispatchCenter;
    }

    private DispatchCenter() {
        onlineClientList = new ArrayList<>();
        loginClientMap = new HashMap<>();
        chatRoomMap = new HashMap<>();
    }

    /**
     * 新客户连接
     *
     * @param channel 网络通道
     */
    public Client newClient(SocketChannel channel) {
        Client client = new Client(channel);
        synchronized (onlineClientList) {
            onlineClientList.add(client);
        }
        timerExecutor.schedule(() -> {
            if (!client.isLogin()) {
                kick(client, "登陆超时");
            }
        }, 5, TimeUnit.SECONDS);
        return client;
    }

    /**
     * t出某用户
     *
     * @param client 用户
     * @param msg    信息
     */
    public void kick(Client client, String msg) {
        // TODO: 2022/1/27
    }

    /**
     * t出某用户
     *
     * @param uidCode 用户识别码
     * @param msg     信息
     */
    public void kick(String uidCode, String msg) {
        // TODO: 2022/1/27
    }

    /**
     * t出所有用户
     *
     * @param msg 信息
     */
    public void kickAll(String msg) {
        synchronized (onlineClientList) {
            for (Client client : onlineClientList) {
                kick(client, msg);
            }
        }
    }

    public void stop() {
        kickAll("服务器终止");
        // TODO: 2022/1/4
    }
}
