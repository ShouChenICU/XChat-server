package icu.xchat.server.database;

import icu.xchat.server.exceptions.UnknownDatabaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 数据库管理器
 *
 * @author shouchen
 */
public final class DataBaseManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataBaseManager.class);
    public static final String DB_TYPE_SQLITE = "SQLite";
    private static DataBase dataBase;

    /**
     * 根据类型初始化数据库
     *
     * @param dbType 数据库类型
     */
    public static synchronized void initDataBase(String dbType, String username, String password, String url) throws UnknownDatabaseException {
        if (dataBase != null) {
            LOGGER.warn("重复初始化数据库！");
            return;
        }
        if (DB_TYPE_SQLITE.equalsIgnoreCase(dbType)) {
            initSQLite(username, password, url);
        } else {
            throw new UnknownDatabaseException();
        }
        DaoManager.init();
    }

    /**
     * 初始化SQLite数据库
     */
    private static void initSQLite(String username, String password, String url) {
        dataBase = new DataBaseForSQLite();
        dataBase.initDataBase(username, password, url);
    }

    public static DataBase getDataBase() {
        return dataBase;
    }

    public static Connection getConnection() throws SQLException {
        return dataBase.getConnection();
    }
}
