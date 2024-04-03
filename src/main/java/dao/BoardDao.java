package dao;

import domain.board.Board;
import domain.piece.Piece;
import domain.position.Position;
import java.util.Map;

public interface BoardDao {

    int save(int gameId, Position position, Piece piece);

    int saveAll(int gameId, Board board);

    Map<Position, Piece> findSquaresByGame(int gameId);

    int updateByGame(int gameId, Position position, Piece piece);

    int deleteByGame(int gameId);
}
