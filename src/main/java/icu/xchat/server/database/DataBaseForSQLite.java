package icu.xchat.server.database;

import icu.xchat.server.Server;
import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteDataSource;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 数据库的SQLite实现
 *
 * @author shouchen
 */
class DataBaseForSQLite extends DataBase {
    private SQLiteDataSource sqLiteDataSource;

    @Override
    public void init() {
        SQLiteConfig config = new SQLiteConfig();
        config.setSynchronous(SQLiteConfig.SynchronousMode.NORMAL);
        config.setJournalMode(SQLiteConfig.JournalMode.TRUNCATE);
        config.setEncoding(SQLiteConfig.Encoding.UTF8);
        config.setTransactionMode(SQLiteConfig.TransactionMode.IMMEDIATE);
        config.enforceForeignKeys(true);
        sqLiteDataSource = new SQLiteDataSource(config);
        sqLiteDataSource.setUrl(Server.getConfiguration().getDbUrl());
    }

    @Override
    public Connection getConnection0() throws SQLException {
        return sqLiteDataSource.getConnection();
    }
}
