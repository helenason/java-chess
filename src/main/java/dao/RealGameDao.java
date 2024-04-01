package dao;

import domain.board.Turn;
import domain.piece.Color;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Optional;

public class RealGameDao extends DaoConnection implements GameDao {

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
