package dao;

import data.BoardData;
import domain.piece.Bishop;
import domain.piece.Color;
import domain.piece.King;
import domain.piece.Knight;
import domain.piece.Pawn;
import domain.piece.Piece;
import domain.piece.Queen;
import domain.piece.Rook;
import domain.position.Position;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
                    .prepareStatement(
                            "INSERT INTO board(file_column, rank_row, piece_type, piece_color) VALUES(?, ?, ?, ?)");
            preparedStatement.setInt(1, position.file()); // TODO: 여기서는 getter 를 써도 무방한가?
            preparedStatement.setInt(2, position.rank());
            preparedStatement.setString(3, PieceType.asType(piece));
            preparedStatement.setString(4, PieceColor.asColor(piece));
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
                String pieceType = resultSet.getString("piece_type");
                String pieceColor = resultSet.getString("piece_color");
                boards.add(new BoardData(fileColumn, rankRow, pieceType, pieceColor));
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

    private enum PieceType {

        BISHOP(Bishop.class, "bishop"),
        ROOK(Rook.class, "rook"),
        QUEEN(Queen.class, "queen"),
        KING(King.class, "king"),
        KNIGHT(Knight.class, "knight"),
        PAWN(Pawn.class, "pawn"),
        ;

        private final Class<? extends Piece> type;
        private final String dataOutput;

        PieceType(Class<? extends Piece> type, String dataOutput) {
            this.type = type;
            this.dataOutput = dataOutput;
        }

        public static String asType(Piece piece) {
            return Arrays.stream(values())
                    .filter(pieceType -> pieceType.type == piece.getClass())
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("[DB_ERROR] 저장할 수 없는 데이터입니다."))
                    .dataOutput;
        }
    }

    private enum PieceColor {

        WHITE(Color.WHITE, "white"),
        BLACK(Color.BLACK, "black"),
        ;

        private final Color color;
        private final String dataOutput;

        PieceColor(Color color, String dataOutput) {
            this.color = color;
            this.dataOutput = dataOutput;
        }

        public static String asColor(Piece piece) {
            return Arrays.stream(values())
                    .filter(pieceType -> pieceType.color == piece.color())
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("[DB_ERROR] 저장할 수 없는 데이터입니다."))
                    .dataOutput;
        }
    }
}
