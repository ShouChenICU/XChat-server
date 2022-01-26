package icu.xchat.server.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

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
        mainSelector = Selector.open();
        dispatchCenter = DispatchCenter.getInstance();

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
        while (isRun) {
            // TODO: 2022/1/4
        }
    }

    public void stop() {
        // TODO: 2022/1/25
        isRun = false;
        try {
            serverSocketChannel.close();
        } catch (IOException e) {
            LOGGER.error("服务端网络通道关闭失败！", e);
        }
        dispatchCenter.stop();
        try {
            mainSelector.close();
        } catch (IOException e) {
            LOGGER.error("主选择器关闭失败！", e);
        }
    }

    public boolean isRun() {
        return isRun;
    }
}
