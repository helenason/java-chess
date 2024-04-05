package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DaoConnection {

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
                    PASSWORD
            );
        } catch (SQLException e) {
            throw new RuntimeException("[DB_ERROR] 데이터베이스 에러입니다. 관리자에게 문의해주세요.");
        }
    }
}
