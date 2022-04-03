package icu.xchat.server;

/**
 * 全局变量
 *
 * @author shouchen
 */
final public class GlobalVariables {
    private static final String[] EDITIONS = new String[]{"Alpha", "Beta", "Release"};
    /**
     * 主版本号
     */
    public static final Integer MAJOR_VERSION = 0;
    /**
     * 副版本号
     */
    public static final Integer MINOR_VERSION = 1;
    /**
     * 修订号
     */
    public static final Integer PATCH_VERSION = 2;
    /**
     * 版本类型
     */
    public static final String EDITION = EDITIONS[0];
    /**
     * 版本全称
     */
    public static final String VERSION_STRING = MAJOR_VERSION + "." + MINOR_VERSION + "." + PATCH_VERSION + "-" + EDITION;
    /**
     * 通讯协议版本
     */
    public static final Integer PROTOCOL_VERSION = 2;
}
