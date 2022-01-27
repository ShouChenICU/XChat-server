package icu.xchat.server;

/**
 * 全局变量
 *
 * @author shouchen
 */
final class GlobalVariables {
    private static final String[] EDITIONS = new String[]{"Alpha", "Beta", "Release"};
    /**
     * 主版本号
     */
    public static Integer MAJOR_VERSION = 0;
    /**
     * 副版本号
     */
    public static Integer MINOR_VERSION = 0;
    /**
     * 修订号
     */
    public static Integer PATCH_VERSION = 1;
    /**
     * 版本类型
     */
    public static String EDITION = EDITIONS[0];
    /**
     * 版本全称
     */
    public static String VERSION_STRING = MAJOR_VERSION + "." + MINOR_VERSION + "." + PATCH_VERSION + "-" + EDITION;
    /**
     * 通讯协议版本
     */
    public static Integer PROTOCOL_VERSION = 1;
}
