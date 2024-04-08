package dao.fake;

import dao.BoardDao;
import domain.board.Board;
import domain.piece.Piece;
import domain.position.Position;
import dto.BoardData;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class FakeBoardDao implements BoardDao {

    private final Map<Integer, Map<Position, Piece>> boards = new HashMap<>();

    @Override
    public int save(int gameId, Position position, Piece piece) {
        Map<Position, Piece> squares = boards.getOrDefault(gameId, new HashMap<>());
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
    public BoardData findSquaresByGame(int gameId) {
        Map<Position, Piece> squares = boards.getOrDefault(gameId, new HashMap<>());
        return new BoardData(squares);
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
        boards.replace(gameId, new HashMap<>());
        return squares.size();
    }
}
