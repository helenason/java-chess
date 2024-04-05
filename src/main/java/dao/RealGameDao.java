package dao;

import dao.mapper.ColorData;
import domain.board.Turn;
import domain.piece.Color;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class RealGameDao implements GameDao {

    private final DaoConnection daoConnection = new DaoConnection();

    @Override
    public int save(Turn turn) {
        try (Connection connection = daoConnection.getConnection()) {
            PreparedStatement preparedStatement = connection
                    .prepareStatement("INSERT INTO game(turn) VALUES(?)", Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, ColorData.asData(turn));
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException("[DB_ERROR] 데이터베이스 에러입니다. 관리자에게 문의해주세요.");
        }
    }

    @Override
    public Map<Integer, Turn> findAll() {
        try (Connection connection = daoConnection.getConnection()) {
            PreparedStatement preparedStatement = connection
                    .prepareStatement("SELECT * FROM game");
            ResultSet resultSet = preparedStatement.executeQuery();
            Map<Integer, Turn> games = new HashMap<>();
            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                String rawTurn = resultSet.getString(2);
                Color color = ColorData.asColor(rawTurn);
                games.put(id, new Turn(color));
            }
            return games;
        } catch (SQLException e) {
            throw new RuntimeException("[DB_ERROR] 데이터베이스 에러입니다. 관리자에게 문의해주세요.");
        }
    }

    @Override
    public Optional<Turn> findTurnById(int id) {
        try (Connection connection = daoConnection.getConnection()) {
            PreparedStatement preparedStatement = connection
                    .prepareStatement("SELECT turn FROM game WHERE game_id = ?");
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String rawTurn = resultSet.getString("turn");
                Turn turn = new Turn(ColorData.asColor(rawTurn));
                return Optional.of(turn);
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("[DB_ERROR] 데이터베이스 에러입니다. 관리자에게 문의해주세요.");
        }
    }

    @Override
    public int updateById(int id, Turn turn) {
        try (Connection connection = daoConnection.getConnection()) {
            PreparedStatement preparedStatement = connection
                    .prepareStatement("UPDATE game SET turn = ? WHERE game_id = ?");
            preparedStatement.setString(1, ColorData.asData(turn));
            preparedStatement.setInt(2, id);
            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("[DB_ERROR] 데이터베이스 에러입니다. 관리자에게 문의해주세요.");
        }
    }

    @Override
    public int deleteById(int id) {
        try (Connection connection = daoConnection.getConnection()) {
            PreparedStatement preparedStatement = connection
                    .prepareStatement("DELETE FROM game WHERE game_id = ?");
            preparedStatement.setInt(1, id);
            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("[DB_ERROR] 데이터베이스 에러입니다. 관리자에게 문의해주세요.");
        }
    }
}
