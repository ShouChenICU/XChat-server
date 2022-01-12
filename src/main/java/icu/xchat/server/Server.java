package icu.xchat.server;

import icu.xchat.server.database.DataBaseManager;
import icu.xchat.server.net.NetCore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static void initServer() throws Exception {
        LOGGER.info("初始化配置...");
        configuration = ServerConfiguration.load();
        LOGGER.info("加载服务端密钥对...");
        ServerKeyPairTool.loadKeyPair(configuration.getKeypairType());
        LOGGER.info("密钥对加载完毕");
        LOGGER.info("初始化数据库...");
        DataBaseManager.initDataBase(configuration.getDbType());
        LOGGER.info("数据库初始化完毕");
        LOGGER.info("初始化公共线程池...");
        PublicThreadPool.init(configuration.getThreadPoolSize());
        LOGGER.info("线程池初始化完毕，线程数量：" + configuration.getThreadPoolSize());
        LOGGER.info("初始化网络...");
        netCore = NetCore.getInstance();
        LOGGER.info("网络初始化完毕");
        Runtime.getRuntime().addShutdownHook(new Thread(Server::stop));
    }

    /**
     * 获取服务端配置
     *
     * @return 配置
     */
    public static ServerConfiguration getConfiguration() {
        return configuration;
    }

    /**
     * 获取网络核心
     *
     * @return 网络核心
     */
    public static NetCore getNetCore() {
        return netCore;
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
            LOGGER.info("开始初始化XChat-server...");
            initServer();
            LOGGER.info("XChat-server初始化完毕！");
        } catch (Exception e) {
            LOGGER.error("初始化失败！", e);
            System.exit(-1);
        }
    }

    /**
     * 停止服务端
     */
    public static void stop() {
        LOGGER.info("开始停止XChat-server...");
        netCore.stop();
    }
}
