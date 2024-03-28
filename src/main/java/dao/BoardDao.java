package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class BoardDao {


    private static final String SERVER = "localhost:3306";
    private static final String DATABASE = "chess";
    private static final String OPTION = "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";

    public Connection getConnection() {
        try {
            return DriverManager.getConnection(
                    String.format("jdbc:mysql://%s/%s%s", SERVER, DATABASE, OPTION),
                    USERNAME,
                    PASSWORD);
        } catch (SQLException e) {
            System.out.printf("DB 연결 오류: %s\n", e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
