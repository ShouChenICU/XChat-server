package icu.xchat.server.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * 网络核心
 *
 * @author shouchen
 */
public class NetCore {
    private static final Logger LOGGER = LoggerFactory.getLogger(NetCore.class);
    private static ServerSocketChannel serverSocketChannel;
    private static Selector mainSelector;
    private static boolean isRun;

    static {
        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            mainSelector = Selector.open();
            serverSocketChannel.register(mainSelector, SelectionKey.OP_ACCEPT);
            isRun = true;
        } catch (Exception e) {
            isRun = false;
            LOGGER.error("", e);
        }
    }

    /**
     * 绑定服务端口
     *
     * @param port 端口
     * @throws IOException IO异常
     */
    public static void bindPort(int port) throws IOException {
        serverSocketChannel.bind(new InetSocketAddress(port));
    }

    /**
     * 主轮询
     */
    public static void mainLoop() {
        Set<SelectionKey> selectedKeys = mainSelector.selectedKeys();
        while (isRun) {
            try {
                mainSelector.select();
            } catch (Exception e) {
                LOGGER.error("", e);
                return;
            }
            Iterator<SelectionKey> keyIterator = selectedKeys.iterator();
            while (keyIterator.hasNext()) {
                SelectionKey key = keyIterator.next();
                keyIterator.remove();
                if (key.isReadable()) {
                    key.interestOps(0);
                    AbstractNetIO netIO = (AbstractNetIO) key.attachment();
                    if (netIO != null) {
                        WorkerThreadPool.execute(() -> {
                                    try {
                                        netIO.doRead();
                                    } catch (Exception e) {
                                        netIO.exceptionHandler(e);
                                    }
                                }
                        );
                    }
                } else if (key.isAcceptable()) {
                    try {
                        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
                        SocketChannel channel = serverChannel.accept();
                        channel.configureBlocking(false);
                        SelectionKey selectionKey = channel.register(mainSelector, 0);
                        DispatchCenter.newConnect(selectionKey);
                    } catch (IOException e) {
                        LOGGER.warn("", e);
                    }
                }
            }
        }
    }

    public static void stop() {
        isRun = false;
        try {
            mainSelector.close();
            serverSocketChannel.close();
        } catch (IOException e) {
            LOGGER.error("", e);
        }
    }

    public static boolean isRun() {
        return isRun;
    }
}
