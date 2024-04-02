package dao.fake;

import dao.BoardDao;
import domain.board.Board;
import domain.piece.Piece;
import domain.position.File;
import domain.position.Position;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

public class FakeBoardDao implements BoardDao {

    private final Map<Integer, Map<Position, Piece>> boards = new HashMap<>();

    @Override
    public int save(int gameId, Position position, Piece piece) {
        HashMap<Position, Piece> squares = new HashMap<>();
        squares.put(position, piece);
        boards.put(gameId, squares);
        return 1;
    }

    @Override
    public int saveAll(int gameId, Board board) {
        int savedCount = 0;
        for (Entry<Position, Piece> entry : board.getSquares().entrySet()) {
            savedCount += save(gameId, entry.getKey(), entry.getValue());
        }
        return savedCount;
    }

    @Override
    public int countAll() {
        return boards.size();
    }

    @Override
    public List<Piece> findPiecesByType(int gameId, Class<? extends Piece> pieceType) {
        Map<Position, Piece> squares = boards.get(gameId);
        return squares.values().stream()
                .filter(piece -> piece.getClass() == pieceType)
                .toList();
    }

    @Override
    public List<Piece> findPiecesByFile(int gameId, File file) {
        Map<Position, Piece> squares = boards.get(gameId);
        return squares.keySet().stream()
                .filter(position -> position.hasFile(file))
                .map(squares::get)
                .toList();
    }

    @Override
    public List<Piece> findPiecesByGame(int gameId) {
        Map<Position, Piece> squares = boards.get(gameId);
        return squares.values().stream().toList();
    }

    @Override
    public Map<Position, Piece> findSquaresByGame(int gameId) {
        Map<Position, Piece> squares = boards.get(gameId);
        return Collections.unmodifiableMap(squares);
    }

    @Override
    public Optional<Piece> findPieceByPosition(int gameId, Position position) {
        Map<Position, Piece> squares = boards.get(gameId);
        if (squares.containsKey(position)) {
            return Optional.of(squares.get(position));
        }
        return Optional.empty();
    }

    @Override
    public int updateByGame(int gameId, Position position, Piece piece) {
        Map<Position, Piece> squares = boards.get(gameId);
        squares.replace(position, piece);
        return 1;
    }

    @Override
    public int deleteByGame(int gameId) {
        Map<Position, Piece> squares = boards.get(gameId);
        int beforeSize = squares.size();
        boards.replace(gameId, new HashMap<>());
        return beforeSize;
    }
}
