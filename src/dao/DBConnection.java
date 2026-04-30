package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/CLB_bia";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "123456";
    private static final Logger LOGGER = Logger.getLogger(DBConnection.class.getName());
     
    private static Connection conn;

    public static Connection getConnection() {
        if (conn == null) {
            try {
                conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                LOGGER.info("Kết nối MySQL thành công!");
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Kết nối thất bại!", e);
            }
        }
        return conn;
    }

    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                if (!conn.isClosed()) {
                    conn.close();
                    LOGGER.info("Đã đóng kết nối!");
                }
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Không thể đóng kết nối!", e);
            }
        }
    }
}