package icu.xchat.server.net;

import icu.xchat.server.entities.ChatRoomInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
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
    private final ConcurrentHashMap<String, Client> loginClientMap;
    private final ConcurrentHashMap<Integer, ChatRoomInfo> chatRoomMap;

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
        loginClientMap = new ConcurrentHashMap<>();
        chatRoomMap = new ConcurrentHashMap<>();
    }

    /**
     * 新客户连接
     *
     * @param channel 网络通道
     */
    public void newClient(SocketChannel channel) throws IOException {
        Client client = new Client(channel);
        synchronized (onlineClientList) {
            onlineClientList.add(client);
        }
        timerExecutor.schedule(() -> {
            if (!client.isLogin()) {
                kick(client, "登陆超时");
                // TODO: 2022/1/31
                //closeClient(client);
            }
        }, 5, TimeUnit.SECONDS);
    }

    /**
     * 关闭一个客户连接
     *
     * @param client 客户
     */
    public void closeClient(Client client) {
        if (client == null) {
            return;
        }
        synchronized (onlineClientList) {
            onlineClientList.remove(client);
            if (client.isLogin()) {
                loginClientMap.remove(client.getUserInfo().getUidCode());
            }
        }
        client.getSelectionKey().cancel();
        try {
            client.getChannel().close();
        } catch (IOException e) {
            LOGGER.warn("", e);
        }
    }

    /**
     * 关闭一个客户连接
     *
     * @param uidCode 用户uid
     */
    public void closeClient(String uidCode) {
        closeClient(loginClientMap.get(uidCode));
    }

    /**
     * t出某用户
     *
     * @param client 用户
     * @param msg    信息
     */
    public void kick(Client client, String msg) {
        if (client == null) {
            return;
        } else if (msg == null) {
            msg = "";
        }
        System.out.println(msg);
        // TODO: 2022/1/27
    }

    /**
     * t出某用户
     *
     * @param uidCode 用户识别码
     * @param msg     信息
     */
    public void kick(String uidCode, String msg) {
        kick(loginClientMap.get(uidCode), msg);
    }

    /**
     * t出所有用户
     *
     * @param msg 信息
     */
    public void kickAll(String msg) {
        synchronized (onlineClientList) {
            Iterator<Client> iterator = onlineClientList.iterator();
            while (iterator.hasNext()) {
                Client client = iterator.next();
                kick(client, msg);
                try {
                    client.getChannel().close();
                } catch (IOException e) {
                    LOGGER.warn("", e);
                }
                iterator.remove();
                if (client.isLogin()) {
                    loginClientMap.remove(client.getUserInfo().getUidCode());
                }
            }
        }
    }

    public void stop() {
        kickAll("服务器终止");
        // TODO: 2022/1/4
    }
}
