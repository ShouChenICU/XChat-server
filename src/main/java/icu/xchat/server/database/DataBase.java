package icu.xchat.server.database;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 数据库抽象类
 *
 * @author shouchen
 */
public abstract class DataBase {
    public static final String DB_TYPE_SQLITE = "sqlite";
    private static DataBase DATA_BASE;

    /**
     * 初始化SQLite数据库
     */
    public static synchronized void initSQLite() {
        if (DATA_BASE == null) {
            return;
        }
        DATA_BASE = new DataBaseForSQLite();
        DATA_BASE.init();
    }

    public static Connection getConnection() throws SQLException {
        return DATA_BASE.getConnection0();
    }

    public abstract void init();

    public abstract Connection getConnection0() throws SQLException;
}
