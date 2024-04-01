package dao.fake;

import dao.BoardDao;
import domain.piece.Piece;
import domain.position.File;
import domain.position.Position;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class FakeBoardDao implements BoardDao {

    private final Map<Position, Piece> squares = new HashMap<>();

    @Override
    public int save(Position position, Piece piece) {
        squares.put(position, piece);
        return 1;
    }

    @Override
    public int countAll() {
        return squares.size();
    }

    @Override
    public List<Piece> findPiecesByType(Class<? extends Piece> pieceType) {
        return squares.values().stream()
                .filter(piece -> piece.getClass() == pieceType)
                .toList();
    }

    @Override
    public List<Piece> findPiecesByFile(File file) {
        return squares.keySet().stream()
                .filter(position -> position.hasFile(file))
                .map(squares::get)
                .toList();
    }

    @Override
    public List<Piece> findAllPieces() {
        return squares.values().stream().toList();
    }

    @Override
    public Optional<Piece> findPieceByPosition(Position position) {
        if (squares.containsKey(position)) {
            return Optional.of(squares.get(position));
        }
        return Optional.empty();
    }

    @Override
    public int update(Position position, Piece piece) {
        squares.replace(position, piece);
        return 1;
    }

    @Override
    public int delete() {
        int beforeSize = squares.size();
        squares.clear();
        return beforeSize;
    }
}
