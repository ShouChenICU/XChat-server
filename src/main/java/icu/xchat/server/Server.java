package icu.xchat.server;

import icu.xchat.server.net.NetCore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * XChat服务端主类
 *
 * @author shouchen
 */
public final class Server {
    private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);
    private static ServerConfiguration configuration;
    private static NetCore netCore;

    /**
     * 初始化服务端
     */
    private static void initServer() throws IOException {
        netCore = NetCore.getInstance();
    }

    /**
     * 打印标题信息
     */
    private static void printHeadline() {
        System.out.println("XChat-server starting...");
        System.out.println("Version:" + GlobalVariables.VERSION_STRING);
    }

    /**
     * 启动服务端
     */
    public static void start() {
        printHeadline();
        try {
            initServer();
        } catch (IOException e) {
            LOGGER.error("", e);
        }
    }
}
