package dao;

import data.BoardData;
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
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
            preparedStatement.setString(3, PieceType.asData(piece));
            preparedStatement.setString(4, PieceColor.asData(piece));
            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Piece> findByFile(File file) {
        try (Connection connection = getConnection()) {
            List<Piece> pieces = new ArrayList<>();
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT * FROM board WHERE file_column = ?");
            preparedStatement.setInt(1, file.order());
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

    public Optional<Piece> findByPosition(Position position) {
        try (Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT * FROM board WHERE file_column = ? and rank_row = ?");
            preparedStatement.setInt(1, position.file());
            preparedStatement.setInt(2, position.rank());
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

    public List<BoardData> findAll() { // TODO: BoardData -> Piece & naming
        try (Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM board");
            ResultSet resultSet = preparedStatement.executeQuery();

            List<BoardData> boards = new ArrayList<>();
            while (resultSet.next()) {
                int fileColumn = resultSet.getInt("file_column");
                int rankRow = resultSet.getInt("rank_row");
                File file = File.asFile(fileColumn);
                Rank rank = Rank.asRank(rankRow);
                Position position = PositionGenerator.generate(file, rank);

                String pieceType = resultSet.getString("piece_type");
                String pieceColor = resultSet.getString("piece_color");
                Class<? extends Piece> type = PieceType.asType(pieceType);
                Constructor<? extends Piece> constructor = type.getConstructor(Color.class);
                Color color = PieceColor.asColor(pieceColor);
                Piece piece = constructor.newInstance(color);

                boards.add(new BoardData(position, piece));
            }
            return boards;
        } catch (SQLException | NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public int update(Position position, Piece piece) {
        try (Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "UPDATE board SET piece_type = ?, piece_color = ? WHERE file_column = ? and rank_row = ?");
            preparedStatement.setString(1, PieceType.asData(piece));
            preparedStatement.setString(2, PieceColor.asData(piece));
            preparedStatement.setInt(3, position.file());
            preparedStatement.setInt(4, position.rank());
            return preparedStatement.executeUpdate();
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
