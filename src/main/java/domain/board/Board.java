package domain.board;

import dao.BoardDao;
import domain.piece.Color;
import domain.piece.None;
import domain.piece.Pawn;
import domain.piece.Piece;
import domain.position.File;
import domain.position.Position;
import domain.position.PositionGenerator;
import domain.position.Rank;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Board {

    // TODO: 도메인과 DAO 분리하기!!
    private final Map<Position, Piece> squares;

    private Board(Map<Position, Piece> squares) {
        this.squares = squares;
    }

    public static Board create() {
        return create(new InitBoardGenerator());
    }

    public static Board create(BoardGenerator boardGenerator) {
        Map<Position, Piece> squares = boardGenerator.generate();
        return new Board(squares);
    } // TODO: 오직 테스트만을 위한 메서드?

    public static Board create(Map<Position, Piece> squares) {
        return new Board(squares);
    }

    private static boolean isBoardNotExist(BoardDao boardDao) {
        return boardDao.countAll() == 0;
    }

    public void reset() {
//        boardDao.delete();
    }

    public Piece findPieceByPosition(File file, Rank rank) {
        return findPieceByPosition(PositionGenerator.generate(file, rank));
    }

    public Piece findPieceByPosition(Position position) {
        return squares.getOrDefault(position, new None(Color.NONE));
    }

    public void movePiece(Position source, Position target) {
        Piece sourcePiece = findPieceByPosition(source);
        squares.replace(target, sourcePiece);
        squares.replace(source, new None(Color.NONE));
    }

    public boolean isBlocked(Position source, Position target) {
        List<Position> betweenPositions = source.betweenPositions(target);
        return betweenPositions.stream()
                .map(this::findPieceByPosition)
                .anyMatch(Piece::isNotBlank);
    }

    public Map<Piece, Integer> findRemainPieces(Color color) {
        Map<Piece, Integer> remainPieces = new HashMap<>();

        List<Piece> allPieces = squares.values().stream().toList();

        allPieces.stream()
                .filter(piece -> piece.hasColor(color))
                .forEach(piece -> {
                    remainPieces.putIfAbsent(piece, 0);
                    remainPieces.computeIfPresent(piece, (key, value) -> value + 1);
                });
        return remainPieces;
    }

    public boolean hasSameColorPawnAtSameFile(Pawn pawn) {
        return Arrays.stream(File.values())
                .anyMatch(file -> countPawnByFileAndColor(file, pawn.color()) > 1);
    }

    private long countPawnByFileAndColor(File file, Color color) {
        return findPiecesByFile(file).stream()
                .filter(piece -> piece.isPawn() && piece.hasColor(color))
                .count();
    }

    private List<Piece> findPiecesByFile(File file) {
        return squares.entrySet().stream()
                .filter(entry -> entry.getKey().hasFile(file))
                .map(Entry::getValue)
                .toList();
    }

    public int countKing() {
        return (int) squares.values().stream()
                .filter(Piece::isKing)
                .count();
    }

    public List<Piece> findKings() {
        return squares.values().stream()
                .filter(Piece::isKing)
                .toList();
    }
}
