package icu.xchat.server.database;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 数据库接口
 *
 * @author shouchen
 */
public interface DataBase {

    void initDataBase();

    Connection getConnection() throws SQLException;
}
