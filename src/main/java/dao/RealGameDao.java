package dao;

import domain.board.Turn;
import domain.piece.Color;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class RealGameDao extends RealDao implements GameDao {

    @Override
    public int save(Turn turn) {
        try (Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection
                    .prepareStatement("INSERT INTO game(turn) VALUES(?)", Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, TurnColor.asData(turn));
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Map<Integer, Turn> findAll() {
        try (Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection
                    .prepareStatement("SELECT * FROM game");
            ResultSet resultSet = preparedStatement.executeQuery();
            Map<Integer, Turn> games = new HashMap<>();
            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                String rawTurn = resultSet.getString(2);
                Color color = TurnColor.asColor(rawTurn);
                games.put(id, new Turn(color));
            }
            return games;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Turn> findTurnById(int id) {
        try (Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection
                    .prepareStatement("SELECT turn FROM game WHERE game_id = ?");
            preparedStatement.setInt(1, id);
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
    public int updateById(int id, Turn turn) {
        try (Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection
                    .prepareStatement("UPDATE game SET turn = ? WHERE game_id = ?");
            preparedStatement.setString(1, TurnColor.asData(turn));
            preparedStatement.setInt(2, id);
            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int deleteById(int id) {
        try (Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection
                    .prepareStatement("DELETE FROM game WHERE game_id = ?");
            preparedStatement.setInt(1, id);
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
