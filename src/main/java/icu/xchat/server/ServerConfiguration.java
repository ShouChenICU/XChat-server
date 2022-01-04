package icu.xchat.server;

import icu.xchat.server.database.DataBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * 服务端配置
 *
 * @author shouchen
 */
public final class ServerConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerConfiguration.class);
    private static final String CONF_FILE_NAME = "xchat-server.conf";
    private static final String SERVER_HOST = "server-host";
    private static final String SERVER_PORT = "server-port";
    private static final String DEFAULT_PORT = "41321";
    private static final String PUBLIC_THREAD_POLL_SIZE = "thread-pool-size";
    private static final String DB_TYPE = "db-type";
    private static final String DB_TYPE_DEFAULT = DataBase.DB_TYPE_SQLITE;
    private static final String DB_URL = "db-url";
    private static final String DB_USERNAME = "db-username";
    private static final String DB_PASSWORD = "db-password";
    private final Properties properties;

    /**
     * 从文件加载配置
     *
     * @param configurationFile 配置文件
     * @return 配置
     * @throws IOException 文件不存在
     */
    private static ServerConfiguration loadFromFile(File configurationFile) throws IOException {
        if (!configurationFile.exists()) {
            throw new FileNotFoundException("文件不存在！");
        }
        Properties properties = new Properties();
        try (FileInputStream fileInputStream = new FileInputStream(configurationFile)) {
            properties.load(fileInputStream);
        }
        return new ServerConfiguration(properties);
    }

    /**
     * 生成默认配置
     */
    private static ServerConfiguration generateDefault() {
        ServerConfiguration configuration = new ServerConfiguration(new Properties());
        configuration.setServerHost("");
        configuration.setServerPort(Integer.parseInt(DEFAULT_PORT));
        configuration.setThreadPoolSize(Runtime.getRuntime().availableProcessors());
        configuration.setDbType(DB_TYPE_DEFAULT);
        configuration.setDbUrl("jdbc:sqlite:xchat-server.db");
        configuration.setDbUsername("");
        configuration.setDbPassword("");
        return configuration;
    }

    /**
     * 加载配置
     *
     * @return 配置
     */
    public static ServerConfiguration load() throws Exception {
        ServerConfiguration configuration;
        try {
            LOGGER.info("尝试从文件加载配置...");
            configuration = loadFromFile(new File(CONF_FILE_NAME));
            LOGGER.info("从文件加载配置成功！");
            return configuration;
        } catch (IOException e) {
            LOGGER.warn("从文件加载配置失败！", e);
        }
        LOGGER.info("开始生成默认配置...");
        configuration = generateDefault();
        try {
            configuration.store();
        } catch (IOException e) {
            LOGGER.error("", e);
            throw e;
        }
        LOGGER.info("生成默认配置成功！");
        LOGGER.info("请重新启动XChat-server！");
        throw new Exception();
    }

    private ServerConfiguration(Properties configuration) {
        this.properties = configuration;
    }

    public Properties getProperties() {
        return properties;
    }

    /**
     * 保存配置
     *
     * @throws IOException 文件异常
     */
    public void store() throws IOException {
        try (OutputStreamWriter writer = new FileWriter(CONF_FILE_NAME, StandardCharsets.UTF_8)) {
            writer.write("###  XChat-server 配置清单  ###\n");
            writer.write("# 服务端网络地址\n");
            writer.write(SERVER_HOST + "=" + getServerHost() + "\n\n");
            writer.write("# 服务端网络端口\n");
            writer.write(SERVER_PORT + "=" + getServerPort() + "\n\n");
            writer.write("# 任务线程池大小\n");
            writer.write(PUBLIC_THREAD_POLL_SIZE + "=" + getThreadPoolSize() + "\n\n");
            writer.write("# 数据库类型（当前仅支持sqlite）\n");
            writer.write(DB_TYPE + "=" + getDbType() + "\n\n");
            writer.write("# JDBC链接地址\n");
            writer.write(DB_URL + "=" + getDbUrl() + "\n\n");
            writer.write("# 数据库用户名\n");
            writer.write(DB_USERNAME + "=" + getDbUsername() + "\n\n");
            writer.write("# 数据库密码\n");
            writer.write(DB_PASSWORD + "=" + getDbPassword());
            writer.flush();
        }
    }

    /**
     * 设置服务器主机名
     *
     * @param host 主机名
     */
    public void setServerHost(String host) {
        properties.setProperty(SERVER_HOST, host);
    }

    /**
     * 获取服务器主机名
     *
     * @return 服务器主机名
     */
    public String getServerHost() {
        return properties.getProperty(SERVER_HOST, "");
    }

    /**
     * 设置服务器端口
     *
     * @param port 端口
     */
    public void setServerPort(int port) {
        if (port < 1 || port > 65535) {
            port = Integer.parseInt(DEFAULT_PORT);
        }
        properties.setProperty(SERVER_PORT, String.valueOf(port));
    }

    /**
     * 获取服务器端口
     *
     * @return 服务器端口
     */
    public int getServerPort() {
        int port;
        try {
            port = Integer.parseInt(properties.getProperty(SERVER_PORT, DEFAULT_PORT));
            if (port < 1 || port > 65535) {
                LOGGER.warn("端口范围错误，将使用默认端口：" + DEFAULT_PORT);
                port = Integer.parseInt(DEFAULT_PORT);
            }
        } catch (NumberFormatException e) {
            LOGGER.warn("端口格式错误，将使用默认端口：" + DEFAULT_PORT);
            port = Integer.parseInt(DEFAULT_PORT);
        }
        return port;
    }

    /**
     * 设置线程池大小
     *
     * @param size 线程池大小
     */
    public void setThreadPoolSize(int size) {
        if (size < 1) {
            size = 1;
        }
        properties.setProperty(PUBLIC_THREAD_POLL_SIZE, String.valueOf(size));
    }

    /**
     * 获取线程池大小
     *
     * @return 线程池大小
     */
    public int getThreadPoolSize() {
        int size;
        try {
            size = Integer.parseInt(properties.getProperty(PUBLIC_THREAD_POLL_SIZE));
            if (size < 1) {
                LOGGER.warn("配置的线程数小于1,将使用默认设置！");
                size = 1;
            }
        } catch (NumberFormatException e) {
            LOGGER.warn("线程数格式错误，将使用默认设置！");
            size = Runtime.getRuntime().availableProcessors();
        }
        return size;
    }

    /**
     * 设置数据库类型
     *
     * @param dbType 数据库类型
     */
    public void setDbType(String dbType) {
        properties.setProperty(DB_TYPE, dbType);
    }

    /**
     * 获取数据库类型
     *
     * @return 数据库类型
     */
    public String getDbType() {
        return properties.getProperty(DB_TYPE, DB_TYPE_DEFAULT);
    }

    /**
     * 设置JDBC地址
     *
     * @param url JDBC地址
     */
    public void setDbUrl(String url) {
        properties.setProperty(DB_URL, url);
    }

    /**
     * 获取JDBC地址
     *
     * @return JDBC地址
     */
    public String getDbUrl() {
        return properties.getProperty(DB_URL);
    }

    /**
     * 设置数据库用户名
     *
     * @param username 用户名
     */
    public void setDbUsername(String username) {
        properties.setProperty(DB_USERNAME, username);
    }

    /**
     * 获取数据库用户名
     *
     * @return 用户名
     */
    public String getDbUsername() {
        return properties.getProperty(DB_USERNAME, "");
    }

    /**
     * 设置数据库密码
     *
     * @param password 密码
     */
    public void setDbPassword(String password) {
        properties.setProperty(DB_PASSWORD, password);
    }

    /**
     * 获取数据库密码
     *
     * @return 密码
     */
    public String getDbPassword() {
        return properties.getProperty(DB_PASSWORD, "");
    }
}
