package icu.xchat.server.net;

import java.io.IOException;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

/**
 * 网络核心
 *
 * @author shouchen
 */
public class NetCore {
    private static volatile NetCore netCore;
    private ServerSocketChannel serverSocketChannel;
    private Selector mainSelector;

    /**
     * 获取单例
     *
     * @return 实例
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


    }
}
