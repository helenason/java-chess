package dao;

import dao.mapper.ColorData;
import dao.mapper.PieceData;
import domain.board.Board;
import domain.piece.Color;
import domain.piece.Piece;
import domain.position.File;
import domain.position.Position;
import domain.position.PositionGenerator;
import domain.position.Rank;
import dto.BoardData;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class RealBoardDao implements BoardDao {

    private final DaoConnection daoConnection = new DaoConnection();

    @Override
    public int save(int gameId, Position position, Piece piece) {
        try (Connection connection = daoConnection.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO board(file_column, rank_row, piece_type, piece_color, game_id) VALUES(?, ?, ?, ?, ?)");
            preparedStatement.setInt(1, position.file());
            preparedStatement.setInt(2, position.rank());
            preparedStatement.setString(3, PieceData.asData(piece));
            preparedStatement.setString(4, ColorData.asData(piece));
            preparedStatement.setInt(5, gameId);
            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("[DB_ERROR] 데이터베이스 에러입니다. 관리자에게 문의해주세요.");
        }
    }

    @Override
    public int saveAll(int gameId, Board board) {
        int savedCount = 0;
        for (Entry<Position, Piece> square : board.getSquares().entrySet()) {
            savedCount += save(gameId, square.getKey(), square.getValue());
        }
        return savedCount;
    }

    @Override
    public BoardData findSquaresByGame(int gameId) {
        try (Connection connection = daoConnection.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT * FROM board WHERE game_id = ?");
            preparedStatement.setInt(1, gameId);
            ResultSet resultSet = preparedStatement.executeQuery();

            Map<Position, Piece> squares = new HashMap<>();
            while (resultSet.next()) {
                int fileColumn = resultSet.getInt("file_column");
                int rankRow = resultSet.getInt("rank_row");
                File file = File.asFile(fileColumn);
                Rank rank = Rank.asRank(rankRow);

                String pieceType = resultSet.getString("piece_type");
                String pieceColor = resultSet.getString("piece_color");
                Color color = ColorData.asColor(pieceColor);
                Piece piece = PieceData.asType(pieceType, color);
                squares.put(PositionGenerator.generate(file, rank), piece);
            }
            return new BoardData(squares);
        } catch (SQLException e) {
            throw new RuntimeException("[DB_ERROR] 데이터베이스 에러입니다. 관리자에게 문의해주세요.");
        }
    }

    @Override
    public int updateByGame(int gameId, Position position, Piece piece) {
        try (Connection connection = daoConnection.getConnection()) {
            PreparedStatement preparedStatement = connection
                    .prepareStatement(
                            "UPDATE board SET piece_type = ?, piece_color = ? WHERE game_id = ? and file_column = ? and rank_row = ?");
            preparedStatement.setString(1, PieceData.asData(piece));
            preparedStatement.setString(2, ColorData.asData(piece));
            preparedStatement.setInt(3, gameId);
            preparedStatement.setInt(4, position.file());
            preparedStatement.setInt(5, position.rank());
            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("[DB_ERROR] 데이터베이스 에러입니다. 관리자에게 문의해주세요.");
        }
    }

    @Override
    public int deleteByGame(int gameId) {
        try (Connection connection = daoConnection.getConnection()) {
            PreparedStatement preparedStatement = connection
                    .prepareStatement("DELETE FROM board WHERE game_id = ?");
            preparedStatement.setInt(1, gameId);
            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("[DB_ERROR] 데이터베이스 에러입니다. 관리자에게 문의해주세요.");
        }
    }
}
