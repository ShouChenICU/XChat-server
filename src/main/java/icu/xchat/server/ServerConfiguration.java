package icu.xchat.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Properties;

/**
 * 服务端配置
 *
 * @author shouchen
 */
public final class ServerConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerConfiguration.class);
    private static final String CONF_FILE_NAME = "xchat-server.conf";
    private static final String HOST = "host";
    private static final String DEFAULT_HOST = "localhost";
    private static final String PORT = "port";
    private static final String DEFAULT_PORT = "41321";
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
        configuration.setHost(DEFAULT_HOST);
        configuration.setPort(Integer.parseInt(DEFAULT_PORT));
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
        LOGGER.info("请重新启动服务端！");
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
        try (FileOutputStream fileOutputStream = new FileOutputStream(CONF_FILE_NAME)) {
            properties.store(fileOutputStream, "XChat-server configuration");
        }
    }

    /**
     * 设置服务器主机名
     *
     * @param host 主机名
     */
    public void setHost(String host) {
        properties.setProperty(HOST, host);
    }

    /**
     * 获取服务器主机名
     *
     * @return 服务器主机名
     */
    public String getHost() {
        return properties.getProperty(HOST, DEFAULT_HOST);
    }

    /**
     * 设置服务器端口
     *
     * @param port 端口
     */
    public void setPort(int port) {
        if (port < 1 || port > 65535) {
            port = Integer.parseInt(DEFAULT_PORT);
        }
        properties.setProperty(PORT, String.valueOf(port));
    }

    /**
     * 获取服务器端口
     *
     * @return 服务器端口
     */
    public int getPort() {
        return Integer.parseInt(properties.getProperty(PORT, DEFAULT_PORT));
    }
}
