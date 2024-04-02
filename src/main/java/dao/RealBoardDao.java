package dao;

import domain.board.Board;
import domain.piece.Bishop;
import domain.piece.Color;
import domain.piece.King;
import domain.piece.Knight;
import domain.piece.None;
import domain.piece.Pawn;
import domain.piece.Piece;
import domain.piece.Queen;
import domain.piece.Rook;
import domain.position.File;
import domain.position.Position;
import domain.position.PositionGenerator;
import domain.position.Rank;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

public class RealBoardDao extends DaoConnection implements BoardDao {

    @Override
    public int save(int gameId, Position position, Piece piece) {
        try (Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO board(file_column, rank_row, piece_type, piece_color, game_id) VALUES(?, ?, ?, ?, ?)");
            preparedStatement.setInt(1, position.file()); // TODO: 여기서는 getter 를 써도 무방한가?
            preparedStatement.setInt(2, position.rank());
            preparedStatement.setString(3, PieceType.asData(piece));
            preparedStatement.setString(4, PieceColor.asData(piece));
            preparedStatement.setInt(5, gameId);
            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
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
    public int countAll() {
        try (Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT COUNT(*) FROM board");
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return Integer.parseInt(resultSet.getString(1));
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Piece> findPiecesByType(int gameId, Class<? extends Piece> pieceType) {
        try (Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT * FROM board WHERE game_id = ? and piece_type = ?");
            preparedStatement.setInt(1, gameId);
            preparedStatement.setString(2, PieceType.asData(pieceType));
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Piece> pieces = new ArrayList<>();
            while (resultSet.next()) {
                String pieceColor = resultSet.getString("piece_color");
                Color color = PieceColor.asColor(pieceColor);
                Constructor<? extends Piece> constructor = pieceType.getConstructor(Color.class);
                Piece piece = constructor.newInstance(color);
                pieces.add(piece);
            }
            return pieces;
        } catch (SQLException | NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Piece> findPiecesByFile(int gameId, File file) {
        try (Connection connection = getConnection()) {
            List<Piece> pieces = new ArrayList<>();
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT * FROM board WHERE game_id = ? and file_column = ?");
            preparedStatement.setInt(1, gameId);
            preparedStatement.setInt(2, file.order());
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String pieceType = resultSet.getString("piece_type");
                String pieceColor = resultSet.getString("piece_color");
                Class<? extends Piece> type = PieceType.asType(pieceType);
                Constructor<? extends Piece> constructor = type.getConstructor(Color.class);
                Color color = PieceColor.asColor(pieceColor);
                pieces.add(constructor.newInstance(color));
            }
            return pieces;
        } catch (SQLException | NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Piece> findPiecesByGame(int gameId) {
        try (Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT * FROM board WHERE game_id = ?");
            preparedStatement.setInt(1, gameId);
            ResultSet resultSet = preparedStatement.executeQuery();

            List<Piece> pieces = new ArrayList<>();
            while (resultSet.next()) {
                String pieceType = resultSet.getString("piece_type");
                String pieceColor = resultSet.getString("piece_color");
                Class<? extends Piece> type = PieceType.asType(pieceType);
                Constructor<? extends Piece> constructor = type.getConstructor(Color.class);
                Color color = PieceColor.asColor(pieceColor);
                Piece piece = constructor.newInstance(color);

                pieces.add(piece);
            }
            return pieces;
        } catch (SQLException | NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Map<Position, Piece> findSquaresByGame(int gameId) { // TODO: 위 메서드와 동일한 쿼리
        try (Connection connection = getConnection()) {
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
                Class<? extends Piece> type = PieceType.asType(pieceType);
                Constructor<? extends Piece> constructor = type.getConstructor(Color.class);
                Color color = PieceColor.asColor(pieceColor);
                Piece piece = constructor.newInstance(color);

                squares.put(PositionGenerator.generate(file, rank), piece);
            }
            return squares;
        } catch (SQLException | NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Piece> findPieceByPosition(int gameId, Position position) {
        try (Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT * FROM board WHERE game_id = ? and file_column = ? and rank_row = ?");
            preparedStatement.setInt(1, gameId);
            preparedStatement.setInt(2, position.file());
            preparedStatement.setInt(3, position.rank());
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String pieceType = resultSet.getString("piece_type");
                String pieceColor = resultSet.getString("piece_color");
                Class<? extends Piece> type = PieceType.asType(pieceType); // TODO: 괜찮은 방법인지 체크 (첫 사용이라서)
                Constructor<? extends Piece> constructor = type.getConstructor(Color.class);
                Color color = PieceColor.asColor(pieceColor);
                return Optional.of(constructor.newInstance(color));
            }
            return Optional.empty();
        } catch (SQLException | NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int updateByGame(int gameId, Position position, Piece piece) {
        try (Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection
                    .prepareStatement(
                            "UPDATE board SET piece_type = ?, piece_color = ? WHERE game_id = ? and file_column = ? and rank_row = ?");
            preparedStatement.setString(1, PieceType.asData(piece));
            preparedStatement.setString(2, PieceColor.asData(piece));
            preparedStatement.setInt(3, gameId);
            preparedStatement.setInt(4, position.file());
            preparedStatement.setInt(5, position.rank());
            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int deleteByGame(int gameId) {
        try (Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection
                    .prepareStatement("DELETE FROM board WHERE game_id = ?");
            preparedStatement.setInt(1, gameId);
            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private enum PieceType {

        BISHOP(Bishop.class, "bishop"),
        ROOK(Rook.class, "rook"),
        QUEEN(Queen.class, "queen"),
        KING(King.class, "king"),
        KNIGHT(Knight.class, "knight"),
        PAWN(Pawn.class, "pawn"),
        NONE(None.class, "none"),
        ;

        private final Class<? extends Piece> type;
        private final String dataOutput;

        PieceType(Class<? extends Piece> type, String dataOutput) {
            this.type = type;
            this.dataOutput = dataOutput;
        }

        private static String asData(Piece piece) {
            return Arrays.stream(values())
                    .filter(pieceType -> pieceType.type == piece.getClass())
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("[DB_ERROR] 저장할 수 없는 데이터입니다."))
                    .dataOutput;
        }

        private static String asData(Class<? extends Piece> type) {
            return Arrays.stream(values())
                    .filter(pieceType -> pieceType.type == type)
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("[DB_ERROR] 저장할 수 없는 데이터입니다."))
                    .dataOutput;
        }

        private static Class<? extends Piece> asType(String dataOutput) {
            return Arrays.stream(values())
                    .filter(pieceType -> pieceType.dataOutput.equals(dataOutput))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("[DB_ERROR] 저장할 수 없는 데이터입니다."))
                    .type;
        }
    }

    private enum PieceColor {

        WHITE(Color.WHITE, "white"),
        BLACK(Color.BLACK, "black"),
        NONE(Color.NONE, "none"),
        ;

        private final Color color;
        private final String dataOutput;

        PieceColor(Color color, String dataOutput) {
            this.color = color;
            this.dataOutput = dataOutput;
        }

        private static String asData(Piece piece) {
            return Arrays.stream(values())
                    .filter(pieceType -> pieceType.color == piece.color())
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("[DB_ERROR] 저장할 수 없는 데이터입니다."))
                    .dataOutput;
        }

        private static Color asColor(String dataOutput) {
            return Arrays.stream(values())
                    .filter(pieceType -> pieceType.dataOutput.equals(dataOutput))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("[DB_ERROR] 저장할 수 없는 데이터입니다."))
                    .color;
        }
    }
}
