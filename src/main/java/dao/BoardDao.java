package dao;

import data.BoardData;
import domain.piece.Piece;
import domain.position.Position;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import view.mapper.PieceOutput;

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

    public int save(Position position, Piece piece) {
        try (Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection
                    .prepareStatement("INSERT INTO board(file_column, rank_row, piece) VALUES(?, ?, ?)");
            preparedStatement.setInt(1, position.file());
            preparedStatement.setInt(2, position.rank());
            preparedStatement.setString(3, PieceOutput.asOutput(piece));
            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<BoardData> findAll() {
        try (Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM board");
            ResultSet resultSet = preparedStatement.executeQuery();

            List<BoardData> boards = new ArrayList<>();
            while (resultSet.next()) {
                int fileColumn = resultSet.getInt("file_column");
                int rankRow = resultSet.getInt("rank_row");
                String piece = resultSet.getString("piece");
                boards.add(new BoardData(fileColumn, rankRow, piece));
            }
            return boards;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int deleteAll() {
        try (Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM board");
            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
