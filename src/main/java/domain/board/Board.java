package domain.board;

import dao.BoardDao;
import domain.piece.Color;
import domain.piece.King;
import domain.piece.None;
import domain.piece.Piece;
import domain.position.File;
import domain.position.Position;
import domain.position.PositionGenerator;
import domain.position.Rank;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Board {

    private Board(Map<Position, Piece> squares) {
    }

    public static Board create() {
        return create(new InitBoardGenerator());
    }

    public static Board create(BoardGenerator boardGenerator) {
        Map<Position, Piece> squares = boardGenerator.generate();
        return new Board(squares);
    } // TODO: 오직 테스트만을 위한 메서드?

    public Piece findPieceByPosition(File file, Rank rank) {
        return findPieceByPosition(PositionGenerator.generate(file, rank));
    }

    public Piece findPieceByPosition(Position position) {
        BoardDao boardDao = new BoardDao();
        return boardDao.findPieceByPosition(position).orElseGet(() -> new None(Color.NONE));
    }

    public void movePiece(Position source, Position target) {
        Piece sourcePiece = findPieceByPosition(source);
        BoardDao boardDao = new BoardDao();
        boardDao.update(target, sourcePiece);
        boardDao.update(source, new None(Color.NONE));
    }

    public boolean isBlocked(Position source, Position target) {
        List<Position> betweenPositions = source.betweenPositions(target);
        return betweenPositions.stream()
                .map(this::findPieceByPosition)
                .anyMatch(Piece::isNotBlank);
    }

    public Map<Piece, Integer> findRemainPieces(Color color) {
        Map<Piece, Integer> remainPieces = new HashMap<>();

        BoardDao boardDao = new BoardDao();
        List<Piece> allPieces = boardDao.findAllPieces();

        allPieces.stream()
                .filter(piece -> piece.hasColor(color))
                .forEach(piece -> {
                    remainPieces.putIfAbsent(piece, 0);
                    remainPieces.computeIfPresent(piece, (key, value) -> value + 1);
                });
        return remainPieces;
    }

    public boolean hasSameColorPawnAtSameFile(Color color) { // TODO: check naming
        return Arrays.stream(File.values())
                .anyMatch(file -> countPawnByFileAndColor(file, color) > 1);
    }

    private long countPawnByFileAndColor(File file, Color color) {
        return findPiecesByFile(file).stream()
                .filter(piece -> piece.isPawn() && piece.hasColor(color))
                .count();
    }

    private List<Piece> findPiecesByFile(File file) {
        BoardDao boardDao = new BoardDao();
        return boardDao.findPiecesByFile(file);
    }

    public int countKing() {
        BoardDao boardDao = new BoardDao();
        return boardDao.countPiece(King.class);
    }
}
