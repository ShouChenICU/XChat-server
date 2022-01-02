package icu.xchat.server.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
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
    private ServerSocketChannel serverSocketChannel;
    private Selector mainSelector;
    private DispatchCenter dispatchCenter;
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
     * 主轮询
     */
    private void mainLoop() {
        while (isRun) {

        }
    }

    public void stop() {
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
