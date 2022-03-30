package icu.xchat.server.net;

import icu.xchat.server.database.DaoManager;
import icu.xchat.server.entities.MessageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
    private static final List<Client> onlineClientList;
    private static final ConcurrentHashMap<String, Client> loginClientMap;
    private static final ConcurrentHashMap<Integer, ChatRoom> roomMap;

    static {
        onlineClientList = new ArrayList<>();
        loginClientMap = new ConcurrentHashMap<>();
        roomMap = new ConcurrentHashMap<>();
        timerExecutor = new ScheduledThreadPoolExecutor(1, runnable -> {
            Thread thread = new Thread(runnable);
            thread.setDaemon(true);
            return thread;
        });
    }

    /**
     * 加载聊天室
     */
    public static void loadChatRoom() {
        List<Integer> ridList = DaoManager.getRoomDao().getRoomIdList();
        for (int rid : ridList) {
            roomMap.put(rid, new ChatRoom(DaoManager.getRoomDao().getRoomInfoByRid(rid)));
        }
    }

    public static void putChatRoom(ChatRoom chatRoom) {
        roomMap.put(chatRoom.getRid(), chatRoom);
    }

    /**
     * 广播消息
     *
     * @param messageInfo 消息信息
     */
    public static void broadcastMessage(MessageInfo messageInfo) {
        ChatRoom chatRoom = roomMap.get(messageInfo.getRid());
        if (chatRoom != null) {
            chatRoom.broadcastMessage(messageInfo);
        } else {
            LOGGER.warn("异常消息：{}", messageInfo);
        }
    }

    /**
     * 新客户连接
     *
     * @param channel 网络通道
     */
    public static void newClient(SocketChannel channel) throws IOException {
        Client client = new Client(channel);
        synchronized (onlineClientList) {
            onlineClientList.add(client);
        }
        heartTest(client);
        timerExecutor.schedule(() -> {
            if (!client.isLogin()) {
                kick(client, "登陆超时");
                closeClient(client);
            }
        }, 5, TimeUnit.SECONDS);
    }

    /**
     * 将已登陆的用户加入调度器以及房间的在线用户列表中
     *
     * @param client 客户实体
     */
    public static void putLoginClient(Client client) {
        loginClientMap.put(client.getUserInfo().getUidCode(), client);
        for (int rid : client.getRidList()) {
            ChatRoom chatRoom = roomMap.get(rid);
            if (chatRoom != null) {
                chatRoom.putClint(client);
            }
        }
    }

    /**
     * 心跳检测
     *
     * @param client 客户
     */
    private static void heartTest(Client client) {
        if (System.currentTimeMillis() - client.getHeartTime() > 30000) {
            if (client.getChannel().isConnected()) {
                closeClient(client);
            }
        } else {
            timerExecutor.schedule(() -> heartTest(client), 10, TimeUnit.SECONDS);
        }
    }

    /**
     * 关闭一个客户连接
     *
     * @param client 客户
     */
    public static void closeClient(Client client) {
        if (client == null) {
            return;
        }
        synchronized (onlineClientList) {
            onlineClientList.remove(client);
        }
        if (client.isLogin()) {
            loginClientMap.remove(client.getUserInfo().getUidCode());
            for (int rid : client.getRidList()) {
                roomMap.get(rid).removeClient(client.getUserInfo().getUidCode());
            }
        }
        try {
            client.close();
        } catch (IOException e) {
            LOGGER.warn("", e);
        } finally {
            NetCore.wakeup();
        }
    }

    /**
     * 关闭一个客户连接
     *
     * @param uidCode 用户uid
     */
    public static void closeClient(String uidCode) {
        closeClient(loginClientMap.get(uidCode));
    }

    /**
     * t出某用户
     *
     * @param client 用户
     * @param msg    信息
     */
    public static void kick(Client client, String msg) {
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
    public static void kick(String uidCode, String msg) {
        kick(loginClientMap.get(uidCode), msg);
    }

    /**
     * t出所有用户
     *
     * @param msg 信息
     */
    public static void kickAll(String msg) {
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

    public static void stop() {
        kickAll("服务器终止");
        // TODO: 2022/1/4
    }
}
