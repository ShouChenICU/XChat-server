package icu.xchat.server.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.util.Set;

/**
 * 网络核心
 *
 * @author shouchen
 */
public class NetCore {
    private static final Logger LOGGER = LoggerFactory.getLogger(NetCore.class);
    private static volatile NetCore netCore;
    private final ServerSocketChannel serverSocketChannel;
    private final Selector mainSelector;
    private final DispatchCenter dispatchCenter;
    private boolean isRun;

    /**
     * 获取单例
     *
     * @return 单实例
     */
    public static NetCore getInstance() throws IOException {
        if (netCore == null) {
            synchronized (NetCore.class) {
                if (netCore == null) {
                    netCore = new NetCore();
                }
            }
        }
        return netCore;
    }

    private NetCore() throws IOException {
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        mainSelector = Selector.open();
        serverSocketChannel.register(mainSelector, SelectionKey.OP_ACCEPT);
        dispatchCenter = DispatchCenter.getInstance();
        isRun = true;
    }

    /**
     * 绑定服务端口
     *
     * @param port 端口
     * @throws IOException IO异常
     */
    public void bindPort(int port) throws IOException {
        serverSocketChannel.bind(new InetSocketAddress(port));
    }

    /**
     * 主轮询
     */
    public void mainLoop() {
        Set<SelectionKey> selectionKeys = mainSelector.selectedKeys();
        while (isRun) {
            try {
                mainSelector.select();
            } catch (IOException e) {
                LOGGER.error(",", e);
                return;
            }
            for (SelectionKey key : selectionKeys) {
                selectionKeys.remove(key);
                if (key.isReadable()) {
                    Client client = (Client) key.attachment();
                    key.cancel();
                    WorkerThreadPool.execute(() -> {
                        try {
                            client.doRead();
                        } catch (Exception e) {
                            LOGGER.warn("", e);
                            dispatchCenter.closeClient(client);
                        }
                    });
                } else if (key.isAcceptable()) {
                    try {
                        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
                        SocketChannel channel = serverChannel.accept();
                        channel.configureBlocking(false);
                        dispatchCenter.newClient(channel);
                    } catch (IOException e) {
                        LOGGER.warn("", e);
                    }
                }
            }
        }
    }

    public SelectionKey register(SocketChannel channel, int ops, Client client) throws ClosedChannelException {
        SelectionKey selectionKey = channel.register(mainSelector, ops, client);
        mainSelector.wakeup();
        return selectionKey;
    }

    public void stop() {
        isRun = false;
        try {
            mainSelector.close();
            serverSocketChannel.close();
        } catch (IOException e) {
            LOGGER.error("", e);
        }
        dispatchCenter.stop();
    }

    public boolean isRun() {
        return isRun;
    }
}
