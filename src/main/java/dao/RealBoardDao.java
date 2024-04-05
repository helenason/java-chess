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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

public class RealBoardDao implements BoardDao {

    private final DaoConnection daoConnection = new DaoConnection();

    @Override
    public int save(int gameId, Position position, Piece piece) {
        try (Connection connection = daoConnection.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO board(file_column, rank_row, piece_type, piece_color, game_id) VALUES(?, ?, ?, ?, ?)");
            preparedStatement.setInt(1, position.file());
            preparedStatement.setInt(2, position.rank());
            preparedStatement.setString(3, PieceType.asData(piece));
            preparedStatement.setString(4, PieceColor.asData(piece));
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
    public Map<Position, Piece> findSquaresByGame(int gameId) {
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
                Color color = PieceColor.asColor(pieceColor);
                Piece piece = PieceType.asType(pieceType, color);
                squares.put(PositionGenerator.generate(file, rank), piece);
            }
            return squares;
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
            preparedStatement.setString(1, PieceType.asData(piece));
            preparedStatement.setString(2, PieceColor.asData(piece));
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

    private enum PieceType {

        BISHOP("bishop", Bishop::new),
        ROOK("rook", Rook::new),
        QUEEN("queen", Queen::new),
        KING("king", King::new),
        KNIGHT("knight", Knight::new),
        PAWN("pawn", Pawn::new),
        NONE("none", None::new),
        ;

        private final String dataOutput;
        private final Function<Color, Piece> pieceGenerator;

        PieceType(String dataOutput, Function<Color, Piece> pieceGenerator) {
            this.dataOutput = dataOutput;
            this.pieceGenerator = pieceGenerator;
        }

        private static String asData(Piece piece) {
            return Arrays.stream(values())
                    .filter(pieceType -> pieceType.pieceGenerator.apply(piece.color()).equals(piece))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("[DB_ERROR] 저장할 수 없는 데이터입니다."))
                    .dataOutput;
        }

        private static Piece asType(String dataOutput, Color color) {
            return Arrays.stream(values())
                    .filter(pieceType -> pieceType.dataOutput.equals(dataOutput))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("[DB_ERROR] 저장할 수 없는 데이터입니다."))
                    .pieceGenerator.apply(color);
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
