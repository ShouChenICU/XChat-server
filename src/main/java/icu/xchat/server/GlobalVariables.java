package icu.xchat.server;

/**
 * 全局变量
 *
 * @author shouchen
 */
public final class GlobalVariables {
    private static final String[] EDITIONS = new String[]{"Alpha", "Beta", "Release"};
    public static Integer MAJOR_VERSION = 0;
    public static Integer MINOR_VERSION = 0;
    public static Integer PATCH_VERSION = 1;
    public static String EDITION = EDITIONS[0];
    public static String VERSION_STRING = MAJOR_VERSION + "." + MINOR_VERSION + "." + PATCH_VERSION + "-" + EDITION;
}
