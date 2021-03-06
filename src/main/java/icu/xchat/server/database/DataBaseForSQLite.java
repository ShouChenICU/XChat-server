package icu.xchat.server.database;

import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 数据库的SQLite实现
 *
 * @author shouchen
 */
class DataBaseForSQLite implements DataBase {
    private SQLiteDataSource sqLiteDataSource;

    /**
     * 初始化数据库结构
     */
    private void initSchema() {
        // TODO: 2022/1/4
    }

    @Override
    public void initDataBase(String username, String password, String url) {
        SQLiteConfig config = new SQLiteConfig();
        config.setSynchronous(SQLiteConfig.SynchronousMode.NORMAL);
        config.setJournalMode(SQLiteConfig.JournalMode.TRUNCATE);
        config.setEncoding(SQLiteConfig.Encoding.UTF8);
        config.setTransactionMode(SQLiteConfig.TransactionMode.IMMEDIATE);
        config.enforceForeignKeys(true);
        sqLiteDataSource = new SQLiteDataSource(config);
        sqLiteDataSource.setUrl(url);
        initSchema();
    }

    @Override
    public Connection getConnection() throws SQLException {
        return sqLiteDataSource.getConnection();
    }

    @Override
    public DataSource getDataSource() {
        return sqLiteDataSource;
    }
}
