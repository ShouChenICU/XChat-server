package icu.xchat.server.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 数据库管理器
 *
 * @author shouchen
 */
public abstract class DataBaseManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataBaseManager.class);
    public static final String DB_TYPE_SQLITE = "SQLite";
    private static DataBase dataBase;

    /**
     * 根据类型初始化数据库
     *
     * @param dbType 数据库类型
     */
    public static synchronized void initDataBase(String dbType) {
        if (dataBase != null) {
            LOGGER.warn("重复初始化数据库！");
            return;
        }
        if (DB_TYPE_SQLITE.equalsIgnoreCase(dbType)) {
            initSQLite();
        }
    }

    /**
     * 初始化SQLite数据库
     */
    private static void initSQLite() {
        dataBase = new DataBaseForSQLite();
        dataBase.initDataBase();
    }

    public static Connection getConnection() throws SQLException {
        return dataBase.getConnection();
    }
}
