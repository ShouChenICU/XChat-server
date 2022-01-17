package icu.xchat.server.database;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 数据库接口
 *
 * @author shouchen
 */
public interface DataBase {

    void initDataBase(String username, String password, String url);

    Connection getConnection() throws SQLException;

    DataSource getDataSource();
}
