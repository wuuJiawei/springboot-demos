package com.j.sample2;

import cn.hutool.core.util.IdUtil;
import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import cn.hutool.db.ds.simple.SimpleDataSource;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Date;

@Slf4j
public class DatabaseHelper {

    private static final String TABLE_NAME = "SendInformation";

    public static void insertData(String sendInfo) {
        Entity entity = new Entity("SendInformation");
        entity.set("SaveTime", new Date());
        entity.set("SendInfo", sendInfo);
        try {
            Db.use(initDataSource()).insert(entity);
        } catch (SQLException e) {
            log.error("Failed to insert data to database: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static Long insertDataForId(String sendInfo) {
        Entity entity = new Entity("SendInformation");
        entity.set("SaveTime", new Date());
        entity.set("SendInfo", sendInfo);
        Long key;
        try {
            key = Db.use(initDataSource()).insertForGeneratedKey(entity);
        } catch (SQLException e) {
            log.error("Failed to insert data to database: {}", e.getMessage());
            throw new RuntimeException(e);
        }
        return key;
    }

//    public static void updateStatus(int id, String status) {
//        String query = "UPDATE " + TABLE_NAME + " SET Status = ? WHERE ID = ?";
//
//        try (Connection connection = getConnection();
//             PreparedStatement statement = connection.prepareStatement(query)) {
//
//            statement.setString(1, status);
//            statement.setInt(2, id);
//
//            statement.executeUpdate();
//        } catch (SQLException e) {
//            log.error("Failed to update status in database: {}", e.getMessage());
//        }
//    }

//    public static void saveException(int id, String exception) {
//        String query = "UPDATE " + TABLE_NAME + " SET Exception = ? WHERE ID = ?";
//
//        try (Connection connection = getConnection();
//             PreparedStatement statement = connection.prepareStatement(query)) {
//
//            statement.setString(1, exception);
//            statement.setInt(2, id);
//
//            statement.executeUpdate();
//        } catch (SQLException e) {
//            log.error("Failed to save exception in database: {}", e.getMessage());
//        }
//    }

    private static Connection getConnection2() throws SQLException {
        String url = ConfigHelper.getProperty("db.url");
        String username = ConfigHelper.getProperty("db.username");
        String password = ConfigHelper.getProperty("db.password");

        return DriverManager.getConnection(url, username, password);
    }

    private static DataSource initDataSource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(ConfigHelper.getProperty("db.url"));
        ds.setUsername(ConfigHelper.getProperty("db.username"));
        ds.setPassword(ConfigHelper.getProperty("db.password"));
        ds.setDriverClassName(ConfigHelper.getProperty("db.driver"));
        return ds;
    }

}
