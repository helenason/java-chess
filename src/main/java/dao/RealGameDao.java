package dao;

import domain.board.Turn;
import domain.piece.Color;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Optional;

public class RealGameDao implements GameDao {

    private static final String SERVER = "localhost:3306";
    private static final String DATABASE = "chess";
    private static final String OPTION = "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";

    @Override
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

    @Override
    public int save(Turn turn) {
        try (Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection
                    .prepareStatement("INSERT INTO game(turn) VALUES(?)");
            preparedStatement.setString(1, TurnColor.asData(turn));
            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Turn> findTurn() {
        try (Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT turn FROM game");
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String rawTurn = resultSet.getString("turn");
                Turn turn = new Turn(TurnColor.asColor(rawTurn));
                return Optional.of(turn);
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int update(Turn turn) {
        try (Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE game SET turn = ?");
            preparedStatement.setString(1, TurnColor.asData(turn));
            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int delete() {
        try (Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM game");
            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private enum TurnColor {

        WHITE(Color.WHITE, "white"),
        BLACK(Color.BLACK, "black"),
        NONE(Color.NONE, "none"),
        ;

        private final Color color;
        private final String dataOutput;

        TurnColor(Color color, String dataOutput) {
            this.color = color;
            this.dataOutput = dataOutput;
        }

        private static String asData(Turn turn) {
            return Arrays.stream(values())
                    .filter(turnColor -> turnColor.color == turn.color())
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("[DB_ERROR] 저장할 수 없는 데이터입니다."))
                    .dataOutput;
        }

        private static Color asColor(String rawColor) {
            return Arrays.stream(values())
                    .filter(turnColor -> turnColor.dataOutput.equals(rawColor))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("[DB_ERROR] 저장할 수 없는 데이터입니다."))
                    .color;
        }
    }
}
